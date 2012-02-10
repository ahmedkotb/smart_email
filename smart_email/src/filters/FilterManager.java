package filters;

import weka.core.Instance;
import weka.core.Instances;
import general.Email;
import java.util.ArrayList;

public class FilterManager {
	
	private Filter[] filters;
	
	public FilterManager(Filter[] filters){
		this.filters = filters;
		//TODO
	}
	
	public Instances getDataset(ArrayList<Email> emails){
		//TODO
		return null;
	}
	
	public Instance gitInstance(Email email){
		//TODO
		return null;
	}
}
