package training;

import java.util.ArrayList;
import javax.mail.MessagingException;
import preprocessors.PreprocessorManager;
import weka.core.Instances;
import classification.Classifier;
import datasource.ImapDAO;
import filters.Filter;
import filters.FilterCreatorManager;
import filters.FilterManager;
import general.Email;

/**
 * This class is used for training the classifier given the user's email,
 * password and the classifier type.
 * 
 * @author Amr Sharaf
 * 
 */
public class AccountTrainer extends Thread implements TrainerIF {

	private String email;
	private String password;
	private String classifierType;

	public AccountTrainer(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public void run() {

	}

	@Override
	public void init() {
		ImapDAO imapDAO = new ImapDAO(email, password);
		ArrayList<String> labels = imapDAO.getClasses();
		System.out.println(labels);

		// training data
		ArrayList<Email> trainingData = new ArrayList<Email>();
		for (String label : labels) {
			ArrayList<Email> emails = imapDAO.getClassifiedEmails(label, 1000);
			for (Email e : emails) {
				try {
					System.out.println(e.getHeader("X-label")[0]);
				} catch (MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				trainingData.add(e);
			}
		}

		String preprocessors = "preprocessors.Lowercase,preprocessors.NumberNormalization,"
				+ "preprocessors.UrlNormalization,preprocessors.WordsCleaner,"
				+ "preprocessors.StopWordsRemoval,preprocessors.EnglishStemmer";

		String filtersList = "filters.SenderFilterCreator,"
				+ "filters.WordFrequencyFilterCreator,"
				+ "filters.LabelFilterCreator";

		// create the preprocessors manager
		PreprocessorManager preprocessorManager = new PreprocessorManager(
				preprocessors.split(","));

		// step 1: pre-process emails
		for (Email e : trainingData)
			preprocessorManager.apply(e);

		// step 2: create filters
		FilterCreatorManager filterCreatorMgr = null;
		Filter[] filters = null;
		try {
			filterCreatorMgr = new FilterCreatorManager(filtersList.split(","),
					trainingData);
			filters = filterCreatorMgr.getFilters();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		FilterManager filterManager = new FilterManager(filters);

		// step 3: generate the dataset
		Instances dataset = filterManager.getDataset(trainingData);

		// step 4: build the classifier
		Classifier classifier = Classifier.getClassifierByName(classifierType,
				null);
		classifier.buildClassifier(dataset);
	}

	@Override
	public Classifier trainModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instances getTrainedInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instances getTestInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		new AccountTrainer("gp.term.project@gmail.com", "gptermproject").init();
	}

}
