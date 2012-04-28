package classification;

public class SGDClassifier extends OnlineClassifier{
	
	private static final long serialVersionUID = 3886129977205158557L;

	public SGDClassifier() {
		//initialize the classifier
		classifier = new moa.classifiers.functions.SGD();
		classifier.prepareForUse();
	}
	
}
