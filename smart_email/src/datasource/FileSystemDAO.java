package datasource;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
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
		File directory = new File(this.datasetPath);
		// Return available categories (directories only).
		File[] childDirectories = directory.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		for (File childDirectory : childDirectories)
			classes.add(childDirectory.getName());
		return classes;
	}

	@Override
	public Email[] getClassifiedEmails(String labelName, int limit) {
		try {
			File dir = new File(datasetPath
					+ System.getProperty("file.separator") + labelName);
			ArrayList<Email> emails = new ArrayList<Email>();
			File[] files = dir.listFiles();
			int[] fileNames = new int[files.length];
			for (int i = 0; i < files.length; i++) {
				String name = files[i].getName();
				fileNames[i] = Integer.parseInt(name.substring(0,
						name.length() - 1));
			}
			Arrays.sort(fileNames);
			for (int i = fileNames.length - 1; i > -1; --i) {
				// check the limit
				if (emails.size() == limit)
					break;
				int number = fileNames[i];
				File file = new File(dir.getPath()
						+ System.getProperty("file.separator") + number + ".");
				Session session = Session.getDefaultInstance(new Properties());
				InputStream inputStream = new FileInputStream(file);
				Email email = new Email(session, inputStream);
				email.setFileName(labelName);
				emails.add(email);
				inputStream.close();
			}
			Email[] array = new Email[emails.size()];
			emails.toArray(array);
			return array;
		} catch (Exception ex) {
			// Error in reading and parsing emails.
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public void applyLabel(long emailId, String labelName) {
		// TODO Implement this method.
	}

	@Override
	public Email[] getUnclassified(int limit) {
		// TODO Implement this method.
		return null;
	}
}
