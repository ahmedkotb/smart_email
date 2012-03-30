package filters;

import java.util.ArrayList;

import general.Email;

public interface FilterCreator {

	public Filter createFilter(ArrayList<Email> emails);
	
}
