package preprocessors;

import general.Email;

public class EnglishStemmer implements Preprocessor {

	Stemmer stemmer = new Stemmer();
	@Override
	public void apply(Email email) {
		//process the subject
		String[] words = email.getSubject().split(" ");
		String subject = "";
		
		for (String word : words){
			stemmer.add(word.toCharArray(), word.length());
			stemmer.stem();
			subject += stemmer.toString() + " ";
		}
		
		email.setSubject(subject);
		
		//process the body
		words = email.getContent().split(" ");
		StringBuilder sb = new StringBuilder(email.getContent().length());
		
		for (String word : words){
			stemmer.add(word.toCharArray(), word.length());
			stemmer.stem();
			sb.append(stemmer.toString() + " ");
		}
		
		email.setContent(sb.toString());
	}

}
