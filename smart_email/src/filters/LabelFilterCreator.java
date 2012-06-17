package filters;

import general.Email;

import java.util.ArrayList;
import java.util.HashSet;

import javax.mail.MessagingException;

import weka.core.Attribute;

public class LabelFilterCreator implements FilterCreator{

	@Override
	public Filter createFilter(ArrayList<Email> emails) {
		HashSet<String> labels = new HashSet<String>();
		ArrayList<String> fvLabels = new ArrayList<String>();
		for(Email email : emails){
			String lbl = null;
			try {
				// TODO: Moustafa: Can we handle more than one label?
				lbl = email.getHeader("X-label")[0];
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			if(!labels.contains(lbl)){
				labels.add(lbl);
				fvLabels.add(lbl);
			}
		}
				
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		atts.add(new Attribute("LabelAtt", fvLabels));
		
		return new LabelFilter(atts, null);
	}

}
