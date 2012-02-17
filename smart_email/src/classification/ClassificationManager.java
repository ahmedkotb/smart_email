package classification;

import java.util.ArrayList;
import java.util.HashMap;

import preprocessors.PreprocessorManager;

import weka.core.Instances;

import datasource.DAO;
import datasource.FileSystemDAO;
import filters.Filter;
import filters.FilterCreatorManager;
import filters.FilterManager;
import general.Email;

public class ClassificationManager {

	private final int LIMIT = 2000; //upper limit to number of training data set per label
	private HashMap<String, Filter[]> userFilters = new HashMap<String, Filter[]>();
	private static final String DATASET_PATH = "../../../enron_processed/";
	private String[] filterCreatorsNames;
	private String[] preprocessors;
	
	public String getGoldenDataPath(String username) {
		return DATASET_PATH + username;
	}
	
	public ClassificationManager(String[] filterCreatorsNames, String[] preprocessors){
		this.filterCreatorsNames = filterCreatorsNames;
		this.preprocessors = preprocessors;
	}
	
	public FilterManager getFilterManager(String username){
		Filter[] filters = userFilters.get(username);
		if(filters == null) return null;
		return new FilterManager(filters);
	}
	
	private Email[] getTrainingSet(DAO dao, int trainingSetPercentage){
		ArrayList<String> labels = dao.getClasses();
		ArrayList<Email> training = new ArrayList<Email>();
				
		for(int i=0; i<labels.size(); i++){
			//XXX what about the limit, and will i need to loop and get the unclassified email into chunks, or just set the limit to High value, this will require a func. in the DAO that takes a starting index
			Email[] emails = dao.getClassifiedEmails(labels.get(i), LIMIT);

			int trainingLimit = (int) Math.ceil((trainingSetPercentage/100.0)*emails.length);
			for(int j=0; j<trainingLimit; j++)
				training.add(emails[j]);
		}
		Email[] trainingSet = new Email[training.size()];
		training.toArray(trainingSet);		
		
		return trainingSet;
	}
	
	private void preprocessEmails(Email[] emails){
		PreprocessorManager pm = new PreprocessorManager(preprocessors);
		for(Email email : emails)
			pm.apply(email);
	}
	
	public Classifier trainUserFromFileSystem(String username, String classifierName, int trainingSetPercentage) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		String datasetPath = ClassificationManager.DATASET_PATH + username;
		DAO dao = new FileSystemDAO(datasetPath);
		Email[] trainingSet = getTrainingSet(dao, trainingSetPercentage);
		preprocessEmails(trainingSet);
		
		Filter[] filters = userFilters.get(username);
		if(filters == null){
			FilterCreatorManager filterCreatorMgr = new FilterCreatorManager(filterCreatorsNames, trainingSet);
			filters = filterCreatorMgr.getFilters();
			userFilters.put(username, filters);
		}
		
		FilterManager filterMgr = new FilterManager(filters);
		Instances dataset = filterMgr.getDataset(trainingSet);
		Classifier classifier = Classifier.getClassifierByName(classifierName, null);
		classifier.buildClassifier(dataset);
		return classifier;
	}

/*	
	//Dummy function for now type: 0->naive bayes, 1->decision tree , 2->svm
	public Classifier go(String datasource, String username, String password, int type) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		String daoSource;
		if(datasource.toLowerCase().equals("imap")) daoSource = datasource + ":" + username + ":" + password;
		else daoSource = datasource + ":" + username;
		
		DAO dao = DAO.getInstance(daoSource);
		
		ArrayList<String> labels = dao.getClasses();
		ArrayList<Email> training = new ArrayList<Email>();
				
		for(int i=0; i<labels.size(); i++){
			//XXX what about the limit, and will i need to loop and get the unclassified email into chunks, or just set the limit to High value, this will require a func. in the DAO that takes a starting index
			Email[] emails = dao.getClassifiedEmails(labels.get(i), LIMIT);
			///XXX training:test = 60:40
			double trainingRatio = 0.6;
//			int trainingLimit = (int) Math.ceil(trainingRatio*emails.length);
			int trainingLimit = emails.length;
			for(int j=0; j<trainingLimit; j++)
				training.add(emails[j]);
		}
		Email[] trainingSet = new Email[training.size()];
		training.toArray(trainingSet);

		String[] preprocessors = new String[]{
				"preprocessors.Lowercase", "preprocessors.NumberNormalization", "preprocessors.UrlNormalization", "preprocessors.WordsCleaner", "preprocessors.StopWordsRemoval", "preprocessors.EnglishStemmer"
		};
		PreprocessorManager pm = new PreprocessorManager(preprocessors);
		for (Email e: trainingSet)
			pm.apply(e);

		String[] filterCreatorsNames = new String[]{
			"filters.DateFilterCreator", "filters.SenderFilterCreator", "filters.WordFrequencyFilterCreator", "filters.LabelFilterCreator"
		};
//		String[] filterCreatorsNames = new String[]{
//				"filters.SenderFilterCreator", "filters.DateFilterCreator", "filters.WordFrequencyFilterCreator", "filters.LabelFilterCreator"	
//		};

		FilterCreatorManager mgr = new FilterCreatorManager(filterCreatorsNames, trainingSet);
		Filter[] filters = mgr.getFilters();
		usersFilters.put(username, filters);
		FilterManager filterMgr = new FilterManager(filters);
		
		Instances dataset = filterMgr.getDataset(trainingSet);
		
		//TODO: why getClassifierByName? i thing using a constructor will be better
//		Classifier bayes = Classifier.getClassifierByName("NaiveBayes", null);
		Classifier classifier = null;
		switch (type){
			case 0: 
				classifier = new NaiveBayesClassifier();
				break;
			case 1:
				classifier = new DecisionTreeClassifier();
				break;
			case 2:
				classifier = new SVMClassifier();
				break;
		}
		classifier.buildClassifier(dataset);
		
		return classifier;
	}
*/
}
