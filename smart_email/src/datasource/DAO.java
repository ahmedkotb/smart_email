package datasource;

import java.io.IOException;
import java.util.ArrayList;

import general.Email;

public abstract class DAO {

	//IMAP Source format: IMAP:username:password
	// File Systems Source format: FileSystems:path
	public static DAO getInstance(String source){
		String[] sourceTokens = source.split(":");
		
		if (sourceTokens.length > 2)
			return new ImapDAO(sourceTokens[1], sourceTokens[2]);
		else 
			return new FileSystemDAO(sourceTokens[1]);
	}
	
	public abstract ArrayList<String> getClasses();
	
	public abstract Email[] getClassifiedEmails(String labelName, int limit);
	
	public abstract Email[] getUnclassified(int limit);
	
	public abstract void applyLabel(String emailId, String labelName);
	
	public static void main(String args[]) throws IOException {
       //ImapDAO imapdao=   (ImapDAO) DAO.getInstance("IMAP::");
       //imapdao.getClasses();
       //imapdao.getUnclassified(10);
	   FileSystemDAO filesystemdao=   (FileSystemDAO) DAO.getInstance("FileSystems:/media/e/");
	   filesystemdao.getClasses();

	}
}
