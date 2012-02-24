package preprocessors;

import general.Email;

public class EnglishStemmer implements Preprocessor {

	Stemmer stemmer = new Stemmer();

	@Override
	public void apply(Email email) {
		try {
			// process the subject
			String[] words = email.getSubject().split(" ");
			String subject = "";

			for (String word : words) {
				stemmer.add(word.toCharArray(), word.length());
				stemmer.stem();
				subject += stemmer.toString() + " ";
			}
			// TODO Abbas please review
			email.setSubject(subject);

			// process the body
			words = ((String) email.getContent()).split(" ");
			StringBuilder sb = new StringBuilder(
					((String) email.getContent()).length());

			for (String word : words) {
				stemmer.add(word.toCharArray(), word.length());
				stemmer.stem();
				sb.append(stemmer.toString() + " ");
			}

			email.setContent(sb.toString(), "text/plain");
		} catch (Exception ex) {
			// ignore
		}
	}
}
