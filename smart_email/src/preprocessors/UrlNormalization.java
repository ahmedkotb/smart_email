package preprocessors;

import general.Email;

public class UrlNormalization implements Preprocessor{

	private static final String URL_TAG = " URL ";
	
	//TODO: search for a stronger regex to handle www.url.com and 
	private static final String URL_REGEX = "(http|https)://[^\\s]*";
	
	@Override
	public void apply(Email email) {
		//other protocols like ftp .. etc
		email.setSubject(email.getSubject().replaceAll(URL_REGEX, URL_TAG));
		email.setContent(email.getContent().replaceAll(URL_REGEX, URL_TAG));
	}

}
