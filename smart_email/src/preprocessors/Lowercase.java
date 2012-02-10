package preprocessors;

import general.Email;

/**
 * the lowercase preprocessor is used to convert each word in the email
 * body and subject to lower case
 * @author ahmedkotb
 *
 */
public class Lowercase implements Preprocessor{

	@Override
	public void apply(Email email) {
		//convert email body and subject to lowercase
		email.setSubject(email.getSubject().toLowerCase());
		email.setContent(email.getContent().toLowerCase());
	}

}
