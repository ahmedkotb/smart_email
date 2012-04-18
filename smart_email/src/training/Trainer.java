package training;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
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
	FilterManager filterManager;
	// Seed used for shuffling the data.
	private static final long SEED = 1334008424702l;
	// Trained instances
	private Instances trainedInstances;
	// Testing instances
	private Instances testingInstances;

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
		this.preprocessors = preprocessors;
		this.filterCreators = filterCreators;
	}

	public void init() {
		ClassificationManager classifierManager = ClassificationManager
				.getInstance();
		String path = classifierManager.getGoldenDataPath(username);
		DAO dao = DAO.getInstance("FileSystems:" + path);
		ArrayList<String> labels = dao.getClasses();
		testingSet = new ArrayList<Email>();
		trainingSet = new ArrayList<Email>();
		int maximumLimit = classifierManager.getTrainingLimit();
		double trainingSetRatio = trainingPercentage / 100.0;
		for (int i = 0; i < labels.size(); i++) {
			ArrayList<Email> emails = dao.getClassifiedEmails(labels.get(i),
					maximumLimit);
			int testSetStartIndex = (int) Math.ceil(trainingSetRatio
					* emails.size());
//			Collections.shuffle(emails, new Random(SEED));
			Collections.reverse(emails);
			for (int j = 0; j < testSetStartIndex; j++)
				trainingSet.add(emails.get(j));
			for (int j = testSetStartIndex; j < emails.size(); j++)
				testingSet.add(emails.get(j));
		}
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
