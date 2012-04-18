package wordFrequencyTools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChiTermManager extends TermManager {

	//maps Term ==> no of emails
	HashMap<String, Integer> documentFrequencies;

	//no of emails in this label
	int emailsCount = 0;

	public ChiTermManager(String label, int nGramsMax) {
		super(label, nGramsMax);
		documentFrequencies = new HashMap<String, Integer>();
	}
	
	@Override
	public void updateFrequencies(List<HashMap<String, Double>> grams) {
		// merge the two hash maps for each ngram
		// XXX this method should be called for every email 
		// and should be called only once
		// XXX only for unigrams now
		
		emailsCount++;
		
		Iterator<Map.Entry<String, Double>> itr = grams.get(0)
				.entrySet().iterator();
		
		while (itr.hasNext()) {
			Map.Entry<String, Double> pair = itr.next();

			int oldValue = 0;
			if (documentFrequencies.containsKey(pair.getKey()))
				oldValue = documentFrequencies.get(pair.getKey());
				
			//we only increase by one as we are counting no of document each term appears in
			documentFrequencies.put(pair.getKey(), oldValue+1);
		}
	}

	@Override
	public String[] extractImportantWords(int maxSize,
			Collection<TermManager> allManagers) {
		
		List<WordScore> tokenScores = new ArrayList<WordScore>();
		
		//calculate chi score for each token
		
		//System.out.println(this.emailsCount);
		Iterator<String> itr = documentFrequencies.keySet().iterator();
		
		while (itr.hasNext()){
			String token = itr.next();
			int N11 = documentFrequencies.get(token);
			int N01 = this.emailsCount - N11;
			int N10 = 0;
			int N00 = 0;
			Iterator<TermManager> manItr = allManagers.iterator();
			while (manItr.hasNext()){
				ChiTermManager tm = (ChiTermManager) manItr.next();
				if (tm.label.equals(this.label))
					continue;
				if (tm.documentFrequencies.containsKey(token)){
					N10 += tm.documentFrequencies.get(token);
					N00 += tm.emailsCount - tm.documentFrequencies.get(token);
				}else{
					N00 += tm.emailsCount;
				}
			}
			
			int N = N00 + N01 + N10 + N11;
			//System.out.println("(T,L) (" + token + ","  + this.label + ")" +
			//		"  (N11,N10,N01,N00) => N  " + N11 + "," + N10 + "," + N01 + "," + N00 + " => " + N);
			double t1 =  (N * (N11*N00 - N10*N01) * (N11*N00 - N10*N01));
			double t2 =  (N11 + N01) * (N11 + N10) * (N10 + N00) * (N01 + N00);
			double chiScore =  t1/t2;
			tokenScores.add(new WordScore(token, chiScore));
		}
		
		Collections.sort(tokenScores);
		
		int size = Math.min(maxSize, tokenScores.size());
		String[] importantWords = new String[size];
		
		for (int i = 0; i < size; i++)
			importantWords[i] =  tokenScores.get(i).word;
		
		/*
		System.out.println("label : " + this.label);
		for (int i = 0; i < Math.min(10,tokenScores.size()); i++) {
			System.out.println(tokenScores.get(i).word + "\t" + tokenScores.get(i).score);
		}
		System.out.println("--------");
		*/
		
		return importantWords;
	}
	
}
