package filters;

import general.Email;

import java.util.ArrayList;
import java.util.HashSet;

import weka.core.Attribute;

public class SenderFilterCreator implements FilterCreator {

	@Override
	public Filter createFilter(ArrayList<Email> emails) {
		String subject = "";
		try {
			HashSet<String> senders = new HashSet<String>();
			ArrayList<String> sendersVals = new ArrayList<String>();
			// in the calssification phase, if we found new sender, we will
			// assign it to SenderAtt_Other
			sendersVals.add("SenderAtt_Other");
			for (Email email : emails) {
				subject = email.getSubject();
				String sender = email.getFrom()[0].toString();
				if (!senders.contains(sender)) {
					senders.add(sender);
					sendersVals.add(sender);
				}
			}

			ArrayList<Attribute> atts = new ArrayList<Attribute>();
			atts.add(new Attribute("SenderAtt", sendersVals));

			return new SenderFilter(atts, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("BAD EMAIL SUBJ " + subject);
			return null;
		}
	}

}
