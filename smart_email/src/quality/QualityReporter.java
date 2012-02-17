package quality;

import classification.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Interface for evaluating different classification models.
 * 
 * @author Amr Sharaf
 * 
 */
public interface QualityReporter {

	/**
	 * Returns a copy of the confusion matrix.
	 * 
	 * @return double[][] representing the confusion matrix.
	 */
	public double[][] getConfusionMatrix();

	/**
	 * Gets the number of instances correctly classified
	 * 
	 * @return long representing number of instances correctly classified.
	 */
	public double getCorrect();

	/**
	 * Performs a cross-validation for a classifier on a set of instances.
	 * 
	 * @param model
	 *            the classification model
	 * @param trainingData
	 *            training data.
	 * @param fold
	 */
	public void crossValidateModel(Classifier model, Instances trainingData,
			int fold);

	/**
	 * Returns the estimated error rate or the root mean squared error if class
	 * is numeric.
	 * 
	 * @return error rate.
	 */
	public double getErrorRate();

	/**
	 * Evaluates the classifier on a given set of instances.
	 * 
	 * @param model
	 *            Classification model
	 * @param testingData
	 *            Training data.
	 */
	public void evaluateModel(Classifier model, Instances testingData);

	/**
	 * Calculate the false negative rate with respect to a particular class.
	 * 
	 * @param classIndex
	 *            index for the class considered positive
	 * @return (incorrectly classified negatives) / (total negatives)
	 */
	public double falseNegativeRate(int classIndex);

	/**
	 * Calculate the false positive rate with respect to a particular class.
	 * 
	 * @param classIndex
	 *            index for the class considered positive.
	 * @return (incorrectly classified positives) / (total positives)
	 */
	public double falsePositiveRate(int classIndex);

	/**
	 * Calculate the F-Measure with respect to a particular class.
	 * 
	 * @param classIndex
	 *            index for the class considered positive.
	 * @return (2 * recall * precision) / (recall + precision)
	 */
	public double fMeasure(int classIndex);

	/**
	 * Gets the number of instances incorrectly classified (that is, for which
	 * an incorrect prediction was made).
	 * 
	 * @return the number of incorrectly classified instances
	 */
	public double incorrect();

	/**
	 * Calculate number of false negatives with respect to a particular class.
	 * 
	 * @param classIndex
	 *            index for the class considered positive.
	 * @return incorrectly classified negatives
	 */
	public double numFalseNegatives(int classIndex);

	/**
	 * Calculate number of false positives with respect to a particular class.
	 * 
	 * @param classIndex
	 *            index for the class considered positive.
	 * @return incorrectly classified positives.
	 */
	public double numFalsePositives(int classIndex);

	/**
	 * Gets the number of test instances that had a known class value (actually
	 * the sum of the weights of test instances with known class value)
	 * 
	 * @return the number of test instances that had a known class value.
	 */
	public double numInstances();

	/**
	 * Calculate the number of true negatives with respect to a particular
	 * class.
	 * 
	 * @param classIndex
	 *            index for the class considered positive.
	 * @return number of true negatives
	 */
	public double numTrueNegatives(int classIndex);

	/**
	 * Calculate the number of true positives with respect to a particular
	 * class.
	 * 
	 * @param classIndex
	 *            index for the class considered positive.
	 * @return correctly classified positives
	 */
	public double numTruePositives(int classIndex);

	/**
	 * Gets the percentage of instances correctly classified (that is, for which
	 * a correct prediction was made).
	 * 
	 * @return percentage of instances correctly classified.
	 */
	public double pctCorrect();

	/**
	 * Gets the percentage of instances incorrectly classified.
	 * 
	 * @return Gets the percentage of instances incorrectly classified.
	 */
	public double pctIncorrect();

	/**
	 * Gets the percentage of instances not classified (that is, for which no
	 * prediction was made by the classifier).
	 * 
	 * @return percentage of instances not classified.
	 */
	public double pctUnclassified();

	/**
	 * Calculate the precision with respect to a particular class.
	 * 
	 * @param classIndex
	 *            index for the class considered positive.
	 * @return precision with respect to a particular class.
	 */
	public double precision(int classIndex);

	/**
	 * Calculate the recall with respect to a particular class.
	 * @param classIndex
	 * @return recall with respect to a particular class.
	 */
	public double recall(int classIndex);

	/**
	 * Generates a breakdown of the accuracy for each class, 
	 * incorporating various information-retrieval statistics, 
	 * such as true/false positive rate, precision/recall/F-Measure.
	 * @param title
	 * @return Details String.
	 */
	public String toClassDetailsString(String title);

	/**
	 * Outputs the performance statistics in summary form
	 * @return summary string.
	 */
	public String toSummaryString();
	
	/**
	 * Calculate the true negative rate with respect to a particular class.
	 * 
	 * @param classIndex
	 *            index for class considered positive.
	 * @return correctly classified negatives / total negatives
	 */
	public double trueNegativeRate(int classIndex);

	/**
	 * Calculate the true positive rate with respect to a particular class.
	 * 
	 * @param classIndex
	 *            index for the class considered positive.
	 * @return (correctly classified positives) / (total positives)
	 */
	public double truePositiveRate(int classIndex);

	/**
	 * Gets the number of instances not classified (that is, for which no
	 * prediction was made by the classifier).
	 * 
	 * @return the number of unclassified instances.
	 */
	public double unclassified();

	/**
	 * Updates the class prior probabilities (when incrementally training)
	 * @param instance
	 */
	public void updatePriors(Instance instance);
}
