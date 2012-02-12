package filters;

import general.Email;

import java.util.ArrayList;

import weka.core.Attribute;

public class DateFilterCreator implements FilterCreator{

	@Override
	public Filter createFilter(Email[] emails) {
		ArrayList<Attribute> atts = new ArrayList<Attribute>(1);
		atts.add(new Attribute("DateAttribute"));
		return new DateFilter(atts, null); 
	}

}
