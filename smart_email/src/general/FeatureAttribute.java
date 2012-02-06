package general;

import java.util.ArrayList;
import java.util.Enumeration;


public class FeatureAttribute {

	public FeatureAttribute(String name){
		//TODO
	}
	
	public FeatureAttribute(String name, ArrayList<String> nominals){
		//TODO
	}
	
	public boolean isNominal(){
		//TODO
		return false;
	}
	
	public boolean isNumeric(){
		//TODO
		return false;
	}
	
	public FeatureType getType(){
		//TODO
		return null;
	}
	
	public Enumeration<String> enumerateValues(){
		//XXX I'm not sure of this function prototype! 
		
		//TODO
		return null;
	}
	
	public String getValue(int index){
		//TODO
		
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	public int getValuesNumber(){
		//TODO
		return 0;
	}
	
	public String getName(){
		//TODO
		return null;
	}
	
	public int indexOfValue(String value){
		//TODO;
		return 0;
	}
	
	public int addStringValue(String value){
		//TODO
		return 0;
	}
	
	public void copy(){
		//TODO
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
}
