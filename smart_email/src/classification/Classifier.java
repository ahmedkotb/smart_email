package classification;

import java.io.Serializable;
import weka.core.Instance;
import weka.core.Instances;

public abstract class Classifier implements weka.classifiers.Classifier,
		Serializable {

	private static final long serialVersionUID = -3535958974683789931L;

	public static Classifier getClassifierByName(String classifierName,
			String[] options) {
		
		classifierName = classifierName.toLowerCase();

		if (classifierName.equals("naivebayes"))
			return new NaiveBayesClassifier();
		else if (classifierName.equals("svm"))
			return new SVMClassifier();
		else if (classifierName.equals("decisiontree"))
			return new DecisionTreeClassifier();
		else if (classifierName.equals("onlinenaivebayes"))
			return new NaiveBayesMoaClassifier();
		else if (classifierName.equals("sgd"))
			return new SGDClassifier();

		return null;
	}

	public abstract double classifyInstance(Instance instance);

	public abstract void buildClassifier(Instances trainingSet);

	public abstract double[] distributionForFeaturesVector(Instance instance);

	public abstract void trainOnInstance(Instance Instance)
			throws UnsupportedOperationException;
}
