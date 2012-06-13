package training;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import preprocessors.PreprocessorManager;
import weka.core.Instances;
import classification.ClassificationManager;
import classification.Classifier;
import datasource.ImapDAO;
import entities.Model;
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
public class AccountTrainer extends Thread {

	/**
	 * Account email.
	 */
	private String email;

	/**
	 * Account password.
	 */
	private String password;

	/**
	 * Classification type.
	 */
	private String classifierType;

	/**
	 * Entity manager used for managing JPA objects.
	 */
	private EntityManager entityManager;

	/**
	 * Constructor used for initializing the AccountTrainer object.
	 * 
	 * @param email
	 *            account email.
	 * @param password
	 *            account password.
	 */
	public AccountTrainer(String email, String password) {
		this.email = email;
		this.password = password;
		this.classifierType = "svm";
		this.entityManager = Persistence.createEntityManagerFactory(
				"smart_email").createEntityManager();
	}

	/**
	 * Returns the training data for the account.
	 * 
	 * @return training data.
	 */
	private ArrayList<Email> getTrainingData() {
		// Create a new IMAP data access object.
		ImapDAO imapDAO = new ImapDAO(email, password);
		// Retrieve the email labels.
		ArrayList<String> labels = imapDAO.getClasses();
		// Arraylist for storing the training data.
		ArrayList<Email> trainingData = new ArrayList<Email>();
		// Retrieve maximum number of email messages per-label.
		int trainingLimit = ClassificationManager.getTrainingLimit();
		for (String label : labels) {
			ArrayList<Email> emails = imapDAO.getClassifiedEmails(label,
					trainingLimit);
			trainingData.addAll(emails);
		}
		return trainingData;
	}

	public void run() {
		// Retrieve the training data
		ArrayList<Email> trainingData = getTrainingData();
		// Create the preprocessors manager
		PreprocessorManager preprocessorManager = ClassificationManager
				.getDefaultPreprocessor();
		// Process email message
		preprocessorManager.apply(trainingData);
		// Create filters
		FilterCreatorManager filterCreatorManager = null;
		Filter[] filters = null;
		try {
			filterCreatorManager = new FilterCreatorManager(
					ClassificationManager.getDefaultFiltersList(), trainingData);
			filters = filterCreatorManager.getFilters();
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
		storeFilters(filters);
		storeModel(classifier);
	}

	private void storeFilters(Filter[] filters) {
		try {
			EntityTransaction transaction = entityManager.getTransaction();
			transaction.begin();
			for (int i = 0; i < filters.length; i++) {
				entities.Filter filterEntity = new entities.Filter();
				filterEntity.setEmail(email);
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
				ObjectOutput objectOutput = new ObjectOutputStream(byteArray);
				objectOutput.writeObject(filters[i]);
				byte[] blobFilter = byteArray.toByteArray();
				objectOutput.close();
				byteArray.close();
				filterEntity.setFilter(blobFilter);
				entityManager.persist(filterEntity);
			}
			transaction.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void storeModel(Classifier model) {
		try {
			EntityTransaction transaction = entityManager.getTransaction();
			transaction.begin();
			Model modelEntity = new Model();
			modelEntity.setEmail(email);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			ObjectOutput objectOutput = new ObjectOutputStream(byteArray);
			objectOutput.writeObject(model);
			byte[] blobModel = byteArray.toByteArray();
			objectOutput.close();
			byteArray.close();
			modelEntity.setModel(blobModel);
			entityManager.persist(modelEntity);
			transaction.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new AccountTrainer("gp.term.project@gmail.com", "gptermproject").run();
	}

}
