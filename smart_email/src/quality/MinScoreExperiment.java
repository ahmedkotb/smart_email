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

public class MinScoreExperiment implements ExperimentTunerIF{

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
		
		double[] minScores = new double[]{1000.0, 1,  0.5, 0.2, 1.5, 0.1, 0.05, 0.01, 0};
		for(int i=0; i<minScores.length; i++){ 
			ArrayList<FilterCreator> filterCreators = new ArrayList<FilterCreator>();
			WordFrequencyFilterCreator wfc = new WordFrequencyFilterCreator();
			wfc.setMinScore(minScores[i]);
			filterCreators.add(wfc);
			filterCreators.add(new LabelFilterCreator());
			units.add(new ExperimentUnit(String.valueOf(minScores[i]), "", preprocessors, filterCreators, classifierType, trainingSetPercentage));
		}
		
		return units;
	}

	public static void main(String[] args) throws Exception {
		ExperimentTunerIF tuner = new MinScoreExperiment();
		String[] usernames = new String[]{"beck-s", "farmer-d", "kaminski-v", "kitchen-l", "lokay_m", "sanders_r", "williams-w3"};
//		String[] usernames = new String[]{"lokay_m"};
		ExperimentRunner exp = new ExperimentRunner(tuner, usernames, "WF_MinScore_naiveBayes");
		exp.runExperiment();
	}
}
