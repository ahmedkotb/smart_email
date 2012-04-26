package filters;

import general.Email;

import java.util.ArrayList;
import java.util.HashSet;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import weka.core.Attribute;
import weka.core.FastVector;

public class ToFilterCreator implements FilterCreator {

	@Override
	public Filter createFilter(ArrayList<Email> emails) {
		HashSet<String> receivers = new HashSet<String>();
		FastVector fvReceivers = new FastVector();
		// in the calssification phase, if we found new receiver, we will
		// assign it to ToAtt_Other
		fvReceivers.addElement("ToAtt_Other");
		for (Email email : emails) {
			// TODO: Moustafa please review, could we handle multiple
			// receivers??
			Address[] recipients = null;
			try{
				recipients = email.getRecipients(Message.RecipientType.TO);
			} catch(AddressException aex){
				System.err.println("Address Exception!");
			} catch (MessagingException e) {
				System.err.println("Exception while retreiving TO field");
				e.printStackTrace();
			}
			
			if(recipients != null){
				for(int i=0; i<recipients.length; i++){
					String receiver = recipients[i].toString();
	
					if (!receivers.contains(receiver)) {
						receivers.add(receiver);
						fvReceivers.addElement(receiver);
					}
				}
			}			
		}

		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		atts.add(new Attribute("ToAtt", fvReceivers));
		System.err.println("Num Recipients = " + receivers.size());

		return new ToFilter(atts, null);
	}
}
