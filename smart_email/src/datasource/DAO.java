package datasource;

import general.Email;

public abstract class DAO {

	public static DAO getInstance(String source){
		// TODO
		return null;
	}
	
	public abstract String[] getClasses();
	
	public abstract Email[] getClassifiedEmails(String labelName, int limit);
	
	public abstract Email[] getUnclassified(int limit);
	
	public abstract void applyLabel(String emailId, String labelName);
}
