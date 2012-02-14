package filters;

import general.Email;

public interface FilterCreator {

	public Filter createFilter(Email[] emails);
	
}
