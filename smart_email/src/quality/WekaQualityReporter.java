package quality;

import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.Classifier;


public class WekaQualityReporter implements QualityReporter {

	private Evaluation evaluation;

	public WekaQualityReporter(Instances instances) {
		try {
			evaluation = new Evaluation(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public double[][] getConfusionMatrix() {
		return evaluation.confusionMatrix();
	}

	@Override
	public double getCorrect() {
		return evaluation.correct();
	}

	@Override
	public void crossValidateModel(Classifier model, Instances trainingData,
			int fold) {
			try {
				evaluation.crossValidateModel(model, trainingData, fold, null, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public double getErrorRate() {
		return evaluation.errorRate();
	}

	@Override
	public void evaluateModel(Classifier model, Instances trainingData) {
		try {
			evaluation.evaluateModel(model, trainingData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public double falseNegativeRate(int classIndex) {
		return evaluation.falseNegativeRate(classIndex);
	}

	@Override
	public double falsePositiveRate(int classIndex) {
		return evaluation.falsePositiveRate(classIndex);
	}

	@Override
	public double fMeasure(int classIndex) {
		return evaluation.fMeasure(classIndex);
	}

	@Override
	public double incorrect() {
		return evaluation.incorrect();
	}

	@Override
	public double numFalseNegatives(int classIndex) {
		return evaluation.numFalseNegatives(classIndex);
	}

	@Override
	public double numFalsePositives(int classIndex) {
		return evaluation.numFalsePositives(classIndex);
	}

	@Override
	public double numInstances() {
		return evaluation.numInstances();
	}

	@Override
	public double numTrueNegatives(int classIndex) {
		return evaluation.numTrueNegatives(classIndex);
	}

	@Override
	public double numTruePositives(int classIndex) {
		return evaluation.numTruePositives(classIndex);
	}

	@Override
	public double pctCorrect() {
		return evaluation.pctCorrect();
	}

	@Override
	public double pctIncorrect() {
		return evaluation.pctIncorrect();
	}

	@Override
	public double pctUnclassified() {
		return evaluation.pctUnclassified();
	}

	@Override
	public double precision(int classIndex) {
		return evaluation.precision(classIndex);
	}

	@Override
	public double recall(int classIndex) {
		return evaluation.recall(classIndex);
	}

	@Override
	public String toClassDetailsString(String title) {
		try {
			return evaluation.toClassDetailsString(title);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toSummaryString() {
		return evaluation.toSummaryString();
	}

	@Override
	public double trueNegativeRate(int classIndex) {
		return evaluation.trueNegativeRate(classIndex);
	}

	@Override
	public double truePositiveRate(int classIndex) {
		return evaluation.truePositiveRate(classIndex);
	}

	@Override
	public double unclassified() {
		return evaluation.unclassified();
	}

	@Override
	public void updatePriors(Instance instance) {
		try {
			evaluation.updatePriors(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
