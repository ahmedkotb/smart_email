package filters;

import java.util.ArrayList;

import weka.core.Attribute;
import general.Email;

/**
 * 
 * @author Amr Sharaf
 *
 */
public class SizeFilterCreator implements FilterCreator {

	// Attribute name for the email size feature.
	private static final String SIZE_ATTRIBUTE = "SizeAttribute";
	
	@Override
	public Filter createFilter(ArrayList<Email> emails) {
		// Prepare for adding a new attribute for the email size.
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(1);
		attributes.add(new Attribute(SIZE_ATTRIBUTE));
		// Return a new filter after adding the attribute type and given
		// no options.
		return new SizeFilter(attributes, null); 
	}

}
