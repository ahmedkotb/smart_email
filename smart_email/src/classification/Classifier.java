package classification;

import general.Dataset;
import general.FeatureVectorInstance;
import java.io.Serializable;

public abstract class Classifier implements Serializable{

	public static Classifier getClassifierByName(String classifierName, String[] options){
		//TODO
		return null;
	}
	
	public abstract double classifyFeatureVector(FeatureVectorInstance instance);
	
	public abstract void buildClassifier(Dataset trainingSet);
	
	public abstract double[] distributionForFeaturesVector(FeatureVectorInstance instance);
}
