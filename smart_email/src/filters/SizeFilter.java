package filters;

import java.util.ArrayList;

import javax.mail.MessagingException;

import weka.core.Attribute;
import general.Email;

public class SizeFilter extends Filter {

	public SizeFilter(ArrayList<Attribute> atts, String[] options) {
		super(atts, options);
	}

	private static final long serialVersionUID = 2816588538309219702L;

	@Override
	public double[] getAttValue(Email email) {
		try {
			double size = 0.0 + email.getSize();
			return new double[] { size };
		} catch (MessagingException e) {
			e.printStackTrace();
			return new double[] { -1.0 };
		}
	}

}
