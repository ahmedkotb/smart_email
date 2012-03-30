package quality;

import java.util.ArrayList;

import filters.FilterCreator;
import filters.LabelFilterCreator;
import filters.SenderFilterCreator;
import filters.WordFrequencyFilterCreator;

import preprocessors.EnglishStemmer;
import preprocessors.Lowercase;
import preprocessors.NumberNormalization;
import preprocessors.Preprocessor;
import preprocessors.StopWordsRemoval;
import preprocessors.UrlNormalization;
import preprocessors.WordsCleaner;

public class Experiment1 implements ExperimentTunerIF{

	@Override
	public ArrayList<ExperimentUnit> getExperimentUnits() {
		ArrayList<ExperimentUnit> units = new ArrayList<ExperimentUnit>();
		
		String classifierType = "naiveBayes";
		int trainingSetPercentage = 60;
		
		ArrayList<Preprocessor> preprocessors = new ArrayList<Preprocessor>();
		preprocessors.add(new Lowercase());
		preprocessors.add(new NumberNormalization());
		preprocessors.add(new UrlNormalization());
		preprocessors.add(new WordsCleaner());
		preprocessors.add(new StopWordsRemoval());
		preprocessors.add(new EnglishStemmer());
		
		ArrayList<FilterCreator> filterCreators1 = new ArrayList<FilterCreator>();
		filterCreators1.add(new SenderFilterCreator());
		filterCreators1.add(new LabelFilterCreator());
		
		ArrayList<FilterCreator> filterCreators2 = new ArrayList<FilterCreator>();
		filterCreators2.add(new WordFrequencyFilterCreator());
		filterCreators2.add(new LabelFilterCreator());
		
		ArrayList<FilterCreator> filterCreators3 = new ArrayList<FilterCreator>();
		filterCreators3.add(new SenderFilterCreator());
		filterCreators3.add(new WordFrequencyFilterCreator());
		filterCreators3.add(new LabelFilterCreator());
		
//		ExperimentUnit unit = new ExperimentUnit(name, description, preprocessors, filterCreators)
		units.add(new ExperimentUnit("Sender Filter", "", preprocessors, filterCreators1, classifierType, trainingSetPercentage));
		units.add(new ExperimentUnit("WF Filter", "", preprocessors, filterCreators2, classifierType, trainingSetPercentage));
		units.add(new ExperimentUnit("Sender+WF Filters", "", preprocessors, filterCreators3, classifierType, trainingSetPercentage));
		
		return units;
	}

	public static void main(String[] args) throws Exception {
		ExperimentTunerIF tuner = new Experiment1();
		String[] usernames = new String[]{"sanders_r", "lokay_m", "beck-s"};
//		String[] usernames = new String[]{"sanders_r"};
		ExperimentRunner exp = new ExperimentRunner(tuner, usernames, "Sender and WF filters Combinations");
		exp.runExperiment();
	}
}
