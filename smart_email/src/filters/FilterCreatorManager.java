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
	
	public Filter[] getFilters(){
		return filters;
	}
}
