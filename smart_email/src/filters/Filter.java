package filters;

import java.util.ArrayList;
import weka.core.Attribute;
import weka.core.Instance;
import general.Email;

public abstract class Filter {
	
	private String[] options;
	private ArrayList<Attribute> atts;
	
	public Filter(ArrayList<Attribute> atts, String[] options){
		this.atts = atts;
		this.options = options;
		//TODO
	}
		
	public ArrayList<Attribute> getAttributes(){
		return this.atts;
	}
	
	public abstract Instance makeInstance(Email email);

}
