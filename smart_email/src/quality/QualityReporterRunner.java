package quality;

import java.util.ArrayList;
import weka.core.Instances;
import classification.ClassificationManager;
import classification.Classifier;
import datasource.DAO;
import filters.Filter;
import filters.FilterCreatorManager;
import filters.FilterManager;
import general.Email;

public class QualityReporterRunner {

	/**
	 * Runs a QualityReporter instance and generate the summary report.
	 * 
	 * @throws Exception
	 */
	private void printSummaryReport() throws Exception {
		String[] preprocessors = new String[] { "preprocessors.Lowercase",
				"preprocessors.NumberNormalization",
				"preprocessors.UrlNormalization", "preprocessors.WordsCleaner",
				"preprocessors.StopWordsRemoval",
				"preprocessors.EnglishStemmer" };
		String[] filterCreatorsNames = new String[] {
				"filters.DateFilterCreator", "filters.SenderFilterCreator",
				"filters.WordFrequencyFilterCreator",
				"filters.LabelFilterCreator" };
		ClassificationManager mgr = new ClassificationManager(
				filterCreatorsNames, preprocessors);

		String path = mgr.getGoldenDataPath("lokay_m");
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
		String username = "lokay_m";
		
		int trainingSetPercentage = 60;
		
		Classifier classifier = mgr.trainUserFromFileSystem(username,
				"svm", trainingSetPercentage);

		Filter[] filters;
		FilterCreatorManager filterCreatorMgr = new FilterCreatorManager(
				filterCreatorsNames, trainingSet);
		filters = filterCreatorMgr.getFilters();
		FilterManager filterMgr = new FilterManager(filters);
		Instances dataset = filterMgr.getDataset(trainingSet);
		QualityReporter reporter = new WekaQualityReporter(dataset);
		reporter.evaluateModel(classifier, filterMgr.getDataset(testingSet));
		System.out.println(reporter.toSummaryString());
		System.out.println(reporter.toClassDetailsString(""));
	}

	public static void main(String[] args) throws Exception {
		new QualityReporterRunner().printSummaryReport();
	}
}
