package classification;

import moa.core.InstancesHeader;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public abstract class OnlineClassifier extends Classifier{

	private static final long serialVersionUID = 6402999324726786933L;
	
	protected moa.classifiers.Classifier classifier;
	
	@Override
	public void buildClassifier(Instances trainingSet) {
		InstancesHeader header = new InstancesHeader(trainingSet);
		classifier.setModelContext(header);
		
		for (int i=0;i<trainingSet.numInstances();++i){
			Instance trainInst = trainingSet.instance(i);
			classifier.trainOnInstance(trainInst);
		}
	}
	
	@Override
	public double classifyInstance(Instance instance) {
		double[] prediction = classifier.getVotesForInstance(instance);
		return Utils.maxIndex(prediction);
	}

	@Override
	public double[] distributionForFeaturesVector(Instance instance) {
		return classifier.getVotesForInstance(instance);
	}
	
	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		return classifier.getVotesForInstance(instance);
	}

	@Override
	public void trainOnInstance(Instance instance)
			throws UnsupportedOperationException {
		
		classifier.trainOnInstance(instance);
	}
	
	@Override
	public Capabilities getCapabilities() {
		// TODO Check capabilities for online classifiers
		return null;
	}

}
