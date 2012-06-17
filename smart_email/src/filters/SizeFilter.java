package filters;

import java.util.ArrayList;

import javax.mail.MessagingException;

import weka.core.Attribute;
import general.Email;

public class SizeFilter extends Filter {
	private int numClasses;
	private int compressionFactor;
	
	public SizeFilter(ArrayList<Attribute> atts, String[] options) {
		super(atts, options);
		this.numClasses = Integer.parseInt(options[0]);
		this.compressionFactor = Integer.parseInt(options[1]);
	}

	private static final long serialVersionUID = 2816588538309219702L;

	@Override
	public double[] getAttValue(Email email) {
		try {
			int compressedSize = email.getSize() / compressionFactor;
			int sizeClass = (compressedSize < numClasses? compressedSize : numClasses);
			return new double[] {sizeClass};
		} catch (MessagingException e) {
			e.printStackTrace();
			return new double[] { -1.0 };
		}
	}

}
