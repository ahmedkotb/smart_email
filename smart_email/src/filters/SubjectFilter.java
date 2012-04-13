package filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import weka.core.Attribute;
import general.Email;

public class SubjectFilter extends Filter{
	
	private static final long serialVersionUID = 7202701812643864245L;
	private String attPrefix;
	private HashMap<String, Integer> indexMap;
	
	private int NGRAMS_MAX = 0;
	private int[] IGNORED_GRAMS;
	private boolean FREQ_NORMALIZATION = false;
	
	/**
	 * Constructor
	 * @param atts List of attributes, one for each important word
	 * @param options Only one option representing the attribute name prefix, which is appended before the word
	 */
	public SubjectFilter(ArrayList<Attribute> atts, String[] options) {
		super(atts, options);
		attPrefix = options[0];
		try {
			NGRAMS_MAX = Integer.parseInt(options[1]);
			String[] ignored = options[2].split(",");
			IGNORED_GRAMS = new int[ignored.length];
			for (int i = 0; i < ignored.length; i++){
				if (ignored[i] != "")
					IGNORED_GRAMS[i] = Integer.parseInt(ignored[i]);
			}
			FREQ_NORMALIZATION = Boolean.valueOf(options[3]);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("INVALID NGrams parameters value");
			System.exit(1);
		}
		
		System.out.println("GRAMS = " + NGRAMS_MAX);
		System.out.println("IGNORED_GRAMS = " + Arrays.toString(IGNORED_GRAMS));
		System.out.println("FREQ_NORMALIZATION = " + FREQ_NORMALIZATION);
		Iterator<Attribute> itr = attributes.iterator();
		indexMap = new HashMap<String, Integer>();
		int index=0;

		while(itr.hasNext()) indexMap.put(getWord(itr.next().name()), index++);
	}

	/**
	 * extracts the word string from the attribute name, because the name of the
	 * attribute is on the form "prefix_word" --> "wff_word"
	 * @param attName Attribute name
	 */
	private String getWord(String attName){
		//TODO : change this function according to the convention in which will name the wordFrequency attributes
		return attName.substring(attPrefix.length());
	}
	
	private List<HashMap<String, Double>> buildGrams(String[] wordsList){
		ArrayList<HashMap<String,Double>> gramsList = new ArrayList<HashMap<String,Double>>();
		for (int i = 1; i <= NGRAMS_MAX; ++i) {
			HashMap<String,Double> grams = new HashMap<String, Double>();
			//if this Ngrams are not ignored
			if (Arrays.binarySearch(IGNORED_GRAMS, i) < 0 ){
				for (int j = 0; j < wordsList.length - i + 1; j++) {
					String gram = "";
					for (int j2 = j; j2 < j+i; j2++)
						gram += wordsList[j2] + " ";
					
					gram = gram.trim();
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
		
	private void fillAttsValues(double[] vals, HashMap<String, Integer> indexMap, Email email){
		String splitRegex = "\\s+";
		//subject is trimmed to avoid empty strings at beginning
		String[] wordsList=null;
		try {
			wordsList = email.getSubject().trim().split(splitRegex);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		List<HashMap<String, Double>> grams = buildGrams(wordsList);

		Iterator<String> it = indexMap.keySet().iterator();
		while (it.hasNext()){
			String key = it.next();
			for (int i = 0; i < NGRAMS_MAX; i++) {
				if (grams.get(i).containsKey(key)){
					vals[indexMap.get(key)] = 1;
					//each key will be found on one gram map only
					break;
				}
			}
		}
	}
	
	@Override
	public double[] getAttValue(Email email){
		double[] vals = new double[attributes.size()];
//		calcFrequencies(vals, indexMap, email);
		fillAttsValues(vals, indexMap, email);
		return vals;
	}
}
