package quality;

import java.util.ArrayList;
import preprocessors.Preprocessor;
import preprocessors.PreprocessorManager;
import training.Trainer;
import weka.core.Instances;
import classification.Classifier;
import filters.FilterCreator;

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
	// List of used filter creators.
	private String[] filterCreatorssList;
	// Classifier type.
	private String classifierType;
	// Username
	private String username;
	// Training set percentage
	private int trainingSetPercentage;
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
	 * Initializes the quality reporter runner from given array of arguments.
	 * 
	 * @param args
	 *            initialization arguments.
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public void Init(String[] args) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		preprocessorsList = readPreprocessorsList(args);
		preprocessors = new PreprocessorManager(preprocessorsList)
				.getPreprocessors();
		filterCreatorssList = readFiltersList(args);
		filterCreators = new ArrayList<FilterCreator>(
				filterCreatorssList.length);
		for (int i = 0; i < filterCreatorssList.length; i++) {
			filterCreators.add((FilterCreator) Class.forName(
					filterCreatorssList[i]).newInstance());
		}

		classifierType = readClassifierType(args);
		username = readUsername(args);
		trainingSetPercentage = readTrainingSet(args);
	}

	/**
	 * Runs a QualityReporter instance and generate the summary report.
	 * 
	 * @throws Exception
	 */
	private void printSummaryReport() throws Exception {
		Trainer trainer = new Trainer(username, classifierType,
				trainingSetPercentage, preprocessors, filterCreators);
		trainer.init();
		Classifier classifier = trainer.trainModel();
		Instances trainingInstances = trainer.getTrainedInstances();
		Instances testingInstances = trainer.getTestInstances();
		QualityReporter reporter = new WekaQualityReporter(trainingInstances);
		// Evaluate classifier.
		reporter.evaluateModel(classifier, testingInstances);
		// Printing summary report.
		System.out.println(reporter.toSummaryString());
		// Printing class details report.
		System.out.println(reporter.toClassDetailsString(""));
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
		args[2] = "svm";
		// Username.
		args[3] = "sanders_r";
		// Training Percentage.
		args[4] = "60";
		QualityReporterRunner repoter = new QualityReporterRunner();
		// Initialize quality reporter runner.
		repoter.Init(args);
		// Print the summary report.
		repoter.printSummaryReport();
	}
}
