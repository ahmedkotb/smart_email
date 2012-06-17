package filters;

import java.util.ArrayList;

import general.Email;

public class FilterCreatorManager {
	private FilterCreator[] filterCreators;
	private Filter[] filters;

	public FilterCreatorManager(String[] filterCreatorsNames,
			ArrayList<Email> trainingSet) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		filterCreators = new FilterCreator[filterCreatorsNames.length];
		filters = new Filter[filterCreatorsNames.length];

		for (int i = 0; i < filterCreatorsNames.length; i++) {
			filterCreators[i] = (FilterCreator) Class.forName(
					filterCreatorsNames[i]).newInstance();
			filters[i] = filterCreators[i].createFilter(trainingSet);
		}
	}

	/**
	 * this constructor is only used for the testing phase (Experiments)
	 * 
	 * @param filterCreatorsList
	 *            : FilterCreator objects
	 * @param trainingSet
	 *            : Email Training Set
	 */
	public FilterCreatorManager(ArrayList<FilterCreator> filterCreatorsList,
			ArrayList<Email> trainingSet) {
		this.filterCreators = new FilterCreator[filterCreatorsList.size()];
		filterCreatorsList.toArray(this.filterCreators);
		filters = new Filter[filterCreators.length];

		for (int i = 0; i < filterCreators.length; i++) {
			filters[i] = filterCreators[i].createFilter(trainingSet);
		}
	}

	public Filter[] getFilters() {
		return filters;
	}
}
