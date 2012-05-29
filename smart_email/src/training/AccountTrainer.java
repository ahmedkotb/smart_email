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

	public AccountTrainer(String email, String password) {
		this.email = email;
		this.password = password;
		this.classifierType = "svm";
		this.entityManager = Persistence.createEntityManagerFactory(
				"smart_email").createEntityManager();
	}

	public void run() {
		init();
	}

	public void init() {
		ClassificationManager classificationManager = ClassificationManager
				.getInstance();
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

		// create the preprocessors manager
		PreprocessorManager preprocessorManager = classificationManager
				.getDefaultPreprocessor();

		// step 1: pre-process emails
		preprocessorManager.apply(trainingData);

		// step 2: create filters
		FilterCreatorManager filterCreatorMgr = null;
		Filter[] filters = null;
		try {
			filterCreatorMgr = new FilterCreatorManager(
					classificationManager.getDefaultFiltersList(), trainingData);
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
			byte[] blobFilter = byteArray.toByteArray();
			objectOutput.close();
			byteArray.close();
			modelEntity.setModel(blobFilter);
			entityManager.persist(modelEntity);
			transaction.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new AccountTrainer("gp.term.project@gmail.com", "gptermproject").init();
	}

}
