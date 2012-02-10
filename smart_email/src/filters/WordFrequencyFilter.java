package filters;

import java.util.ArrayList;
import weka.core.Attribute;
import weka.core.Instance;
import general.Email;

public class WordFrequencyFilter extends Filter{

	public WordFrequencyFilter(ArrayList<Attribute> atts, String[] options) {
		super(atts, options);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Instance makeInstance(Email email){
		// TODO Auto-generated method stub
		return null;
	}
}
