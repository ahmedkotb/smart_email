package classification;

public class NaiveBayesMoaClassifier extends OnlineClassifier {

	private static final long serialVersionUID = 1802816003218716556L;
	
	public NaiveBayesMoaClassifier() {
		//create the classifier
		classifier = new moa.classifiers.bayes.NaiveBayes();
		classifier.prepareForUse();
	}
	
}
