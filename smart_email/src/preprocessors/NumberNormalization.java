package preprocessors;

import general.Email;

public class NumberNormalization implements Preprocessor{

	private final String NUMBER_TAG = " NUMBER ";
	
	@Override
	public void apply(Email email) {
		email.setContent(email.getContent().replaceAll("[-+]?[0-9]*\\.?[0-9]+", NUMBER_TAG));
	}

}
