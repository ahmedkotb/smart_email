package preprocessors;

import general.Email;

public class EmailAddressesNormalization implements Preprocessor {

	private static final String ADDRESS_TAG = " EMAILADDR ";
	
	private static final String EMAIL_REGEX = ".+\\@.+\\..+";
	
	@Override
	public void apply(Email email) {
		email.setSubject(email.getSubject().replaceAll(EMAIL_REGEX, ADDRESS_TAG));
		email.setContent(email.getContent().replaceAll(EMAIL_REGEX, ADDRESS_TAG));
	}

}
