package classification;

import weka.classifiers.trees.DecisionStump;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

public class DecisionTreeClassifier extends Classifier{

	private static final long serialVersionUID = 555668137075221536L;
	
	//private ADTree classifier;
	private DecisionStump classifier;
	
	public DecisionTreeClassifier(){
		//classifier = new ADTree();
		classifier = new DecisionStump();
	}
	
	@Override
	public double classifyInstance(Instance instance) {
		try {
			return classifier.classifyInstance(instance);
		} catch (Exception e) {
			e.printStackTrace();
			return -1.0;
		}
	}

	@Override
	public void buildClassifier(Instances trainingSet) {
		try {
			classifier.buildClassifier(trainingSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public double[] distributionForFeaturesVector(Instance instance) {
		try {
			return classifier.distributionForInstance(instance);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		try {
			return classifier.distributionForInstance(instance);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Capabilities getCapabilities() {
		return classifier.getCapabilities();
	}

}
