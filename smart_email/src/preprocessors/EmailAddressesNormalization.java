package preprocessors;

import java.io.IOException;

import javax.mail.MessagingException;

import general.Email;

public class EmailAddressesNormalization implements Preprocessor {

	private static final String ADDRESS_TAG = " EMAILADDR ";

	private static final String EMAIL_REGEX = ".+\\@.+\\..+";

	@Override
	public void apply(Email email) {
		// TODO Abbas please review
		try {
			email.setSubject(email.getSubject().replaceAll(EMAIL_REGEX,
					ADDRESS_TAG));
			Object content = ((String) email.getContent()).replaceAll(
					EMAIL_REGEX, ADDRESS_TAG);
			email.setContent(content, "text/plain");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
