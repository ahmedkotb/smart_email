package preprocessors;

import general.Email;

public class UrlNormalization implements Preprocessor{

	private final String URL_TAG = " URL ";
	
	@Override
	public void apply(Email email) {
		//TODO: search for a stronger regex to handle www.url.com and 
		//other protocols like ftp .. etc
		email.setContent(email.getContent().replaceAll("(http|https)://[^\\s]*", URL_TAG));
	}

}
