package classification;

import java.util.HashMap;

import preprocessors.PreprocessorManager;
import filters.Filter;
import filters.FilterManager;

public class ClassificationManager {

	// ClassifierManager instance used for implementing singleton
	// design pattern.
	private static ClassificationManager managerInstance = null;
	// Path for the training and testing dataset.
	private static final String DATASET_PATH = "../../../enron_processed/";
	// Maximum limit for emails per label
	private static final int LIMIT = 3000;
	// Default pre-processors.
	private final String preprocessors = "preprocessors.Lowercase,preprocessors.NumberNormalization,"
			+ "preprocessors.UrlNormalization,preprocessors.WordsCleaner,"
			+ "preprocessors.StopWordsRemoval,preprocessors.EnglishStemmer";
	// Default filters.
	private final String filtersList = "filters.SenderFilterCreator,"
			+ "filters.WordFrequencyFilterCreator,"
			+ "filters.LabelFilterCreator";

	/**
	 * The function returns the path for the golden data used for training and
	 * testing of email classifier.
	 * 
	 * @param userName
	 *            Email username.
	 * @return Path for classified emails for this user.
	 */
	public String getGoldenDataPath(String userName) {
		return DATASET_PATH + userName;
	}

	/**
	 * Returns an instance of ClassificationManager. The function implements the
	 * singleton design pattern.
	 * 
	 * @param filterCreatorsNames
	 *            List of filter creator names.
	 * @param preprocessors
	 *            List of email preprocessors.
	 * @return ClassificationManager instance.
	 */
	public static ClassificationManager getInstance() {
		if (managerInstance == null) {
			// Create a new instance for future use.
			managerInstance = new ClassificationManager();
			return managerInstance;
		} else {
			// Return the current available instance from the classification
			// manager.
			return managerInstance;
		}
	}

	/**
	 * Returns the default pre-processor manager
	 * @return default pre-processor manager.
	 */
	public PreprocessorManager getDefaultPreprocessor() {
		return new PreprocessorManager(preprocessors.split(","));
	}
	
	/**
	 * Returns the default list of filters used for classification.
	 * @return default list of filters used for classification.
	 */
	public String[] getDefaultFiltersList() {
		return null;
	}

	/**
	 * Returns maximum number of emails per label used for training.
	 * 
	 * @return maximum number of emails per label used for training.
	 */
	public int getTrainingLimit() {
		return LIMIT;
	}
}
