package quality;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import preprocessors.Preprocessor;
import preprocessors.PreprocessorManager;
import weka.core.Instances;
import classification.ClassificationManager;
import classification.Classifier;
import datasource.DAO;
import filters.Filter;
import filters.FilterCreator;
import filters.FilterCreatorManager;
import filters.FilterManager;
import general.Email;

/**
 * A class used for running the quality reporter and printing the summary
 * report.
 * 
 * @author Amr Sharaf
 * 
 */
public class QualityReporterRunner {

	// List of used preprocessors.
	private String[] preprocessorsList;
	// List of used filters.
	private String[] filtersList;
	// Classifier type.
	private String classifierType;
	// Username
	private String username;
	// Training set percentage
	private int trainingSetPercentage;
	// Training Set.
	private ArrayList<Email> trainingSet;
	// Testing Set.
	private ArrayList<Email> testingSet;
	// if true, the preprocessors and filterCreators will be loaded through
	// reflections, else i will use ready made instances of them
	private boolean useReflection;
	// Preprocessors Instances to be used
	private ArrayList<Preprocessor> preprocessors;
	// fitlerCreators Instances to be used
	private ArrayList<FilterCreator> filterCreators;
	// Index for the list of preprocessors in the command line arguments.
	private static final int PREPROCESSORS_ID = 0;
	// Index for the list of preprocessors in the command line arguments.
	private static final int FILTERS_ID = 1;
	// Index for the classifier type in the command line arguments.
	private static final int CLASSIFIER_ID = 2;
	// Index for the classifier type in the command line arguments.
	private static final int USERNAME_ID = 3;
	// Index for the training percentage in the command line arguments.
	private static final int PERCENTAGE_ID = 4;
	// Delimiter for separating the preprocessors and filters names.
	private static final String DELIMITER = ",";

	/**
	 * Empty Constructor. Parameters will be initialized using Init(args)
	 * function that uses command line args
	 */
	public QualityReporterRunner() {
		useReflection = true;
	}

	/**
	 * This constructor uses ready made FilterCreators and Preprocessors to pass
	 * them to the FilterCreatorManager and PreprocessorManager, instead of
	 * using reflection to load them. It is mainly used in to run Experiments in
	 * the testing phase
	 * 
	 * @param fitlerCreatorsList
	 *            List of FilterCreators
	 * @param preprcoessorsList
	 *            List of Preprocessor
	 * @param username
	 *            Name of the user upon which we will run the classification
	 * @param classifierType
	 *            Classifier Type
	 * @param trainingSetPrecentage
	 *            Percentage of training set from the users dataset
	 */
	public QualityReporterRunner(ArrayList<FilterCreator> filterCreatorsList,
			ArrayList<Preprocessor> preprocessorsList, String username,
			String classifierType, int trainingSetPrecentage) {
		useReflection = false;
		this.preprocessorsList = null;
		this.filtersList = null;
		this.filterCreators = filterCreatorsList;
		this.preprocessors = preprocessorsList;
		this.username = username;
		this.classifierType = classifierType;
		this.trainingSetPercentage = trainingSetPrecentage;
	}

	/**
	 * Initializes the quality reporter runner from given array of arguments.
	 * 
	 * @param args
	 *            initialization arguments.
	 */
	public void Init(String[] args) {
		preprocessorsList = readPreprocessorsList(args);
		filtersList = readFiltersList(args);
		classifierType = readClassifierType(args);
		username = readUsername(args);
		trainingSetPercentage = readTrainingSet(args);
	}

	/**
	 * Runs a QualityReporter instance and generate the summary report.
	 * 
	 * @throws Exception
	 */
	// TODO this function is deceiving as it does all the work of classification
	// to print the summary, while it's name doesn't indicate that
	private void printSummaryReport() throws Exception {
		QualityReporter reporter = EvaluateClassifer();
		// Printing summary report.
		System.out.println(reporter.toSummaryString());
		// Printing class details report.
		System.out.println(reporter.toClassDetailsString(""));
	}

	public QualityReporter EvaluateClassifer() throws Exception {
		// Prepare evaluation data.
		prepareDataset();

		Classifier classifier = null;
		if (useReflection) {
			classifier = trainUserFromFileSystem(username, classifierType,
					trainingSetPercentage);
		} else {
			classifier = trainUserFromFileSystem(username, classifierType,
					trainingSetPercentage, preprocessors, filterCreators);
		}
		// FilterManager filterMgr =
		// classifierManager.getFilterManager(username);
		Instances dataset = filterMgr.getDataset(trainingSet);
		QualityReporter reporter = new WekaQualityReporter(dataset);
		// Evaluate classifier.
		reporter.evaluateModel(classifier, filterMgr.getDataset(testingSet));

		return reporter;
	}

	private Filter[] initializeUserFilters(String username)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		FilterCreatorManager filterCreatorMgr = new FilterCreatorManager(
				filtersList, trainingSet);
		Filter[] filters = filterCreatorMgr.getFilters();
		filterMgr = new FilterManager(filters);
		return filters;
		// userFilters.put(username, filters);
	}

	public Classifier trainUserFromFileSystem(String username,
			String classifierName, int trainingSetPercentage)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException {
		Filter[] filters = initializeUserFilters(username);

		FilterManager filterMgr = new FilterManager(filters);
		Instances dataset = filterMgr.getDataset(trainingSet);
		PrintWriter pw = new PrintWriter(new FileWriter("training.arff"));
		pw.print(dataset.toString());
		pw.close();
		Classifier classifier = Classifier.getClassifierByName(classifierName,
				null);
		classifier.buildClassifier(dataset);
		return classifier;
	}

	FilterManager filterMgr;

	/**
	 * This function is used to re-train user using a specified
	 * filterCreatorList and preprocessorsList It is used primarily in the
	 * testing phase (Experiments) to test different models to the same user
	 * 
	 * @param username
	 * @param classifierName
	 *            Classifier name to be used in classification
	 * @param trainingSetPercentage
	 *            Percentage of the training set from the user's dataset
	 * @param preprocessorsList
	 *            List of preprocessors
	 * @param filterCreatorsList
	 *            List of FilterCreators
	 * @return returns a trained classifier to be used to classify new emails
	 */
	public Classifier trainUserFromFileSystem(String username,
			String classifierName, int trainingSetPercentage,
			ArrayList<Preprocessor> preprocessorsList,
			ArrayList<FilterCreator> filterCreatorsList) {

		FilterCreatorManager filterCreatorMgr = new FilterCreatorManager(
				filterCreatorsList, trainingSet);
		Filter[] filters = filterCreatorMgr.getFilters();
		// overwriter any previously saved model for this user with the new
		// filters of this re-training
		// userFilters.put(username, filters);

		filterMgr = new FilterManager(filters);
		Instances dataset = filterMgr.getDataset(trainingSet);
		Classifier classifier = Classifier.getClassifierByName(classifierName,
				null);
		classifier.buildClassifier(dataset);
		return classifier;
	}

	/**
	 * Prepares the training and testing set.
	 */
	private void prepareDataset() {
		ClassificationManager classifierManager = ClassificationManager
				.getInstance();
		String path = classifierManager.getGoldenDataPath(username);
		DAO dao = DAO.getInstance("FileSystems:" + path);
		ArrayList<String> labels = dao.getClasses();
		ArrayList<Email> testing = new ArrayList<Email>();
		ArrayList<Email> training = new ArrayList<Email>();
		for (int i = 0; i < labels.size(); i++) {
			ArrayList<Email> emails = dao.getClassifiedEmails(labels.get(i),
					2000);
			double trainingSetRatio = trainingSetPercentage / 100.0;
			int testSetStartIndex = (int) Math.ceil(trainingSetRatio
					* emails.size());
			Collections.shuffle(emails);
			for (int j = 0; j < testSetStartIndex; j++)
				training.add(emails.get(j));
			for (int j = testSetStartIndex; j < emails.size(); j++)
				testing.add(emails.get(j));
		}
		testingSet = testing;
		// testing.toArray(testingSet);
		trainingSet = training;
		// training.toArray(trainingSet);

		PreprocessorManager pm = null;
		if (useReflection) {
			pm = new PreprocessorManager(preprocessorsList);
		} else {
			pm = new PreprocessorManager(preprocessors);
		}
		pm.apply(testingSet);
		pm.apply(trainingSet);
	}

	/**
	 * Returns the array of preprocessors names from the arguments array.
	 * 
	 * @param args
	 * @return
	 */
	private String[] readPreprocessorsList(String[] args) {
		String preprocessors = args[PREPROCESSORS_ID];
		return preprocessors.split(DELIMITER);
	}

	/**
	 * Returns the array of filter names from the arguments array.
	 * 
	 * @param args
	 * @return
	 */
	private String[] readFiltersList(String[] args) {
		String preprocessors = args[FILTERS_ID];
		return preprocessors.split(DELIMITER);
	}

	/**
	 * Returns the classifier type from the array of arguments.
	 * 
	 * @param args
	 * @return
	 */
	private String readClassifierType(String[] args) {
		return args[CLASSIFIER_ID];
	}

	/**
	 * Returns the username from the array of arguments.
	 * 
	 * @param args
	 * @return
	 */
	private String readUsername(String[] args) {
		return args[USERNAME_ID];
	}

	private int readTrainingSet(String[] args) {
		int percentage = Integer.parseInt(args[PERCENTAGE_ID]);
		if (percentage < 0 || percentage > 100)
			throw new IllegalArgumentException();
		return percentage;
	}

	public static void main(String[] args) throws Exception {
		args = new String[5];
		// List of preprocessors.
		args[0] = "preprocessors.Lowercase,preprocessors.NumberNormalization,preprocessors.UrlNormalization"
				+ ",preprocessors.WordsCleaner,preprocessors.StopWordsRemoval,preprocessors.EnglishStemmer";
		// List of filters.
		args[1] =
		// "filters.DateFilterCreator," +
		"filters.SenderFilterCreator," + "filters.WordFrequencyFilterCreator,"
				+ "filters.LabelFilterCreator";
		// Classifier name.
		args[2] = "naiveBayes";
		// Username.
		args[3] = "lokay_m";
		// Training Percentage.
		args[4] = "60";
		QualityReporterRunner repoter = new QualityReporterRunner();
		// Initialize quality reporter runner.
		repoter.Init(args);
		// Print the summary report.
		repoter.printSummaryReport();
	}
}
