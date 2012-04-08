package quality;

import java.util.ArrayList;

import preprocessors.EnglishStemmer;
import preprocessors.Lowercase;
import preprocessors.NumberNormalization;
import preprocessors.Preprocessor;
import preprocessors.StopWordsRemoval;
import preprocessors.UrlNormalization;
import preprocessors.WordsCleaner;
import filters.FilterCreator;
import filters.LabelFilterCreator;
import filters.WordFrequencyFilterCreator;

public class ThrsholdExperiment implements ExperimentTunerIF{

	@Override
	public ArrayList<ExperimentUnit> getExperimentUnits() {
		ArrayList<ExperimentUnit> units = new ArrayList<ExperimentUnit>();
		
		String classifierType = "naiveBayes";
//		String classifierType = "svm";
		int trainingSetPercentage = 60;
		
		ArrayList<Preprocessor> preprocessors = new ArrayList<Preprocessor>();
		preprocessors.add(new Lowercase());
		preprocessors.add(new NumberNormalization());
		preprocessors.add(new UrlNormalization());
		preprocessors.add(new WordsCleaner());
		preprocessors.add(new StopWordsRemoval());
		preprocessors.add(new EnglishStemmer());
		
		for(int i=0; i<=5; i++){ //thresholdPercentage will take the values [0, 10, ..., 50]%
			ArrayList<FilterCreator> filterCreators = new ArrayList<FilterCreator>();
			WordFrequencyFilterCreator wfc = new WordFrequencyFilterCreator();
			wfc.setThresholdPercentage(i*10);
			filterCreators.add(wfc);
			filterCreators.add(new LabelFilterCreator());
			units.add(new ExperimentUnit("th(%)="+(i*10), "", preprocessors, filterCreators, classifierType, trainingSetPercentage));
		}
		
		return units;
	}

	public static void main(String[] args) throws Exception {
		ExperimentTunerIF tuner = new ThrsholdExperiment();
		String[] usernames = new String[]{"beck-s", "farmer-d", "kaminski-v", "kitchen-l", "lokay_m", "sanders_r", "williams-w3"};
//		String[] usernames = new String[]{"lokay_m"};
		ExperimentRunner exp = new ExperimentRunner(tuner, usernames, "WF_Threshold_Value_naiveBayes");
		exp.runExperiment();
	}
}
