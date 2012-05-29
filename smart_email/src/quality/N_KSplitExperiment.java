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
	private static String[] usernames = new String[]{"beck-s", "farmer-d", "kaminski-v", "kitchen-l", "lokay_m", "sanders_r", "williams-w3"};
	private static int[] emailsCount = new int[]{1971, 3672, 4477, 4015, 2493, 1188, 2769};
	private static int currentUserIndex = 4;
	
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
		int K = emailsCount[currentUserIndex]/N;
		for(int k=1; k<K; k++){ 
			ArrayList<FilterCreator> filterCreators = new ArrayList<FilterCreator>();
			filterCreators.add(new SenderFilterCreator());
			filterCreators.add(new SizeFilterCreator());
			filterCreators.add(new ToFilterCreator());
			filterCreators.add(new WordFrequencyFilterCreator());
			filterCreators.add(new SubjectFilterCreator());
			filterCreators.add(new LabelFilterCreator());
			String title = ((k%2)==0? "" : "\n\n")+(N*k);
			units.add(new ExperimentUnit(title, "", preprocessors, filterCreators, N, k));
			System.gc();
		}
		
		return units;
	}

	public static void main(String[] args) throws Exception {
		ExperimentTunerIF tuner = new N_KSplitExperiment();
//		for(int i=0; i<usernames.length; i++){
		for(int i=2; i<=2; i++){
			currentUserIndex = i;
			String username = usernames[i];
			String[] algorithms = new String[]{"svm", "naiveBayes"};
			ExperimentRunner exp = new ExperimentRunner(tuner, algorithms, username);
			exp.runExperiment();
		}
	}
}
