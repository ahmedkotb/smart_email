package training;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

import javax.mail.MessagingException;

import junit.framework.Assert;
import classification.ClassificationManager;
import classification.Classifier;
import datasource.DAO;
import preprocessors.Preprocessor;
import preprocessors.PreprocessorManager;
import weka.core.Instances;
import filters.Filter;
import filters.FilterCreator;
import filters.FilterCreatorManager;
import filters.FilterManager;
import general.Email;

public class Trainer implements TrainerIF {

	// User for which the model will be built.
	private String username;
	// Classifier type.
	private String classifierType;
	// Training set percentage
	private int trainingPercentage;
	// Training Set.
	private ArrayList<Email> trainingSet;
	// Testing Set.
	private ArrayList<Email> testingSet;
	// Preprocessors used for processing data.
	private ArrayList<Preprocessor> preprocessors;
	// Filter creators used.
	private ArrayList<FilterCreator> filterCreators;
	// Filter manager
	private FilterManager filterManager;
	// Trained instances
	private Instances trainedInstances;
	// Testing instances
	private Instances testingInstances;
	// Partition size for training with the first x * N emails and testing with the following N
	private int N;
	// Number of partitions of size N to train with
	private int K;
	// training type, either using percentage or K*N emails
	private TrainingType trainingType;

	/**
	 * Constructor for creating a new Trainer instance.
	 * 
	 * @param username
	 *            User to be trained.
	 * @param classifierType
	 *            Classifier type.
	 * @param trainingPercentage
	 *            training percentage.
	 * @param preprocessorManager
	 *            processing manager.
	 * @param filterCreatorManager
	 *            filter creator manager.
	 */
	public Trainer(String username, String classifierType,
			int trainingPercentage, ArrayList<Preprocessor> preprocessors,
			ArrayList<FilterCreator> filterCreators) {
		this.username = username;
		this.classifierType = classifierType;
		this.trainingPercentage = trainingPercentage;
		this.trainingType = TrainingType.PERCENTAGE;
		this.preprocessors = preprocessors;
		this.filterCreators = filterCreators;
	}

	/**
	 * Constructor for creating a new Trainer instance.
	 * 
	 * @param username
	 *            User to be trained.
	 * @param classifierType
	 *            Classifier type.
	 * @param trainingPercentage
	 *            training percentage.
	 * @param preprocessorManager
	 *            processing manager.
	 * @param filterCreatorManager
	 *            filter creator manager.
	 */
	public Trainer(String username, String classifierType,
			int N, int K, ArrayList<Preprocessor> preprocessors,
			ArrayList<FilterCreator> filterCreators) {
		this.username = username;
		this.classifierType = classifierType;
		this.N = N;
		this.K = K;
		if(N==0 || K==0)
			throw new IllegalArgumentException("N and K parameters for training can't be 0");
		
		this.trainingType = TrainingType.KN_PARTITIONS;
		this.preprocessors = preprocessors;
		this.filterCreators = filterCreators;
	}

	private void loadDatasetWithPartitions(DAO dao, int maximumLimit){
		ArrayList<String> labels = dao.getClasses();
		testingSet = new ArrayList<Email>();
		trainingSet = new ArrayList<Email>();
		
		PriorityQueue<Email> emails = new PriorityQueue<Email>();
		
		for (int i = 0; i < labels.size(); i++) {
			ArrayList<Email> labelEmails = dao.getClassifiedEmails(labels.get(i),
					maximumLimit);
			Collections.reverse(labelEmails);
			
			int end = Math.min((K+1)*N, labelEmails.size());
							
			for (int j = 0; j < end; j++)
				emails.add(labelEmails.get(j));
		}
		
		if(emails.size() < (K+1)*N)
			throw new IllegalArgumentException("Number of emails for training and testing (" + emails.size() + ")is < (k+1)*N (" + ((K+1)*N) + ")");
		
		HashSet<String> trainedLabels = new HashSet<String>();
		for(int i=0; i<K*N; i++){
			trainingSet.add(emails.poll());
			try {
				trainedLabels.add(trainingSet.get(i).getHeader("X-label")[0]);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		
		for(int i=0; i<N; i++){
			Email mail = emails.poll();
			try {
				if(trainedLabels.contains(mail.getHeader("X-label")[0]))
					testingSet.add(mail);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		
		try {
			System.err.println(trainingSet.get(0).getSentDate() + ", " + trainingSet.get(1).getSentDate());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadDatasetWithPercentage(DAO dao, int maximumLimit){
		ArrayList<String> labels = dao.getClasses();
		testingSet = new ArrayList<Email>();
		trainingSet = new ArrayList<Email>();
		
		double trainingSetRatio = trainingPercentage / 100.0;
		for (int i = 0; i < labels.size(); i++) {
			ArrayList<Email> emails = dao.getClassifiedEmails(labels.get(i),
					maximumLimit);
			int testSetStartIndex = (int) Math.ceil(trainingSetRatio
					* emails.size());
			Collections.reverse(emails);
			for (int j = 0; j < testSetStartIndex; j++)
				trainingSet.add(emails.get(j));
			for (int j = testSetStartIndex; j < emails.size(); j++)
				testingSet.add(emails.get(j));
		}		
	}
	
	private void loadDataset(DAO dao, int maximumLimit ){
		if(trainingType == TrainingType.PERCENTAGE)
			loadDatasetWithPercentage(dao, maximumLimit);
		else if(trainingType == TrainingType.KN_PARTITIONS)
			loadDatasetWithPartitions(dao, maximumLimit);
		else
			throw new IllegalArgumentException("TrainingType is not set");
	}
	
	public void init() {
		ClassificationManager classifierManager = ClassificationManager
				.getInstance();
		String path = classifierManager.getGoldenDataPath(username);
		DAO dao = DAO.getInstance("FileSystems:" + path);
		int maximumLimit = classifierManager.getTrainingLimit();
		
		loadDataset(dao, maximumLimit);
		
		PreprocessorManager preprocessorManager = new PreprocessorManager(
				preprocessors);
		preprocessorManager.apply(testingSet);
		preprocessorManager.apply(trainingSet);
	}

	public Classifier trainModel() {
		Assert.assertNotNull(
				"Training data is empty. Did you run init() method?",
				trainingSet);
		FilterCreatorManager filterCreatorManager = new FilterCreatorManager(
				filterCreators, trainingSet);
		Filter[] filters = filterCreatorManager.getFilters();
		filterManager = new FilterManager(filters);
		trainedInstances = filterManager.getDataset(trainingSet);
		Classifier classifier = Classifier.getClassifierByName(classifierType,
				null);
		classifier.buildClassifier(trainedInstances);
		return classifier;
	}

	public Instances getTrainedInstances() {
		// Check that the trainModel() method was called.
		Assert.assertNotNull(
				"No trained instances. Did you run trainModel() method?",
				trainedInstances);
		return trainedInstances;
	}

	public Instances getTestInstances() {
		// Check that the trainModel() method was called.
		Assert.assertNotNull(
				"Filter manager not set. Did you run trainModel() method?",
				filterManager);
		// If the function was called for the first time, cash the testing
		// instances in the testingInstances attribute for future use.
		if (testingInstances == null) {
			testingInstances = filterManager.getDataset(testingSet);
		}
		// Return the testing instances.
		return testingInstances;
	}
}
