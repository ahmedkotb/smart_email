package filters;

import general.Email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wordFrequencyTools.*;

import javax.mail.MessagingException;

import weka.core.Attribute;
import weka.core.FastVector;

public class SubjectFilterCreator implements FilterCreator{

	//Map from the label (class) name to its LabelTermFrequencyManager 
	private HashMap<String, TermManager> labelFreqMgrMap;

	//Map from each word to the set of labels it appeared in (for the Inverse Document Frequency [IDF] calculations)
	private HashMap<String, HashSet<String>> wordToLabelsMap;

	//Appending a prefix to each word that will become a feature reduces the probability of having 2 features from different filters with the same name, which causes a run time error
	private final String ATT_NAME_PREFIX = "WFF_";
	
	//Maximum number of words feature taken from EACH label 
	private int impWordsPerLabel;
	
	//[own-heuristic] Words that have TF-IDF score less than (thresholdPercentage/100) * maximum TF-IDF score in the label, will be ignored
	//To turn this feature off, set the threshold to zero
	private int thresholdPercentage;
	private double minScore;
	// normalized frequencies
	private boolean freqNormalization;
	private boolean useBinaryAttributes;
	private TokensChoiceAlgorithm tokenChoiceAlgorithm;
	
	//#### TUNABLE CONSTANTS #####
	//===================================
	private final int DEFAULT_IMP_WORDS_PER_LABEL = 80;
	private final int DEFAULT_THRESHOLD_PERCENTAGE = 10;
	//XXX this value needs to be selected based on tests (or even eliminated at all!)
	private final double DEFAULT_MIN_SCORE = Double.MAX_VALUE;
	private final boolean DEFAULT_FREQ_NORMALIZATION = true;
	private final boolean DEFAULT_BINARY_FEATURES = true;

	private final TokensChoiceAlgorithm DEFAULT_TOKEN_ALGORITHM = TokensChoiceAlgorithm.TF_IDF;

	//ngrams constants
	private final int NGRAMS_MAX = 1;
	private final int[] IGNORED_GRAMS = new int[] {};
	//====================================
	
	
	public SubjectFilterCreator() {
		labelFreqMgrMap = new HashMap<String, TermManager>();
		wordToLabelsMap = new HashMap<String, HashSet<String>>();
		
		impWordsPerLabel = DEFAULT_IMP_WORDS_PER_LABEL;
		thresholdPercentage = DEFAULT_THRESHOLD_PERCENTAGE;
		minScore = DEFAULT_MIN_SCORE;
		freqNormalization = DEFAULT_FREQ_NORMALIZATION;
		useBinaryAttributes = DEFAULT_BINARY_FEATURES;
		tokenChoiceAlgorithm = DEFAULT_TOKEN_ALGORITHM;
		
		// sort ignored grams array
		Arrays.sort(IGNORED_GRAMS);

		//print the name of the algorithm used for important words choosing
		System.out.println("Term Choice Algorithm for the SubjectFilter is " + tokenChoiceAlgorithm);
	}

	/**
	 * Getter for impWordsPerLabel
	 * @return Value of the maximum number of words feature taken from EACH label
	 */
	public int getImpWordsPerLable(){
		return this.impWordsPerLabel;
	}

	/**
	 * Setter for impWordsPerLable
	 * @param impWordsPerLabel Value of the maximum number of words feature taken from EACH label (>= 0)
	 */
	public void setImpWordsPerLabel(int impWordsPerLabel){
		if(impWordsPerLabel >= 0)
			this.impWordsPerLabel = impWordsPerLabel;
	}

	/**
	 * getter for the thresholdPercentage
	 * @return The percentage value that specifies the minimum feature score that will be considered
	 * Words that have score less than (thresholdPercentage/100) * maximum TF-IDF score in the label, will be ignored
	 */
	public int getTresholdPercentage(){
		return this.thresholdPercentage;
	}
	
	/**
	 * Setter for thresholdPercentage
	 * @param thresholdPercentage Value of the threshold percentage that specifies the minimum feature score that will be considered
	 */
	public void setThresholdPercentage(int thresholdPercentage){
		if(thresholdPercentage >=0 && thresholdPercentage <= 100){
			this.thresholdPercentage = thresholdPercentage;
		}
	}

	/**
	 * Getter for FreqNormalization
	 * @return True if Frequency Normalization is applied. False otherwise
	 */
	public boolean getFreqNormalization(){
		return freqNormalization;
	}
	
	/**
	 * Setter for FreqNormalization
	 * @param freqNormalization True if Frequency Normalization is required to be applied. False otherwise
	 */
	public void setFreqNormalization(boolean freqNormalization){
		this.freqNormalization = freqNormalization;
	}
	
	/**
	 * Getter for the useBinaryAttributes
	 * @return True if weka binary attributes is used. False otherwise
	 */
	public boolean getUseBinaryAttributes(){
		return useBinaryAttributes;
	}
	
	/**
	 * set useBinaryAttributes option
	 * @param useBinaryAttributes True for binaryAttributes, false for double attributes
	 */
	public void setUseBinaryAttributes(boolean useBinaryAttributes){
		this.useBinaryAttributes = useBinaryAttributes;
	}
	
	/**
	 * Getter for minScore
	 * @return The value of the minimum score that is used to select words features 
	 */
	public double getMinScore(){
		return this.minScore;
	}
	
	/**
	 * Setter for minScore
	 * @param minScore Set the value of the minScore
	 */
	public void setMinScore(double minScore){
		this.minScore = minScore;
	}

	/**
	 * builds Ngrams from the given tokens list starting from 1 to NGRAMS_MAX ,
	 * ignoring the grams specified in the IGRNOREDGRAMS array
	 * 
	 * @param wordsList
	 *            array of strings (tokens)
	 * @return List of hashmaps where the first contains the unigrams count the
	 *         second contains the bigram counts .. etc
	 */
	private List<HashMap<String, Double>> buildGrams(String[] wordsList) {
		ArrayList<HashMap<String, Double>> gramsList = new ArrayList<HashMap<String, Double>>();
		for (int i = 1; i <= NGRAMS_MAX; ++i) {
			HashMap<String, Double> grams = new HashMap<String, Double>();
			// if this Ngrams are not ignored
			if (Arrays.binarySearch(IGNORED_GRAMS, i) < 0) {
				for (int j = 0; j < wordsList.length - i + 1; j++) {
					String gram = wordsList[j];
					for (int j2 = j + 1; j2 < j + i; j2++)
						gram += " " + wordsList[j2];

					if (grams.containsKey(gram))
						grams.put(gram, grams.get(gram) + 1);
					else
						grams.put(gram, 1.0);
				}
			}
			gramsList.add(grams);
		}
		return gramsList;
	}
	
	/**
	 * extracts the Attributes which are the important words from all labels
	 * 
	 * @return an ArrayList of Attribute objects
	 */
	private ArrayList<Attribute> extractAttributes() {
		ArrayList<Attribute> atts = new ArrayList<Attribute>();

		FastVector fv = new FastVector(2);
		fv.addElement("False");
		fv.addElement("True");

		Collection<TermManager> allManagers = labelFreqMgrMap.values();

		Iterator<Map.Entry<String, TermManager>> itr = labelFreqMgrMap
				.entrySet().iterator();
		HashSet<String> uniqueWords = new HashSet<String>();
		while (itr.hasNext()) {
			Map.Entry<String, TermManager> pair = itr.next();
			String[] words = pair.getValue().extractImportantWords(impWordsPerLabel,allManagers);
			for(int i=0; i<words.length; i++){
				if(!uniqueWords.contains(words[i])){
					uniqueWords.add(words[i]);
					if(useBinaryAttributes){
						atts.add(new Attribute(ATT_NAME_PREFIX + words[i], fv));
					} else{
						atts.add(new Attribute(ATT_NAME_PREFIX + words[i]));
					}
				}
			}
		}

		return atts;
	}

	@Override
	public Filter createFilter(ArrayList<Email> emails) {
		// Steps
		// 1- loop on emails and for each email
		// a- calculate normalized frequencies
		// b- update frequencies for each label (labelFreqMgr)
		// c- update wordTolabels (how many times each term appears /label)

		//loop to count the number of labels (TF-IDF needs this number in the constructor
		HashSet<String> labels = new HashSet<String>();
		for(Email email : emails){
			String lbl = null;
			try {
				lbl = email.getHeader("X-label")[0];
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}

			if(!labels.contains(lbl))
				labels.add(lbl);
		}
		
		for (Email email : emails) {
			String lbl = null;
			try {
				lbl = email.getHeader("X-label")[0];
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}

			TermManager mgr = labelFreqMgrMap.get(lbl);
			if (mgr == null) {
				switch (tokenChoiceAlgorithm){
					case TF_IDF:
						int numLabels = labels.size();
						mgr = new TfIdfManager(lbl, NGRAMS_MAX, this.wordToLabelsMap, numLabels, this.thresholdPercentage, this.minScore);
						break;
					case CHI_STATISTIC:
						mgr = new ChiTermManager(lbl, NGRAMS_MAX);
						break;
				}
				labelFreqMgrMap.put(lbl, mgr);
			}

			String[] wordsList = null;
			try {
				//TODO: adding the subject here improves accuracy, but now there is a SubjectFilter that we need to improve
				wordsList = ((String) email.getSubject()).split("\\s+");
			} catch (MessagingException e) {
				e.printStackTrace();
			}

			List<HashMap<String, Double>> grams = buildGrams(wordsList);

			if (freqNormalization) {
				for (int i = 0; i < grams.size(); i++) {
					Iterator<Map.Entry<String, Double>> it = grams.get(i)
							.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, Double> entry = it.next();
						// divides each frequency of unigrams by number of words
						// and each bigrams by number of bigrams (number of
						// words - 1) and so on
						entry.setValue(entry.getValue()
								/ (wordsList.length - i));
					}
				}
			}

			mgr.updateFrequencies(grams);
		}

		//passing arguments to the filter
		ArrayList<Attribute> atts = extractAttributes();
		String[] options = new String[5];
		options[0] = ATT_NAME_PREFIX;
		options[1] = NGRAMS_MAX + "";
		options[2] = "";
		options[3] = freqNormalization + "";
		for (int i = 0; i < IGNORED_GRAMS.length; i++) {
			if (i == IGNORED_GRAMS.length - 1)
				options[2] += IGNORED_GRAMS[i];
			else
				options[2] += IGNORED_GRAMS[i] + ",";
		}
		options[4] = useBinaryAttributes + "";
		
		Filter wordFrequencyFilter = new WordFrequencyFilter(atts, options);

		return wordFrequencyFilter;
	}
}
