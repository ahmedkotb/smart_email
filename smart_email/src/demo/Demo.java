package demo;

import filters.Filter;
import filters.FilterCreatorManager;
import filters.FilterManager;
import general.Email;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import classification.Classifier;

import preprocessors.PreprocessorManager;
import weka.core.Instance;
import weka.core.Instances;

import datasource.DAO;
import datasource.ImapDAO;

public class Demo {

	DAO imapDao;
	ArrayList<String> labels;
	ArrayList<String> chosenLabels;
	ArrayList<Email> trainingData;
	String[] preprocessorsList;
	String[] filtersList;
	String algorithm;
	FilterManager filterManager;
	PreprocessorManager preprocessorManager;
	Classifier classifier;

	public void dummyTest() {
		ArrayList<String> classes = imapDao.getClasses();
		System.out.println("Classes :");
		for (String s : classes)
			System.out.println(s);
		System.out.println("------------------");

		ArrayList<Email> emails = imapDao.getClassifiedEmails("INBOX", 6);
		for (Email e : emails) {
			try {
				System.out.println(e.getSubject());
				String fname = "NONE";
				if (e.getFolder() != null)
					fname = e.getFolder().getName();
				System.out.println(fname);
				try{
				e.setHeader("X-label", "a");
				}catch (Exception ex) {
					ex.printStackTrace();
				}
				System.out.println("============");
				if (e.getContent() instanceof String){
					System.out.println(e.getContent());
				} else {
					Multipart multipart = (Multipart) e.getContent();
					System.out.println("has " + multipart.getCount());
					for (int x = 0; x < multipart.getCount(); x++) {
						BodyPart bodyPart = multipart.getBodyPart(x);

						String disposition = bodyPart.getDisposition();
						if (disposition != null
								&& (disposition.equals(BodyPart.ATTACHMENT))) {
							System.out.println("Mail have some attachment : ");

							DataHandler handler = bodyPart.getDataHandler();
							System.out.println("file name : "
									+ handler.getName());
						} else {
							int min = bodyPart.getContent().toString().length();
							System.out.println(bodyPart.getContent().toString()
									.substring(0, Math.min(100, min)));
						}
						System.out.println("<<<<<<,>>>>>>>>");
					}
				}
			} catch (MessagingException me) {
				me.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			System.out.println("-------------------------------------------");
		}

	}

	public Demo(String preprocessorsList,String filtersList,String algorithm){
		this.preprocessorsList = preprocessorsList.split("\\s*,\\s*");
		this.filtersList = filtersList.split("\\s*,\\s*");
		this.algorithm = algorithm;
	}
	
	public void initDAO() {
		imapDao = new ImapDAO("gp.term.project", "<password>");
	}

	public void chooseLabels() {
		labels = imapDao.getClasses();
		//System.out
		//		.println("Please choose the labels you want to train on (comma separated)");
		System.out.println("labels:");
		System.out.println("========");

		for (int i = 0; i < labels.size(); i++){
			if (i == 5 || i==6 || i ==10 || i== 14 || i==15 || i==16 || i== 18 || i==19 || i == 13) 
				continue;
				
			System.out.println(i + ". " + labels.get(i));
		}

		//System.out.println("------------------");
		//System.out.print("your choice : ");
		try {
			//BufferedReader br = new BufferedReader(new InputStreamReader(
			//		System.in));
			//String input = br.readLine();
			String input = "1,2,3";
			String[] items = input.split("\\s*,\\s*");
			chosenLabels = new ArrayList<String>(items.length);
			for (String item : items)
				chosenLabels.add(labels.get(Integer.parseInt(item)));
			//System.out.println("--------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void normalizeEmail(Email e) {
		//if the content is not a direct string
		//grap the string component of the content
		try{
			if (!(e.getContent() instanceof String)){
				Multipart multipart = (Multipart) e.getContent();
				//XXX assuming that the text part will be the first part [citations needed]
				//e.setContent((Multipart)((Object)multipart.getBodyPart(0).toString()));
				e.setContent(multipart.getBodyPart(0).toString(), "text/plain");
			}
		} catch (MessagingException me) {
			me.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void fetchEmails() {
		trainingData = new ArrayList<Email>();
		for (String label : chosenLabels) {
			ArrayList<Email> emails = imapDao.getClassifiedEmails(label, 5);
			for (Email e: emails){
				normalizeEmail(e);
				trainingData.add(e);
			}
		}
		//System.out.println("FETCHED EMAIL COUNT " + trainingData.size());
	}
	
	public void trainEmails(){
		//create the preprocessors manager
		preprocessorManager = new PreprocessorManager(preprocessorsList);
		
		//step 1: preprocess emails
		for (Email e: trainingData)
			preprocessorManager.apply(e);
		
		//step 2: create filters
		FilterCreatorManager filterCreatorMgr  = null;
		Filter[] filters = null;
		try {
			filterCreatorMgr = new FilterCreatorManager(
					filtersList,trainingData);
			filters = filterCreatorMgr.getFilters();	
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		filterManager = new FilterManager(filters, true);
		
		//step 3: generate the dataset
		Instances dataset = filterManager.getDataset(trainingData);
		
		//step 4: build the classifier
		classifier = Classifier.getClassifierByName(algorithm,
				null);
		classifier.buildClassifier(dataset);
	}

	public void classifyEmails(int count){
		ArrayList<Email> emails = imapDao.getUnclassified(count);
		for (Email e: emails){
			System.out.println("============================");
			try{
				String subject = e.getSubject();
				normalizeEmail(e);
				preprocessorManager.apply(e);
				Instance inst = filterManager.makeInstance(e);
				double res = classifier.classifyInstance(inst);
				System.out.println("Subject : " + subject);
				String labelName = inst.classAttribute().value((int) res);
				imapDao.applyLabel(e.getUid(), labelName);
				System.out.println("Classified As : " + labelName);
			}catch(MessagingException me){
				me.printStackTrace();
			}
		}
		System.out.println("============================");
	}
	
	public static void main(String[] args) {
		// preprocessors.
		String preprocessors = 
				"preprocessors.Lowercase,preprocessors.NumberNormalization," +
				"preprocessors.UrlNormalization,preprocessors.WordsCleaner," +
				"preprocessors.StopWordsRemoval,preprocessors.EnglishStemmer";
		// filters.
		String filters = 
				"filters.SenderFilterCreator," +
				"filters.WordFrequencyFilterCreator," +
				"filters.LabelFilterCreator";
		
		String algorithm = "naiveBayes";
		
		Demo demo = new Demo(preprocessors,filters,algorithm);
		
		demo.initDAO();
		demo.chooseLabels();
		System.out.println("----------------");
		System.out.println("Learning Phase : Collecting Training Data...");
		demo.fetchEmails();
		System.out.println("Learning Phase : Building Classifier...");
		demo.trainEmails();
		System.out.println("Classification Phase : Classifying...");
		demo.classifyEmails(4);
	}
}
