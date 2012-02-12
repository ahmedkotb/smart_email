package preprocessors;

import general.Email;

public class EnglishStemmer implements Preprocessor {

	Stemmer stemmer = new Stemmer();
	@Override
	public void apply(Email email) {
		String[] words = email.getContent().split(" ");
		StringBuilder sb = new StringBuilder(email.getContent().length());
		
		for (String word : words){
			stemmer.add(word.toCharArray(), word.length());
			stemmer.stem();
			sb.append(stemmer.toString() + " ");
		}
		
		email.setContent(sb.toString());
	}

}
