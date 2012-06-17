package preprocessors;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import general.Email;

/**
 * This preprocessor is used to remove stopwords from email body and subject
 * 
 * @author ahmedkotb
 * 
 */
public class StopWordsRemoval implements Preprocessor {

	private HashSet<String> stopwords;
	// stop words list path : http://www.lextek.com/manuals/onix/stopwords1.html
	private static final String stopwordsFileName = "english_stopwords.txt";

	public StopWordsRemoval() {
		try {
			stopwords = loadStopWords("res"
					+ System.getProperty("file.separator") + stopwordsFileName);
		} catch (IOException e) {
			System.err.println("Couldn't load stopwords list");
			e.printStackTrace();
		}
	}

	@Override
	public void apply(Email email) {
		try {
			// process the subject
			String[] words = email.getSubject().split(" ");
			String subject = "";
			for (String word : words) {
				if (!stopwords.contains(word))
					subject += word + " ";
			}
			// TODO Abbas please review
			email.setSubject(subject);
			// process the body
			words = ((String) email.getContent()).split(" ");
			StringBuilder sb = new StringBuilder(
					((String) email.getContent()).length());

			for (String word : words) {
				if (!stopwords.contains(word))
					sb.append(word + " ");
			}

			email.setContent(sb.toString(), "text/plain");
		} catch (Exception ex) {
			// ignore
		}
	}

	private HashSet<String> loadStopWords(String filename) throws IOException {
		HashSet<String> stopwords = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String word;
		while ((word = br.readLine()) != null) {
			stopwords.add(word);
		}
		return stopwords;
	}

}
