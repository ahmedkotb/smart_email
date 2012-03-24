package datasource;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import general.Email;

public class FileSystemDAO extends DAO {

	private static final String FILE_SEPARATOR = "file.separator";
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
	public ArrayList<Email> getClassifiedEmails(String labelName, int limit) {
		try {
			File directory = new File(datasetPath
					+ System.getProperty(FILE_SEPARATOR) + labelName);
			ArrayList<Email> emails = new ArrayList<Email>();
			File[] files = directory.listFiles();
			int[] fileNames = new int[files.length];
			for (int i = 0; i < files.length; i++) {
				String name = files[i].getName();
				fileNames[i] = Integer.parseInt(name.substring(0,
						name.length() - 1));
			}
			Arrays.sort(fileNames);
			for (int i = fileNames.length - 1; emails.size() < limit && i >= 0; --i) {
				int number = fileNames[i];
				File file = new File(directory.getPath()
						+ System.getProperty("file.separator") + number + ".");
				Session session = Session.getDefaultInstance(new Properties());
				InputStream inputStream = new FileInputStream(file);
				Message message = new MimeMessage(session, inputStream);
				Email email = new Email(message);
				email.setHeader("X-label", labelName);
				emails.add(email);
				inputStream.close();
			}
			return emails;
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
	public ArrayList<Email> getUnclassified(int limit) {
		// TODO Implement this method.
		return null;
	}
}
