package unitTests;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import preprocessors.PreprocessorManager;

import classification.ClassificationManager;
import classification.Classifier;
import classification.NaiveBayesClassifier;
import datasource.DAO;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import filters.Filter;
import filters.FilterCreatorManager;
import filters.FilterManager;
import filters.WordFrequencyFilterCreator;
import general.Email;

public class WordFrequencyFilterTest {

	private WordFrequencyFilterCreator wf;
	private static Email[] emails;
	private static Email[] trainingSet;
	private static Instances dataset;
	private static FilterManager filterMgr;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/*
		 * Data Set contains 6 emails and 3 labels
		 * num of imortant words per label is manually set to 5 (during the testing only)
		 * - Sports Label -> 1 email, the label contains 4 words: the(10), players(3), football(2), match(5)
		 * - Education Label -> 3 emails, the label contains 8 words: the(11), college(1), test(4), lecture(5), section(6), quiz(2), subject(2), students(6)
		 * - News Label -> 2 emails, the label contains 10 words: the(8), Egypt(2), revolution(2), SCAF(5), demonstration(1), Tahrir(5), people(3), test(3), Cairo(2), Alex(1) 
		 */
		emails = new Email[6];
		String content = " , the the the    players  match the the players match match football match players the the match the ! the the?";
		emails[0] = new Email("x", "y", "football", content, content.length(), new Date());
		emails[0].setLabel("sports");

		content = "the lecture, setion, the quiz, students students section, the subject, the students ";
		emails[1] = new Email("x", "y", "college", content, content.length(), new Date());
		emails[1].setLabel("Education");

		content = content + " lecture test, the test";
		emails[2] = new Email("x", "y", "section", content, content.length(), new Date());
		emails[2].setLabel("Education");

		content = "the test, the lecture, section, lecture";
		emails[3] = new Email("x", "y", "test", content, content.length(), new Date());
		emails[3].setLabel("Education");

		content = "Egypt, the revolution, the people, Cairo, the test SCAF Tahrir Tahrir SCAF the";
		emails[4] = new Email("x", "y", "Alex people demonstration", content, content.length(), new Date());
		emails[4].setLabel("News");

//		content = content;
		emails[5] = new Email("x", "y", "the - SCAF - Tahrir", content, content.length(), new Date());
		emails[5].setLabel("News");

		//*****************************************************************************

		String path = "enron_processed/lokay_m";		
		DAO dao = DAO.getInstance("FileSystems:" + path);

		ArrayList<String> labels = dao.getClasses();
		ArrayList<Email> training = new ArrayList<Email>();
		for(int i=0; i<labels.size(); i++){
			//XXX what about the limit, and will i need to loop and get the unclassified email into chunks, or just set the limit to High value, this will require a func. in the DAO that takes a starting index
			Email[] emails = dao.getClassifiedEmails(labels.get(i), 1000);
			for(int j=0; j<emails.length; j++)
				training.add(emails[j]);
		}
		trainingSet = new Email[training.size()];
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
//				"filters.SenderFilterCreator", "filters.DateFilterCreator", "filters.LabelFilterCreator"	
//		};

		FilterCreatorManager filterCreatorMgr = new FilterCreatorManager(filterCreatorsNames, trainingSet);
		Filter[] filters = filterCreatorMgr.getFilters();
		filterMgr = new FilterManager(filters);
		dataset = filterMgr.getDataset(trainingSet);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		emails = null;
	}

	@Before
	public void setUp() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		wf = new WordFrequencyFilterCreator();
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test(timeout = 10000)
	public void funcTest0(){
		Filter f = wf.createFilter(emails);
		ArrayList<Attribute> atts = f.getAttributes();
		Assert.assertEquals(14, atts.size());
		
		//Needs manual check here! (for now)
		System.out.println("Test0:\n------");
		for(int i=0; i<atts.size(); i++) System.out.println(atts.get(i).name());
		System.out.println("=================\n");
	}

	@Test(timeout = 10000)
	public void funcTest1() throws FileNotFoundException, IOException, ClassNotFoundException{
		Filter f = wf.createFilter(emails);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("wff.ser"));
		oos.writeObject(f);

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("wff.ser"));
		Filter f_read = null;
		f_read = (Filter) ois.readObject();

		Assert.assertNotNull(f_read);
		Assert.assertEquals(f.getAttributes().size(), f_read.getAttributes().size());

		System.out.println("Test1:\n------");
		for(int i = 0; i<f.getAttributes().size(); i++){
			Assert.assertEquals(f.getAttributes().get(i), f_read.getAttributes().get(i));
			System.out.println(f_read.getAttributes().get(i).name());
		}
		System.out.println("=================\n");
	}

	@Test(timeout = 10000)
	public void funcTest2() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		String[] filterCreatorsNames = new String[]{"filters.DateFilterCreator", "filters.SenderFilterCreator", "filters.WordFrequencyFilterCreator", "filters.LabelFilterCreator"};

		FilterCreatorManager mgr = new FilterCreatorManager(filterCreatorsNames, emails);
		Filter[] filters = mgr.getFilters();
		FilterManager filterMgr = new FilterManager(filters);

		Email test = emails[0];
		System.err.println(test);

		Instances dataset = filterMgr.getDataset(emails);

		Classifier bayes = new NaiveBayesClassifier();
		bayes.buildClassifier(dataset);

		Instance testInstance = filterMgr.makeInstance(test);
		System.err.println(testInstance);
		int result = (int) bayes.classifyInstance(testInstance);
		System.err.println("Result is: " + dataset.classAttribute().value(result));

		Assert.assertEquals(test.getLabel(), dataset.classAttribute().value(result));
	}

	@Test
	public void naiveBayesTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		ClassificationManager mgr = new ClassificationManager();
		String path = "enron_processed/lokay_m";
		Classifier cls = mgr.go("FileSystem", path, null, 0);

		int correct = 0;
		HashMap<String, Integer> res = new HashMap<String, Integer>();
		for(Email email : trainingSet){
			int result = (int) cls.classifyInstance(filterMgr.makeInstance(email));
			String lbl = dataset.classAttribute().value(result);
			if(email.getLabel().equals(lbl)) correct++;
			
			if(!res.containsKey(lbl)) res.put(lbl, 1);
			else res.put(lbl, res.get(lbl)+1);
		}

		System.err.println(res.size());
		Iterator<Entry<String, Integer>> itr = res.entrySet().iterator();
		while(itr.hasNext()){
			Entry<String, Integer> e = itr.next();
			System.err.println(e.getKey() + " --> " + e.getValue());
		}
//		Assert.assertEquals(trainingSet.length, correct);
		double accuracy = correct*100.0 / trainingSet.length;
		System.err.println("NaiveBayes accuracy = " + accuracy);
		Assert.assertTrue(accuracy >= 75);
	}
	
//	@Test
	public void decisionTreeTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		ClassificationManager mgr = new ClassificationManager();
		String path = "enron_processed/lokay_m";
		Classifier cls = mgr.go("FileSystem", path, null, 1);

		int correct = 0;
		for(Email email : trainingSet){
			int result = (int) cls.classifyInstance(filterMgr.makeInstance(email));
			if(email.getLabel().equals(dataset.classAttribute().value(result))) correct++;
		}

//		Assert.assertEquals(trainingSet.length, correct);
		double accuracy = correct*100.0 / trainingSet.length;
		System.err.println("DecisionTree accuracy = " + accuracy);
		Assert.assertTrue(accuracy >= 75);
	}
	
	@Test
	public void svmTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		ClassificationManager mgr = new ClassificationManager();
		String path = "enron_processed/lokay_m";
		Classifier cls = mgr.go("FileSystem", path, null, 2);

		int correct = 0;
		for(Email email : trainingSet){
			int result = (int) cls.classifyInstance(filterMgr.makeInstance(email));
			if(email.getLabel().equals(dataset.classAttribute().value(result))) correct++;
		}

//		Assert.assertEquals(trainingSet.length, correct);
		double accuracy = correct*100.0 / trainingSet.length;
		System.err.println("SVM accuracy = " + accuracy);
		Assert.assertTrue(accuracy >= 75);
	}
}