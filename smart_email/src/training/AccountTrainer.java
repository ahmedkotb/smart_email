package training;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import preprocessors.PreprocessorManager;
import weka.core.Instances;
import classification.ClassificationManager;
import classification.Classifier;
import datasource.ImapDAO;
import entities.Account;
import entities.Model;
import entities.ModelPK;
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
	
	private String status;

	/**
	 * Classification type.
	 */
	private String classifierType;

	/**
	 * Entity manager used for managing JPA objects.
	 */
	private EntityManager entityManager;
	
	/**
	 * Accuracy of the user classification model
	 */
	private float accuracy;
	/**
	 * Total classified emails for the user
	 */
	private int totalClassified;
	/**
	 * Total incorrectly classified emails for the user
	 */
	private int totalIncorrect;
	/**
	 * Average response time for the user 
	 */
	private float AvgResponseTime;
	
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
		this.classifierType = ClassificationManager.getClassifierType();
		//this.classifierType = "onlinenaivebayes";
	}
	
	public AccountTrainer(String email, String password, float accuracy, int totalClassified, int totalIncorrect, float avgResponseTime) {
		this.email = email;
		this.password = password;
		this.classifierType = ClassificationManager.getClassifierType();
		this.accuracy = accuracy;
		this.totalClassified = totalClassified;
		this.totalIncorrect = totalIncorrect;
		this.AvgResponseTime = avgResponseTime;
	}


	/**
	 * Returns the training data for the account.
	 * 
	 * @return training data.
	 */
	private ArrayList<Email> getTrainingData() {
		System.out.println("Connecting to email using IMAP..");
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

	   // imapDAO.closeConnection();

		System.out.println("Training data retrieived using IMAP....");
		
		return trainingData;
	}

	public void run() {
		//initializing entity manager
		this.entityManager = Persistence.createEntityManagerFactory(
				"smart_email").createEntityManager();
		// Store the account with state: training
		storeAccount("Training Phase");
		// Retrieve the training data
		System.out.println("Collecting training data....");
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

		FilterManager filterManager = new FilterManager(filters, true);

		// step 3: generate the dataset
		Instances dataset = filterManager.getDataset(trainingData);

		// step 4: build the classifier
		Classifier classifier = Classifier.getClassifierByName(classifierType,
				null);
		classifier.buildClassifier(dataset);
		System.out.println("Storing account and classification model...");
		storeAccount(filters);
		storeModel(classifier);
		System.out.println("The classifier has been trained and the data "
				+ "was stored in the database..");

	}

	private void storeAccount(String status) {
		Account account = new Account();
		account.setEmail(email);
		account.setToken(password);
		account.setStatus(status);
		account.setLastVisit(new Date());
		account.setAccuracy(this.accuracy);
		account.setTotalClassified(this.totalClassified);
		account.setTotalIncorrect(this.totalIncorrect);
		account.setAvgResponseTime(this.AvgResponseTime);
		EntityTransaction entr = entityManager.getTransaction();
		entr.begin();
		entityManager.merge(account);
		entr.commit();
	}

	/**
	 * Stores the account data in the database.
	 * @param filters array of user filters.
	 */
	private void storeAccount(Filter[] filters) {
		Account account = new Account();
		account.setEmail(email);
		account.setToken(password);
		account.setFiltersList(getSerializedFilters(filters));
		account.setLastVisit(new Date());
		account.setAccuracy(this.accuracy);
		account.setTotalClassified(this.totalClassified);
		account.setTotalIncorrect(this.totalIncorrect);
		account.setAvgResponseTime(this.AvgResponseTime);
		account.setStatus("Ready to receive classification requests");
		EntityTransaction entr = entityManager.getTransaction();
		entr.begin();
		entityManager.merge(account);
		entr.commit();
	}
	
	/**
	 * Returns the array of filters in byte[] form.
	 * @param array of filters.
	 * @return byte[] representing the list of filters.
	 */
	private byte[] getSerializedFilters(Filter[] filters) {
		try {
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			ObjectOutput objectOutput = new ObjectOutputStream(byteArray);
			objectOutput.writeObject(filters);
			byte[] blobFilter = byteArray.toByteArray();
			objectOutput.close();
			byteArray.close();
			return blobFilter;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Stores the trained model to the database.
	 * 
	 * @param model
	 *            the model to store in the database.
	 */
	private void storeModel(Classifier model) {
		try {
			EntityTransaction transaction = entityManager.getTransaction();
			transaction.begin();
			ModelPK modelPk = new ModelPK();
			modelPk.setEmail(email);
			modelPk.setType(classifierType);
			Model modelEntity = new Model();
			modelEntity.setId(modelPk);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			ObjectOutput objectOutput = new ObjectOutputStream(byteArray);
			objectOutput.writeObject(model);
			byte[] blobModel = byteArray.toByteArray();
			objectOutput.close();
			byteArray.close();
			modelEntity.setModel(blobModel);
			entityManager.merge(modelEntity);
			transaction.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new AccountTrainer("gp.term.project@gmail.com", "gptermproject").run();
	}

}
