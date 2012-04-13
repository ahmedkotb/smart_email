package quality;

import java.util.ArrayList;

import filters.FilterCreator;

import preprocessors.Preprocessor;

public class ExperimentUnit {
	private String title;
	private String description;
	private String classifierType;
	private int trainingSetPercentage;
	private ArrayList<Preprocessor> preprocessors;
	private ArrayList<FilterCreator> filterCreators;
	
	public ExperimentUnit(String title, String description, ArrayList<Preprocessor> preprocessors, ArrayList<FilterCreator> filterCreators, String classifierType, int trainingSetPercentage) {
		this.title = title;
		this.description = description;
		this.preprocessors = preprocessors;
		this.filterCreators = filterCreators;
		this.classifierType = classifierType;
		this.trainingSetPercentage = trainingSetPercentage;
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
