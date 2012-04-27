package classification;

import moa.core.InstancesHeader;
import moa.options.ClassOption;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class NaiveBayesMoaClassifier extends Classifier {

	private static final long serialVersionUID = 1802816003218716556L;
	
	private moa.classifiers.Classifier learner;
	
	public NaiveBayesMoaClassifier() {
		//create the classifier
		learner = new moa.classifiers.bayes.NaiveBayes();
		learner.prepareForUse();
	}
	
	@Override
	public void buildClassifier(Instances trainingSet) {
		InstancesHeader header = new InstancesHeader(trainingSet);
		learner.setModelContext(header);
		
		for (int i=0;i<trainingSet.numInstances();++i){
			Instance trainInst = trainingSet.instance(i);
			learner.trainOnInstance(trainInst);
		}
		
	}

	@Override
	public double classifyInstance(Instance instance) {
		double[] prediction = learner.getVotesForInstance(instance);
		return Utils.maxIndex(prediction);
	}

	@Override
	public double[] distributionForFeaturesVector(Instance instance) {
		return learner.getVotesForInstance(instance);
	}

	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		return learner.getVotesForInstance(instance);
	}

	@Override
	public Capabilities getCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void trainOnInstance(Instance Instance)
			throws UnsupportedOperationException {
		
		learner.trainOnInstance(Instance);
	}

}
