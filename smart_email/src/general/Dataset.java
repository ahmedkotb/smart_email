package general;

import java.io.Reader;

public class Dataset {
	private String name;
	
	public Dataset(Reader reader){
		//TODO
	}
	
	public Dataset(String name, FeatureVectorInstance features){
		//TODO
	}
	
	public String getName(){
		return name;
	}

	public void add(FeatureVectorInstance instance){
		//TODO
	}
	
	public FeatureAttribute getAttribute(int index){
		//TODO
		return null;
	}
	
	public FeatureAttribute getATtribute(String attributeName){
		//TODO
		return null;
	}
	
	public boolean checkInstance(FeatureVectorInstance instance){
		//TODO
		return false;
	}
	
	public FeatureAttribute getClassAttribute(){
		//TODO
		return null;
	}
}
