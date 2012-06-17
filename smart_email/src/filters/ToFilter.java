package filters;

import java.util.ArrayList;

import javax.mail.Message;
import javax.mail.MessagingException;

import weka.core.Attribute;
import general.Email;

public class ToFilter extends Filter{
	
	private static final long serialVersionUID = 4767532337209772049L;

	public ToFilter(ArrayList<Attribute> atts, String[] options) {
		super(atts, options);
	}

	@Override
	public double[] getAttValue(Email email){
		double[] ret;
		// TODO: Moustafa Please review, could we handle multiple reveivers?
		try {
			if(email.getRecipients(Message.RecipientType.TO) == null) //To field is empty
				ret = new double[]{-1};
			else
				ret = new double[]{attributes.get(0).indexOfValue(email.getRecipients(Message.RecipientType.TO)[0].toString())};
			
			if(ret[0]<0) ret[0]=attributes.get(0).indexOfValue("ToAtt_Other"); // new receiver that is not in the sender nominals-list 
			return ret;

		} catch (MessagingException e) {
			e.printStackTrace();
			return new double[]{attributes.get(0).indexOfValue("ToAtt_Other")};
		}
	}
}
