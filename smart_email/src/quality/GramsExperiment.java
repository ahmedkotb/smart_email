package quality;

import java.util.ArrayList;

import filters.FilterCreator;
import filters.LabelFilterCreator;
import filters.WordFrequencyFilterCreator;

import preprocessors.EnglishStemmer;
import preprocessors.Lowercase;
import preprocessors.NumberNormalization;
import preprocessors.Preprocessor;
import preprocessors.StopWordsRemoval;
import preprocessors.UrlNormalization;
import preprocessors.WordsCleaner;

public class GramsExperiment implements ExperimentTunerIF{

	@Override
	public ArrayList<ExperimentUnit> getExperimentUnits() {
		ArrayList<ExperimentUnit> units = new ArrayList<ExperimentUnit>();
		
		//String classifierType = "naiveBayes";
		String classifierType = "svm";
		int trainingSetPercentage = 60;
		
		ArrayList<Preprocessor> preprocessors = new ArrayList<Preprocessor>();
		preprocessors.add(new Lowercase());
		preprocessors.add(new NumberNormalization());
		preprocessors.add(new UrlNormalization());
		preprocessors.add(new WordsCleaner());
		preprocessors.add(new StopWordsRemoval());
		preprocessors.add(new EnglishStemmer());

		for(int i=1; i<(1<<2); i++){
			ArrayList<FilterCreator> filterCreators = new ArrayList<FilterCreator>();
			
			WordFrequencyFilterCreator wf = new WordFrequencyFilterCreator();
			int maxGram = Integer.toBinaryString(i).length();
			ArrayList<Integer> ignored = new ArrayList<Integer>();
			for(int j=0; j<maxGram; j++){
				if(((1<<j) & i) == 0) ignored.add(j+1);
			}
			int[] ignoredArray = new int[ignored.size()];
			for(int j=0; j<ignored.size(); j++) ignoredArray[j] = ignored.get(j);
			
			wf.setnGramsMax(maxGram);
			wf.setIgnoredGrams(ignoredArray);
			
			filterCreators.add(wf);
			filterCreators.add(new LabelFilterCreator());
			String title = "grams (" + Integer.toBinaryString(i) + ")";
			units.add(new ExperimentUnit(title, title, preprocessors, filterCreators, classifierType, trainingSetPercentage));
		}
				
		return units;
	}

	public static void main(String[] args) throws Exception {
		ExperimentTunerIF tuner = new GramsExperiment();
		String[] usernames = new String[]{"beck-s", "farmer-d", "kaminski-v", "kitchen-l", "lokay_m", "sanders_r", "williams-w3"};
		ExperimentRunner exp = new ExperimentRunner(tuner, usernames, "grams", "x-grams tests");
		exp.runExperiment();
	}
}
