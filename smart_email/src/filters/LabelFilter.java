package filters;

import java.util.ArrayList;

import javax.mail.MessagingException;

import weka.core.Attribute;
import general.Email;

public class LabelFilter extends Filter{
	private static final long serialVersionUID = 862933061908936L;
	
	public LabelFilter(ArrayList<Attribute> atts, String[] options) {
		super(atts, options);
	}

	@Override
	public double[] getAttValue(Email email){
		// TODO @Moustafa: can we handle more than one label?
		try {
			return new double[]{attributes.get(0).indexOfValue(email.getHeader("X-label")[0])};
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
