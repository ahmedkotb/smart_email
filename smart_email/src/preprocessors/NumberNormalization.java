package preprocessors;

import general.Email;

public class NumberNormalization implements Preprocessor{

	private static final String NUMBER_TAG = " NUMBER ";
	
	private static final String NUMBER_REGEX = "[-+]?[0-9]*\\.?[0-9]+";
	
	@Override
	public void apply(Email email) {
		email.setSubject(email.getSubject().replaceAll(NUMBER_REGEX, NUMBER_TAG));
		email.setContent(email.getContent().replaceAll(NUMBER_REGEX, NUMBER_TAG));
	}

}
