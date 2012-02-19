package datasource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

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

		//return available categories (directories only)
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
		
		for (int i=0;i<files.length;i++){
			String name = files[i].getName();
			fileNames[i] = Integer.parseInt(name.substring(0, name.length()-1));
		}
		
		Arrays.sort(fileNames);
		
		for (int i =fileNames.length-1; i > -1 ;--i){
			//check the limit
			if (emails.size() == limit)
				break;
			int number = fileNames[i];
			File file = new File(dir.getPath() + System.getProperty("file.separator") + number + ".");
			try{
				Email email = new Email();
				parseFile(file, email);
				
				email.setLabel(labelName);
				email.setSize(file.length());
				emails.add(email);
			} catch(Exception e){
				System.err.println("### Format Error in file: " + file);
			}
		}
		
		Email[] array = new Email[emails.size()];
		emails.toArray(array);
		return array;
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
			else{
				//System.err.println("systemDao#ParseFile No To: field");
			}
			

			String subject = line.split("Subject:")[1].trim();
//			System.out.println(subject);

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
			
			br.close();
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
