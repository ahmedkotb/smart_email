package filters;

import general.Email;

import java.util.ArrayList;
import java.util.HashSet;

import weka.core.Attribute;
import weka.core.FastVector;

public class SenderFilterCreator implements FilterCreator{

	@Override
	public Filter createFilter(Email[] emails) {
		HashSet<String> senders = new HashSet<String>();
		FastVector fvSenders = new FastVector();
		//XXX in the calssification phase, if we found new sender, we will assign it to SenderAtt_Other
		fvSenders.addElement("SenderAtt_Other");
		for(Email email : emails){
			String sender = email.getFrom();
			if(! senders.contains(sender)){
				senders.add(sender);
				fvSenders.addElement(sender);
			}
		}
				
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		atts.add(new Attribute("SenderAtt", fvSenders));
		
		return new SenderFilter(atts, null);
	}

}
