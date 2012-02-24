package preprocessors;

import general.Email;

/**
 * The Lowercase preprocessor is used to convert each word in the email
 * body and subject to lower case
 * @author ahmedkotb
 *
 */
public class Lowercase implements Preprocessor{

	@Override
	public void apply(Email email) {
		// TODO Abbas please review
		//convert email body and subject to lowercase
		try{
		email.setSubject(email.getSubject().toLowerCase());
			email.setContent(((String) email.getContent()).toLowerCase(),"text/plain");
	}catch(Exception ex) {
		//ignore
		}
	}

}
