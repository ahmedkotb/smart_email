package general;

import java.util.ArrayList;

public class FeatureVectorInstance {
	private int classIndex;
	private ArrayList<FeatureAttribute> attributes;
	
	public FeatureVectorInstance(int numOfFeatures){
		//TODO
	}
	
	public int getClassIndex(){
		return this.classIndex;
	}
	
	public void setClassIndex(int index){
		this.classIndex = index;
	}
	
	public boolean isMissingClass(){
		//TODO
		return true;
	}
	
	public double getClassValue(){
		//XXX i think we should change it to return int, not double
		
		//TODO
		return 0.0;
	}
	
	public void addFeatureAttribute(int index, FeatureAttribute attribute){
		//XXX make sure to update the classIndex accordingly
		//TODO
	}
	
	public void removeFeatureAttribute(int index){
		//XXX make sure to update the classIndex accordingly
		//TODO
	}
}
