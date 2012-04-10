package training;

import weka.core.Instances;
import classification.Classifier;

/**
 * This interface defines the main methods implemented by the classifier
 * trainer.
 * 
 * @author Amr Sharaf
 * 
 */
public interface TrainerIF {

	/**
	 * Initialize the Trainer instance by preparing the training and testing
	 * sets to be used for building the model.
	 */
	public void init();

	/**
	 * Returns the classifier model.
	 * @return Classifier model
	 */
	public Classifier trainModel();

	/**
	 * Return trained instances.
	 * @return trained instances.
	 */
	public Instances getTrainedInstances();

	/**
	 * Return testing instances.
	 * @return testing instances.
	 */
	public Instances getTestInstances();
}
