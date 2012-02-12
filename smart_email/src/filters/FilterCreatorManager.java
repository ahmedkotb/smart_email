package filters;

import datasource.DAO;

public class FilterCreatorManager {
	FilterCreator[] filterCreators;
	Filter[] filters;
	
	public FilterCreatorManager(String[] filterCreatorsNames, DAO dao) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		filterCreators = new FilterCreator[filterCreatorsNames.length];
		filters = new Filter[filterCreatorsNames.length];
		
		for(int i=0; i<filterCreatorsNames.length; i++){
			filterCreators[i] = (FilterCreator) Class.forName(filterCreatorsNames[i]).newInstance();
			//TODO
			
			
			//XXX what about the limit, and will i need to loop and get the unclassified email into chunks, or just set the limit to High value, this will require a func. in the DAO that takes a starting index
//			int limit = Integer.MAX_VALUE;
//			Email[] training = dao.getClassifiedEmails(label, limit);
//			filters[i] = filterCreators[i].createFilter(emails);
		}
	}
	
	public Filter[] getFilters(){
		return filters;
	}
}
