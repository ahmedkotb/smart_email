package quality;

import java.util.ArrayList;

import filters.FilterCreator;

import preprocessors.Preprocessor;
import training.TrainingType;

public class ExperimentUnit {
	private String title;
	private String description;
	private String classifierType;
	private TrainingType trainingType;
	private int trainingSetPercentage;
	private int N;
	private int K;
	private ArrayList<Preprocessor> preprocessors;
	private ArrayList<FilterCreator> filterCreators;
	
	public ExperimentUnit(String title, String description, ArrayList<Preprocessor> preprocessors, ArrayList<FilterCreator> filterCreators, String classifierType, int trainingSetPercentage) {
		this.title = title;
		this.description = description;
		this.preprocessors = preprocessors;
		this.filterCreators = filterCreators;
		this.classifierType = classifierType;
		this.trainingSetPercentage = trainingSetPercentage;
		this.trainingType = TrainingType.PERCENTAGE;
	}

	public ExperimentUnit(String title, String description, ArrayList<Preprocessor> preprocessors, ArrayList<FilterCreator> filterCreators, String classifierType, int N, int K) {
		this.title = title;
		this.description = description;
		this.preprocessors = preprocessors;
		this.filterCreators = filterCreators;
		this.classifierType = classifierType;
		this.N = N;
		this.K = K;
		this.trainingType = TrainingType.KN_PARTITIONS;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getClassifierType(){
		return this.classifierType;
	}
	
	public void setClassifierType(String classifierType){
		this.classifierType = classifierType;
	}
	
	public int getTrainingSetPercentage(){
		return this.trainingSetPercentage;
	}
	
	public void setTrainingSetPercentage(int trainingSetPercentage){
		this.trainingSetPercentage = trainingSetPercentage;
	}

	public int getN(){
		return this.N;
	}
	
	public void setN(int N){
		this.N = N;
	}
	
	public int getK(){
		return this.K;
	}
	
	public void setK(int K){
		this.K = K;
	}
	
	public TrainingType getTrainingType(){
		return this.trainingType;
	}
	
	public void setTrainingType(TrainingType trainingType){
		this.trainingType = trainingType;
	}
	
	public ArrayList<Preprocessor> getPreprocessors() {
		return preprocessors;
	}

	public void setPreprocessors(ArrayList<Preprocessor> preprocessors) {
		this.preprocessors = preprocessors;
	}

	public ArrayList<FilterCreator> getFilterCreators() {
		return filterCreators;
	}

	public void setFilterCreators(ArrayList<FilterCreator> filterCreators) {
		this.filterCreators = filterCreators;
	}
}
