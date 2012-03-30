package filters;

import general.Email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import weka.core.Attribute;
import weka.core.FastVector;

public class WordFrequencyFilterCreator implements FilterCreator{

	private HashMap<String, LabelTermFrequencyManager> labelFreqMgrMap;
	private HashMap<String, HashSet<String>> wordToLabelsMap;
	private final String ATT_NAME_PREFIX = "WFF_";
	private final int IMP_WORDS_PER_LABEL = 80;
	private final int THRESHOLD_PERCENTAGE = 20;
	
	//grams
	private final int NGRAMS_MAX = 1;
	private final int[] IGNORED_GRAMS = new int[] {};
	//normalized frequencies
	private boolean FREQ_NORMALIZATION = true;
	
	
	public WordFrequencyFilterCreator() {
		labelFreqMgrMap = new HashMap<String, WordFrequencyFilterCreator.LabelTermFrequencyManager>();
		wordToLabelsMap = new HashMap<String, HashSet<String>>();
		
		//sort ignored grams array
		Arrays.sort(IGNORED_GRAMS);
	}
	
	private List<HashMap<String, Double>> buildGrams(String[] wordsList){
		ArrayList<HashMap<String,Double>> gramsList = new ArrayList<HashMap<String,Double>>();
		for (int i = 1; i <= NGRAMS_MAX; ++i) {
			HashMap<String,Double> grams = new HashMap<String, Double>();
			//if this Ngrams are not ignored
			if (Arrays.binarySearch(IGNORED_GRAMS, i) < 0 ){
				for (int j = 0; j < wordsList.length - i + 1; j++) {
					String gram = wordsList[j];
					for (int j2 = j+1; j2 < j+i; j2++)
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
	
	private ArrayList<Attribute> extractAttributes(){
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		
		FastVector fv = new FastVector(2);
		fv.addElement("False");
		fv.addElement("True");
		
		Iterator<Map.Entry<String, LabelTermFrequencyManager>> itr = labelFreqMgrMap.entrySet().iterator();
		HashSet<String> uniqueWords = new HashSet<String>();
		while(itr.hasNext()){
			Map.Entry<String, LabelTermFrequencyManager> pair = itr.next();
			String[] words = pair.getValue().extractImportantWords(IMP_WORDS_PER_LABEL);
			for(int i=0; i<words.length; i++){
				if(!uniqueWords.contains(words[i])){
					uniqueWords.add(words[i]);
					atts.add(new Attribute(ATT_NAME_PREFIX + words[i], fv));
				}
			}
		}
		
		return atts;
	}
	
	@Override
	public Filter createFilter(ArrayList<Email> emails) {
		//Steps
		// 1- loop on emails and for each email
		//   a- calculate normalized frequencies
		//   b- update frequencies for each label (labelFreqMgr)
		//   c- update wordTolabels (how many times each term appears /label)
		
		for(Email email : emails){
			String lbl = null;
			try {
				lbl = email.getHeader("X-label")[0];
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}

			LabelTermFrequencyManager mgr = labelFreqMgrMap.get(lbl);
			if(mgr == null){
				mgr = new LabelTermFrequencyManager();
				labelFreqMgrMap.put(lbl, mgr);
			}
			// TODO: Moustafa please review
			//subject is trimmed to avoid empty strings at beginning
			String[] wordsList = null;
			try {
				wordsList = ((String) email.getContent()).split("\\s+");
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			List<HashMap<String, Double>> grams = buildGrams(wordsList);
			
			if (FREQ_NORMALIZATION){
				for (int i = 0; i < grams.size(); i++) {
					Iterator<Map.Entry<String, Double>> it = grams.get(i).entrySet().iterator();
					while (it.hasNext()){
						Map.Entry<String, Double> entry = it.next();
						//divides each frequency of unigrams by number of words
						//and each bigrams by number of bigrams (number of words - 1)
						// and so on
						entry.setValue(entry.getValue()/(wordsList.length - i));
					}		
				}
			}
			
			mgr.updateFrequencies(grams);
			
			// update wordToLabelMap
			//XXX can avoid the overhead of this loop by having this work 
			//done as a side-effect in the calcNormalizedFreq() function
			
			for (int i = 0; i < NGRAMS_MAX; ++i) {
				Iterator<Map.Entry<String, Double>> it = grams.get(i).entrySet().iterator();
				while(it.hasNext()){
					String word = it.next().getKey();
					if(! wordToLabelsMap.containsKey(word))
						wordToLabelsMap.put(word, new HashSet<String>());
					
					wordToLabelsMap.get(word).add(lbl);
				}
			}
		}
		
		ArrayList<Attribute> atts = extractAttributes();
		String[] options = new String[4];
		options[0] = ATT_NAME_PREFIX;
		options[1] = NGRAMS_MAX + "";
		options[2] = "";
		options[3] = FREQ_NORMALIZATION + "";
		for (int i = 0; i < IGNORED_GRAMS.length; i++) {
			if (i == IGNORED_GRAMS.length - 1)
				options[2] += IGNORED_GRAMS[i];
			else
				options[2] += IGNORED_GRAMS[i] + ",";
		}
		Filter wordFrequencyFilter = new WordFrequencyFilter(atts, options);

		return wordFrequencyFilter;
	}

	private class LabelTermFrequencyManager{
		
		public List<HashMap<String, Double>> gramsFreq;
		
		public LabelTermFrequencyManager(){
			//init grams frequencies list
			gramsFreq  = new ArrayList<HashMap<String,Double>>();
			for (int i = 0; i < NGRAMS_MAX; i++)
				gramsFreq.add(new HashMap<String, Double>());

		}

		public void updateFrequencies(List<HashMap<String, Double>> grams){
			//merge the two hash maps for each ngram
			for (int i = 0; i < NGRAMS_MAX; i++) {
				HashMap<String, Double> labelGrams = gramsFreq.get(i);
				Iterator<Map.Entry<String, Double>> itr = grams.get(i).entrySet().iterator();			
				while(itr.hasNext()){
					Map.Entry<String, Double> pair = itr.next();
					
					if (labelGrams.containsKey(pair.getKey())){
						labelGrams.put(pair.getKey(),
								labelGrams.get(pair.getKey()) + pair.getValue());
					}else
						labelGrams.put(pair.getKey(), pair.getValue());
				}
			}
			
		}
		
		public String[] extractImportantWords(int maxSize){
			ArrayList<TfIdfScore> tfidf = new ArrayList<TfIdfScore>();
			
			for (int i = 0; i < gramsFreq.size(); i++) { // for each gram size
				Iterator<Map.Entry<String, Double>> itr = gramsFreq.get(i).entrySet().iterator();
				while(itr.hasNext()){
					Map.Entry<String, Double> pair = itr.next();
					double tf = pair.getValue();
					
//					if(wordToLabelsMap.get(pair.getKey()).size()>3) continue;
					
					double idf = Math.log10(((double) labelFreqMgrMap.size()) / wordToLabelsMap.get(pair.getKey()).size());
					tfidf.add(new TfIdfScore(pair.getKey(), tf*idf));
				}
			}
			
			if(tfidf.size() > maxSize)
				Collections.sort(tfidf);
			
			//XXX Heuristic: if a term has score < 10% of the highest score, then ignore it
			double threshold = THRESHOLD_PERCENTAGE/100.0 * tfidf.get(0).score;
			int sz = 0;
			for(; sz<tfidf.size(); sz++) if(tfidf.get(sz).score < threshold) break;
			String[] importantWords = new String[Math.min(maxSize, sz)];
			
//			String[] importantWords = new String[Math.min(maxSize, tfidf.size())];
			
			for(int i=0; i<importantWords.length; i++)
				importantWords[i] = tfidf.get(i).word;
//			System.err.println(importantWords.length);
			return importantWords;
		}
	}
	
	private class TfIdfScore implements Comparable<TfIdfScore>{
		public String word;
		public double score;
		private double eps = 1e-10;
		
		public TfIdfScore(String word, double score){
			this.word = word;
			this.score = score;
		}

		@Override
		public int compareTo(TfIdfScore s) {
			if(score > s.score+eps) return -1;
			else if(score + eps < s.score) return 1;
			return 0;
		}
	}
}
