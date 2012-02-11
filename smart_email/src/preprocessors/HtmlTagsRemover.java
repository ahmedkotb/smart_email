package preprocessors;

import general.Email;

public class HtmlTagsRemover implements Preprocessor{

	@Override
	public void apply(Email email) {
		//TODO: might use a specialized tool instead of regex for html tag removal
		email.setContent(email.getContent().replaceAll("<[^<>]+>", " "));
	}

}
