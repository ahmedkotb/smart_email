package filters;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.FastVector;
import general.Email;

/**
 * 
 * @author Amr Sharaf
 *
 */
public class SizeFilterCreator implements FilterCreator {

	// Attribute name for the email size feature.
	private static final String SIZE_ATTRIBUTE = "SizeAttribute";
	private final int NUM_CLASSES = 20;
	private final int COMPRESSION_FACTOR = 500;
	
	@Override
	public Filter createFilter(ArrayList<Email> emails) {
		// Prepare for adding a new attribute for the email size.
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(1);
		FastVector fv = new FastVector(NUM_CLASSES);
		for(int i=0; i<=NUM_CLASSES; i++)
			fv.addElement(String.valueOf(i));
		attributes.add(new Attribute(SIZE_ATTRIBUTE, fv));
		// Return a new filter after adding the attribute type and given

		String[] options = new String[]{NUM_CLASSES + "", COMPRESSION_FACTOR + ""};
		return new SizeFilter(attributes, options); 
	}

}
