package unitTests;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import weka.core.Attribute;

import filters.Filter;
import filters.WordFrequencyFilterCreator;
import general.Email;

public class WordFrequencyFilterTest {
	private WordFrequencyFilterCreator wf;
	private static Email[] emails;
	
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
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		emails = null;
	}

	@Before
	public void setUp() throws Exception {
		wf = new WordFrequencyFilterCreator();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(timeout = 10000)
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
}
