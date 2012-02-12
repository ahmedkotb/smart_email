package filters;

import java.util.ArrayList;
import weka.core.Attribute;
import general.Email;

public class SenderFilter extends Filter{
	private static final long serialVersionUID = 212064865800000L;
	
	public SenderFilter(ArrayList<Attribute> atts, String[] options) {
		super(atts, options);
	}

	@Override
	public double[] getAttValue(Email email){
		return new double[]{attributes.get(0).indexOfValue(email.getFrom())};
	}
}
