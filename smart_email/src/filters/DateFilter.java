package filters;

import java.util.ArrayList;
import weka.core.Attribute;
import general.Email;

public class DateFilter extends Filter{
	private static final long serialVersionUID = 939544392420504L;
	
	public DateFilter(ArrayList<Attribute> atts, String[] options) {
		super(atts, options);
	}

	@Override
	public double[] getAttValue(Email email){
		//date feature will be represented as a numeric attribute (the number of milliseconds since January 1, 1970)
		return new double[]{email.getDate().getTime()};
	}
}
