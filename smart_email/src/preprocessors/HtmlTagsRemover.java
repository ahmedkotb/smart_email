package preprocessors;

import general.Email;

public class HtmlTagsRemover implements Preprocessor {

	// TODO: might use a specialized tool instead of regex for html tag removal
	public static final String TAG_REGEX = "<[^<>]+>";

	@Override
	public void apply(Email email) {
		// TODO Abbas please review
		try {
			email.setSubject(email.getSubject().replaceAll(TAG_REGEX, " "));
			email.setContent(
					((String) email.getContent()).replaceAll(TAG_REGEX, " "),
					"text/plain");
		} catch (Exception ex) {
			//ignore
		}
	}

}
