package classification;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

public class NaiveBayesClassifier extends Classifier {

	private static final long serialVersionUID = -7728431597158327870L;

	private NaiveBayes classifier;

	public NaiveBayesClassifier() {
		classifier = new NaiveBayes();
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
