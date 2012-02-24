package datasource;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import javax.mail.Session;

import general.Email;

public class FileSystemDAO extends DAO {

	private String datasetPath;

	public FileSystemDAO(String datasetPath) {
		this.datasetPath = datasetPath;
	}

	@Override
	public ArrayList<String> getClasses() {
		ArrayList<String> classes = new ArrayList<String>();
		File dir = new File(this.datasetPath);

		// return available categories (directories only)
		File[] subDirs = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		for (File subDir : subDirs)
			classes.add(subDir.getName());

		return classes;
	}

	@Override
	public Email[] getClassifiedEmails(String labelName, int limit) {
		File dir = new File(this.datasetPath
				+ System.getProperty("file.separator") + labelName);

		ArrayList<Email> emails = new ArrayList<Email>();

		File[] files = dir.listFiles();
		int[] fileNames = new int[files.length];

		for (int i = 0; i < files.length; i++) {
			String name = files[i].getName();
			fileNames[i] = Integer
					.parseInt(name.substring(0, name.length() - 1));
		}

		Arrays.sort(fileNames);

		for (int i = fileNames.length - 1; i > -1; --i) {
			// check the limit
			if (emails.size() == limit)
				break;
			int number = fileNames[i];
			File file = new File(dir.getPath()
					+ System.getProperty("file.separator") + number + ".");
			try {
				
				Session session = Session.getDefaultInstance(new Properties());
				InputStream is = null;
				try {
					is = new FileInputStream(file);
				} catch (FileNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				Email email = new Email(session, is);
				is.close();
				email.setFileName(labelName);
				emails.add(email);
			} catch (Exception e) {
				System.err.println("### Format Error in file: " + file);
			}
		}

		Email[] array = new Email[emails.size()];
		emails.toArray(array);
		return array;
	}

	@Override
	public Email[] getUnclassified(int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyLabel(long emailId, String labelName) {
		// TODO Auto-generated method stub

	}

}
