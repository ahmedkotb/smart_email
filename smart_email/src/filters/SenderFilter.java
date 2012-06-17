package filters;

import java.util.ArrayList;

import javax.mail.MessagingException;

import weka.core.Attribute;
import general.Email;

public class SenderFilter extends Filter{
	private static final long serialVersionUID = 212064865800000L;
	
	public SenderFilter(ArrayList<Attribute> atts, String[] options) {
		super(atts, options);
	}

	@Override
	public double[] getAttValue(Email email){
		double[] ret;
		try {
			ret = new double[]{attributes.get(0).indexOfValue(email.getFrom()[0].toString())};
			if(ret[0]<0) ret[0]=attributes.get(0).indexOfValue("SenderAtt_Other"); // new sender that is not in the sender nominals list 
			return ret;

		} catch (MessagingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
