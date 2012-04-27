package classification;

import weka.classifiers.functions.SMO;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

public class SVMClassifier extends Classifier{

	private static final long serialVersionUID = -9105170187757147863L;
	private SMO classifier;
	
	public SVMClassifier(){
		classifier = new SMO();
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

	@Override
	public void trainOnInstance(Instance Instance)
			throws UnsupportedOperationException {
		
		throw new UnsupportedOperationException();
	}

}
