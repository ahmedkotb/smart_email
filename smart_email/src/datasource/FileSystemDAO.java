package datasource;

import java.io.BufferedReader;
import java.io.FileFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import general.Email;

public class FileSystemDAO extends DAO {

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
			//System.out.println(subDir.getName());
			classes.add(subDir.getName());
		}
		return classes;
	}

	@Override
	public Email[] getClassifiedEmails(String labelName, int limit) {
		File dir = new File(this.datasetPath
				+ System.getProperty("file.separator") + labelName);

		//Email[] emails = new Email[limit];
		ArrayList<Email> emails = new ArrayList<Email>();
		int i = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			Email email = new Email();
			System.out.println(file);
			
			try{
				parseFile(file, email);
				email.setLabel(labelName);
				emails.add(email);
			} catch(Exception e){
				System.err.println("Format Error in file: " + file);
			}
			
			//emails[i] = email;
			//i++;
		}
		Email[] ar = new Email[emails.size()];
		emails.toArray(ar);
		return ar;
	}

	private void parseFile(File file, Email email) {
		BufferedReader br = null;
		try {
			//System.out.println("-----------------\n");
			br = new BufferedReader(new FileReader(file));
			String line = null;
			br.readLine();
			DateFormat df = new SimpleDateFormat(
					" EEE, d MMM yyyy HH:mm:ss Z (z)");
			String date = br.readLine().split("Date:")[1];
			Date emailDate = df.parse(date);

			String from = br.readLine().split("From:")[1].trim();

			ArrayList<String> to_addresses = new ArrayList<String>();
			String to = "";
			line = br.readLine();
			if (line.contains("To:")){
				to = line.split("To:")[1].trim();
				String[] tos = to.split(",");
				// Tue, 7 Nov 2000 06:10:00 -0800 (PST)
				while (!(line = br.readLine()).contains("Subject")) {
					for (int i = 0; i < tos.length; i++)
						to_addresses.add(tos[i]);

					tos = line.split(",");
				}
			}
			else
				System.err.print("systemDao#ParseFile No To: field");
			

			String subject = line.split("Subject:")[1].trim();
			System.out.println(subject);

			while (!((line = br.readLine()).contains("X-cc")));
				//System.out.println(line);

				
			for (int i = 0; i < 4; i++) {
				br.readLine();
			}
			String content = "";
			// System.out.println(file);
			
			while ((line = br.readLine()) != null) {
				content = content + line;
			}
			
			email.setContent(content);
			email.setFrom(from);
			email.setDate(emailDate);
			email.setSubject(subject);
			email.setTo(copyArrayList(to_addresses));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String[] copyArrayList(ArrayList<String> source) {

		String[] dest = new String[source.size()];

		for (int i = 0; i < source.size(); i++)
			dest[i] = source.get(i);
		return dest;
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
