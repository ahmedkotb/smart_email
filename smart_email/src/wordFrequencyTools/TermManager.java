package wordFrequencyTools;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class TermManager{

	protected String label = "";
	protected int nGramsMax;
	
	public TermManager(String label, int nGramsMax) {
		this.label = label;
		this.nGramsMax = nGramsMax;
	}
	
	public abstract void updateFrequencies(List<HashMap<String, Double>> grams);

	/**
	 * extracts the important words from this TermManager
	 * all managers are given to this method in case
	 * shared data is required in calculations
	 * @param maxSize maxsize of the returning important words list
	 * @param allManagers collection of term managers of all classes
	 * @return list of important words
	 */
	public abstract String[] extractImportantWords(int maxSize,
			Collection<TermManager> allManagers);
}

