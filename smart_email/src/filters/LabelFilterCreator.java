package filters;

import general.Email;

import java.util.ArrayList;
import java.util.HashSet;

import weka.core.Attribute;
import weka.core.FastVector;

public class LabelFilterCreator implements FilterCreator{

	@Override
	public Filter createFilter(Email[] emails) {
		HashSet<String> labels = new HashSet<String>();
		FastVector fvLabels = new FastVector();
		for(Email email : emails){
			String lbl = email.getLabel();
			if(!labels.contains(lbl)){
				labels.add(lbl);
				fvLabels.addElement(lbl);
			}
		}
				
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		atts.add(new Attribute("LabelAtt", fvLabels));
		
		return new LabelFilter(atts, null);
	}

}
