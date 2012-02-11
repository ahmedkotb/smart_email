package preprocessors;

import general.Email;

public interface Preprocessor {
	
	/**
	 * Applies the preprocessor on the given email
	 * @param email the email to be processed
	 */
	void apply(Email email);
}
