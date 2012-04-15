package wordFrequencyTools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TfIdfManager extends TermManager {

	private List<HashMap<String, Double>> gramsFreq;

	//[own-heuristic] Words that have TF-IDF score less than (thresholdPercentage/100) * maximum TF-IDF score in the label, will be ignored
	//To turn this feature off, set the threshold to zero
	private int thresholdPercentage;
	private double minScore;

	private int numLabels;
	
	//Map from each word to the set of labels it appeared in (for the Inverse Document Frequency [IDF] calculations)
	private HashMap<String, HashSet<String>> wordToLabelsMap;
	
	public TfIdfManager(String label, int nGramsMax, HashMap<String, HashSet<String>> wordToLabelsMap, int numLabels, int thresholdPercentage, double minScore) {
		super(label, nGramsMax);
		this.numLabels = numLabels;
		this.thresholdPercentage = thresholdPercentage;
		this.minScore = minScore;
		this.wordToLabelsMap = wordToLabelsMap;
		
		// init grams frequencies list
		gramsFreq = new ArrayList<HashMap<String, Double>>();
		for (int i = 0; i < nGramsMax; i++)
			gramsFreq.add(new HashMap<String, Double>());
	}

	/**
	 * updates the grams frequencies by the given hashmaps
	 * @param grams the new grams frequencies to update the current one with
	 */
	public void updateFrequencies(List<HashMap<String, Double>> grams) {
		// merge the two hash maps for each ngram
		for (int i = 0; i < nGramsMax; i++) {
			HashMap<String, Double> labelGrams = gramsFreq.get(i);
			Iterator<Map.Entry<String, Double>> itr = grams.get(i)
					.entrySet().iterator();
			
			while (itr.hasNext()) {
				Map.Entry<String, Double> pair = itr.next();

				// update the frequency of the word (gram)
				double newValue = pair.getValue();
				if (labelGrams.containsKey(pair.getKey()))
					newValue += labelGrams.get(pair.getKey());

				labelGrams.put(pair.getKey(), newValue);

				// Mandatory Side Effect: update the number of labels where this word (gram) appeared
				String word = pair.getKey();
				if (!wordToLabelsMap.containsKey(word))
					wordToLabelsMap.put(word, new HashSet<String>());

				wordToLabelsMap.get(word).add(this.label);
			}
		}		
	}

	/**
	 * extracts the important words from the whole words stored in this object
	 * @param maxSize the size of the returning array
	 * @return list of important words
	 */
	public String[] extractImportantWords(int maxSize,Collection<TermManager> allManagers) {
		ArrayList<WordScore> tfidf = new ArrayList<WordScore>();

		for (int i = 0; i < gramsFreq.size(); i++) { // for each gram size
			Iterator<Map.Entry<String, Double>> itr = gramsFreq.get(i)
					.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, Double> pair = itr.next();
				double tf = pair.getValue();

				double idf = Math.log10(((double) numLabels)
						/ wordToLabelsMap.get(pair.getKey()).size());
				tfidf.add(new WordScore(pair.getKey(), tf * idf));
			}
		}

		Collections.sort(tfidf);

		//Heuristic: if a term has score < 10% of the highest score, then ignore it
		double threshold = thresholdPercentage/100.0 * tfidf.get(0).score;
		int sz = 0;
		int maxSz = Math.min(maxSize, tfidf.size());
		for (; sz < maxSz; sz++)
			if (tfidf.get(sz).score < threshold && tfidf.get(sz).score < minScore) //XXX 2 own-heuristics!
				break;
		
		String[] importantWords = new String[sz];
		for (int i = 0; i < importantWords.length; i++)
			importantWords[i] = tfidf.get(i).word;
		
		return importantWords;
	}
}
