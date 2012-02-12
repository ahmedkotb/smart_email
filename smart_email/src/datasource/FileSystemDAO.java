package datasource;

import java.io.FileFilter;
import java.io.File;
import java.util.ArrayList;

import general.Email;

public class FileSystemDAO extends DAO{

	private String datasetPath;
	
	public FileSystemDAO(String datasetPath) {
		this.datasetPath = datasetPath;
	}

	@Override
	public ArrayList<String> getClasses() {
		ArrayList<String> classes = new ArrayList<String>(50);
		File dir = new File(this.datasetPath);  
		   
		File[] subDirs = dir.listFiles(new FileFilter() {  
		    public boolean accept(File pathname) {  
		        return pathname.isDirectory();  
		    }  
		});  
		   
		for (File subDir : subDirs) {  
		    System.out.println(subDir.getName());
		    classes.add(subDir.getName());
		}  
		return classes;
	}

	@Override
	public Email[] getClassifiedEmails(String labelName, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Email[] getUnclassified(int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyLabel(String emailId, String labelName) {
		// TODO Auto-generated method stub
		
	}

}
