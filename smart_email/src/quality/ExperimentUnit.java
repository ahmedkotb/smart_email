package quality;

import java.util.ArrayList;

import filters.FilterCreator;

import preprocessors.Preprocessor;

public class ExperimentUnit {
	private String name;
	private String description;
	private ArrayList<Preprocessor> preprocessors;
	private ArrayList<FilterCreator> filterCreators;
	
	public ExperimentUnit(String name, String description,
			ArrayList<Preprocessor> preprocessors,
			ArrayList<FilterCreator> filterCreators) {
		super();
		this.name = name;
		this.description = description;
		this.preprocessors = preprocessors;
		this.filterCreators = filterCreators;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
