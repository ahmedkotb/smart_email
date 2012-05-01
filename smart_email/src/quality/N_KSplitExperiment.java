package quality;

import java.util.ArrayList;

import filters.FilterCreator;
import filters.LabelFilterCreator;
import filters.SenderFilterCreator;
import filters.SizeFilterCreator;
import filters.SubjectFilterCreator;
import filters.ToFilterCreator;
import filters.WordFrequencyFilterCreator;

import preprocessors.EnglishStemmer;
import preprocessors.Lowercase;
import preprocessors.NumberNormalization;
import preprocessors.Preprocessor;
import preprocessors.StopWordsRemoval;
import preprocessors.UrlNormalization;
import preprocessors.WordsCleaner;

public class N_KSplitExperiment implements ExperimentTunerIF{

	@Override
	public ArrayList<ExperimentUnit> getExperimentUnits() {
		ArrayList<ExperimentUnit> units = new ArrayList<ExperimentUnit>();
		
		ArrayList<Preprocessor> preprocessors = new ArrayList<Preprocessor>();
		preprocessors.add(new Lowercase());
		preprocessors.add(new NumberNormalization());
		preprocessors.add(new UrlNormalization());
		preprocessors.add(new WordsCleaner());
		preprocessors.add(new StopWordsRemoval());
		preprocessors.add(new EnglishStemmer());
		
		int N = 100;
		for(int k=1; k<=5; k++){ 
			ArrayList<FilterCreator> filterCreators = new ArrayList<FilterCreator>();
			filterCreators.add(new SenderFilterCreator());
			filterCreators.add(new SizeFilterCreator());
			filterCreators.add(new ToFilterCreator());
			filterCreators.add(new WordFrequencyFilterCreator());
			filterCreators.add(new SubjectFilterCreator());
			filterCreators.add(new LabelFilterCreator());
			units.add(new ExperimentUnit(("("+N+", " + k+")"), "", preprocessors, filterCreators, N, k));
		}
		
		return units;
	}

	public static void main(String[] args) throws Exception {
		ExperimentTunerIF tuner = new N_KSplitExperiment();
		String username = "lokay_m";
		String[] algorithms = new String[]{"svm", "naiveBayes"};
		ExperimentRunner exp = new ExperimentRunner(tuner, algorithms, username);
		exp.runExperiment();
	}
}
