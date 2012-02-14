package classification;

import weka.classifiers.trees.ADTree;
import weka.core.Instance;
import weka.core.Instances;

public class DecisionTreeClassifier extends Classifier{

	private static final long serialVersionUID = 555668137075221536L;
	private ADTree classifier;
	
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
		return classifier.distributionForInstance(instance);
	}

}
