package classification;

import java.io.Serializable;
import weka.core.Instance;
import weka.core.Instances;

public abstract class Classifier implements Serializable{

	public static Classifier getClassifierByName(String classifierName, String[] options){
		//TODO
		return null;
	}
	
	public abstract double classifyInstance(Instance instance);
	
	public abstract void buildClassifier(Instances trainingSet);
	
	public abstract double[] distributionForFeaturesVector(Instance instance);
}
