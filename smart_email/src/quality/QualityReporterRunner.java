package quality;

import java.util.ArrayList;

import weka.core.Instances;
import classification.ClassificationManager;
import classification.Classifier;
import datasource.DAO;
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
	// Index for the list of preprocessors in the command line arguments.
	private static final int PREPROCESSORS_ID = 0;
	// Index for the list of preprocessors in the command line arguments.
	private static final int FILTERS_ID = 1;
	// Index for the classifier type in the command line arguments.
	private static final int CLASSIFIER_ID = 2;
	// Index for the classifier type in the command line arguments.
	private static final int USERNAME_ID = 3;
	// Delimiter for separating the preprocessors and filters names.
	private static final String DELIMITER = ",";

	public void Init(String[] args) {
		preprocessorsList = readPreprocessorsList(args);
		filtersList = readFiltersList(args);
		classifierType = readClassifierType(args);
		username = readUsername(args);
	}

	/**
	 * Runs a QualityReporter instance and generate the summary report.
	 * 
	 * @throws Exception
	 */
	private void printSummaryReport() throws Exception {
		ClassificationManager classifierManager = new ClassificationManager(
				filtersList, preprocessorsList);
		String path = classifierManager.getGoldenDataPath(username);
		DAO dao = DAO.getInstance("FileSystems:" + path);
		ArrayList<String> labels = dao.getClasses();
		ArrayList<Email> testing = new ArrayList<Email>();
		ArrayList<Email> training = new ArrayList<Email>();
		for (int i = 0; i < labels.size(); i++) {
			Email[] emails = dao.getClassifiedEmails(labels.get(i), 2000);
			double trainingSetRatio = 60 / 100.0;
			int testSetStartIndex = (int) Math.ceil(trainingSetRatio
					* emails.length);
			for (int j = 0; j < testSetStartIndex; j++)
				training.add(emails[j]);
			for (int j = testSetStartIndex; j < emails.length; j++)
				testing.add(emails[j]);
		}
		Email[] testingSet;
		testingSet = new Email[testing.size()];
		testing.toArray(testingSet);
		Email[] trainingSet;
		trainingSet = new Email[training.size()];
		training.toArray(trainingSet);
		int trainingSetPercentage = 60;

		Classifier classifier = classifierManager.trainUserFromFileSystem(
				username, classifierType, trainingSetPercentage);

		FilterManager filterMgr = classifierManager.getFilterManager(username);
		Instances dataset = filterMgr.getDataset(trainingSet);
		QualityReporter reporter = new WekaQualityReporter(dataset);
		reporter.evaluateModel(classifier, filterMgr.getDataset(testingSet));
		// Printing summary report.
		System.out.println(reporter.toSummaryString());
		// Printing class details report.
		System.out.println(reporter.toClassDetailsString(""));
	}

	private String[] readPreprocessorsList(String[] args) {
		String preprocessors = args[PREPROCESSORS_ID];
		return preprocessors.split(DELIMITER);
	}

	private String[] readFiltersList(String[] args) {
		String preprocessors = args[FILTERS_ID];
		return preprocessors.split(DELIMITER);
	}

	private String readClassifierType(String[] args) {
		return args[CLASSIFIER_ID];
	}

	private String readUsername(String[] args) {
		return args[USERNAME_ID];
	}

	public static void main(String[] args) throws Exception {
		QualityReporterRunner repoter = new QualityReporterRunner();
		repoter.Init(args);
		repoter.printSummaryReport();
	}
}
