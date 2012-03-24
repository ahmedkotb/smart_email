package filters;

import general.Email;

public class FilterCreatorManager {
	private FilterCreator[] filterCreators;
	private Filter[] filters;

	//TODO : catching thrown exceptions
	public FilterCreatorManager(String[] filterCreatorsNames, Email[] trainingSet) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		filterCreators = new FilterCreator[filterCreatorsNames.length];
		filters = new Filter[filterCreatorsNames.length];

		for(int i=0; i<filterCreatorsNames.length; i++){
			filterCreators[i] = (FilterCreator) Class.forName(filterCreatorsNames[i]).newInstance();						
			filters[i] = filterCreators[i].createFilter(trainingSet);
		}
	}
	
	/**
	 * this constructor is only used for the testing phase (Experiments)
	 * @param filterCreators: FilterCreator objects
	 * @param trainingSet: Email Training Set
	 */
	public FilterCreatorManager(FilterCreator[] filterCreators, Email[] trainingSet){
		this.filterCreators = filterCreators;
		filters = new Filter[filterCreators.length];
		
		for(int i=0; i<filterCreators.length; i++){
			filters[i] = filterCreators[i].createFilter(trainingSet);
		}
	}
	
	public Filter[] getFilters(){
		return filters;
	}
}
