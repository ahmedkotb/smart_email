package quality;

import classification.Classifier;
import weka.core.Instances;

/**
 * Interface for evaluating different classification models.
 * 
 * @author Amr Sharaf
 * 
 */
/**
 * @author amr
 *
 */
public interface QualityReporter {

	/**
	 * Returns a copy of the confusion matrix.
	 * @return double[][] representing the confusion matrix.
	 */
	public double[][] getConfusionMatrix();

	
	/**
	 * Gets the number of instances correctly classified
	 * @return long representing number of instances correctly classified.
	 */
	public double getCorrect();
	
	/**
	 * Performs a cross-validation for a classifier on a set of instances.
	 * @param model the classification model
	 * @param trainingData training data.
	 * @param fold
	 */
	public void crossValidateModel(Classifier model, Instances trainingData, int fold);
	 
	/**
	 * Returns the estimated error rate or the root mean squared error if class is 
	 * numeric.
	 * @return error rate.
	 */
	public double getErrorRate();
	
	/**
	 * Evaluates the classifier on a given set of instances.
	 * @param model Classification model
	 * @param trainingData Training data.
	 */
	public void evaluateModel(Classifier model, Instances trainingData);
	
	/**
	 * Calculate the true negative rate with respect to a particular class.
	 * @param classIndex index for class considered positive.
	 * @return  correctly classified negatives / total negatives
	 */
	public double trueNegativeRate(int classIndex);
	
	/**
	 * Calculate the true positive rate with respect to a particular class.
	 * @param classIndex index for the class considered positive.
	 * @return  (correctly classified positives) / (total positives)
	 */
	public double truePositiveRate(int classIndex);
}
