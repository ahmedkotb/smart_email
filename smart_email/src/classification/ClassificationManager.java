package classification;

import java.util.ArrayList;

import weka.core.Instances;

import datasource.DAO;
import filters.Filter;
import filters.FilterCreatorManager;
import filters.FilterManager;
import general.Email;

public class ClassificationManager {

	private final int LIMIT = 1000; //upper limit to number of training data set per label

	//Dummy function for now
	public Classifier go(String datasource, String username, String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		String daoSource;
		if(datasource.toLowerCase().equals("imap")) daoSource = datasource + ":" + username + ":" + password;
		else daoSource = datasource + ":" + username;
		
		DAO dao = DAO.getInstance(daoSource);
		String[] filterCreatorsNames = new String[]{
			"filters.DateFilterCreator", "filters.LabelFilterCreator", "filters.SenderFilterCreator", "filters.WordFrequencyFilterCreator"	
		};
		
		ArrayList<String> labels = dao.getClasses();
		ArrayList<Email> training = new ArrayList<Email>();
		
		for(int i=0; i<labels.size(); i++){
			//XXX what about the limit, and will i need to loop and get the unclassified email into chunks, or just set the limit to High value, this will require a func. in the DAO that takes a starting index
			Email[] emails = dao.getClassifiedEmails(labels.get(i), LIMIT);
			for(int j=0; j<emails.length; j++)
				training.add(emails[j]);
		}
		Email[] trainingSet = new Email[training.size()];
		training.toArray(trainingSet);

		FilterCreatorManager mgr = new FilterCreatorManager(filterCreatorsNames, trainingSet);
		Filter[] filters = mgr.getFilters();
		
		FilterManager filterMgr = new FilterManager(filters);
		
		Instances dataset = filterMgr.getDataset(trainingSet);
		//TODO: why getClassifierByName? i thing using a constructor will be better
		Classifier bayes = Classifier.getClassifierByName("NaiveBayes", null);
		bayes.buildClassifier(dataset);
		return bayes;
	}
}
