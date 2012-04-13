package filters;

import java.io.Serializable;
import java.util.ArrayList;
import weka.core.Attribute;
import general.Email;

public abstract class Filter implements Serializable{
	
	private static final long serialVersionUID = 382547736495835L;

	protected String[] options;
	protected ArrayList<Attribute> attributes;
	
	public Filter(ArrayList<Attribute> atts, String[] options){
		this.attributes = atts;
		this.options = options;
	}
		
	public ArrayList<Attribute> getAttributes(){
		return this.attributes;
	}
	
	public abstract double[] getAttValue(Email email);

}
