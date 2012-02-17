package quality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import classification.ClassificationManager;
import classification.Classifier;
import datasource.DAO;
import filters.Filter;
import filters.FilterCreatorManager;
import filters.FilterManager;
import general.Email;

/**
 * A class used for running the quality reporter and printing
 * the summary report.
 * 
 * @author Amr Sharaf
 *
 */
public class QualityReporterRunner {
	
	// List of used preprocessors.
	private String[] preprocessorsList;
	// List of used filters.
	private String[] filtersList;
	

	/**
	 * QualityReporterRunner constructor.
	 * @param preprocessorsList list of used preprocessors.
	 * @param filtersList list of used filters
	 */
	public QualityReporterRunner(String[] preprocessorsList, String[] filtersList) {
		this.preprocessorsList = preprocessorsList;
		this.filtersList = filtersList;
	}

	/**
	 * Runs a QualityReporter instance and generate the summary report.
	 * 
	 * @throws Exception
	 */
	private void printSummaryReport() throws Exception {
		String[] preprocessors = new String[] { "preprocessors.Lowercase",
				"preprocessors.NumberNormalization",
				"preprocessors.UrlNormalization", 
				"preprocessors.WordsCleaner",
				"preprocessors.StopWordsRemoval",
				"preprocessors.EnglishStemmer" };
		String[] filterCreatorsNames = new String[] {
				"filters.DateFilterCreator",
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

		Classifier classifier = mgr.trainUserFromFileSystem(username, "svm",
				trainingSetPercentage);

		Filter[] filters;
		FilterCreatorManager filterCreatorMgr = new FilterCreatorManager(
				filterCreatorsNames, trainingSet);
		filters = filterCreatorMgr.getFilters();
		// FilterManager filterMgr = new FilterManager(filters);
		FilterManager filterMgr = mgr.getFilterManager(username);
		Instances dataset = filterMgr.getDataset(trainingSet);
		QualityReporter reporter = new WekaQualityReporter(dataset);
		reporter.evaluateModel(classifier, filterMgr.getDataset(testingSet));
		System.out.println(reporter.toSummaryString());
		System.out.println(reporter.toClassDetailsString(""));

		FastVector attributes = filterMgr.getAttributes();
		Attribute classAttribute = (Attribute) attributes.elementAt(attributes
				.size() - 1);

		int correct = 0, cnt = 0;
		HashMap<String, Integer> res = new HashMap<String, Integer>();
		System.err.println("testingSet length = " + testingSet.length);
		for (Email email : testingSet) {
			try {
				int result = (int) classifier.classifyInstance(filterMgr
						.makeInstance(email));
				String lbl = classAttribute.value(result);
				if (email.getLabel().equals(lbl))
					correct++;

				if (!res.containsKey(lbl))
					res.put(lbl, 1);
				else
					res.put(lbl, res.get(lbl) + 1);
			} catch (Exception e) {
				cnt++;
			}
		}

		System.err.println("cnt = " + cnt);
		System.err.println(res.size());
		Iterator<Entry<String, Integer>> itr = res.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, Integer> e = itr.next();
			System.err.println(e.getKey() + " --> " + e.getValue());
		}
		double accuracy = correct * 100.0 / testingSet.length;
		System.err.println("correct / test = " + correct + "/"
				+ testingSet.length);
		System.err.println("NaiveBayes accuracy = " + accuracy);
	}

	public static void main(String[] args) throws Exception {
		//new QualityReporterRunner().printSummaryReport();
	}
}
