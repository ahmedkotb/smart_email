import classification.ClassificationManager;
import classification.Classifier;


public class Main {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		ClassificationManager mgr = new ClassificationManager();
		String path = "enron_processed/lokay_m";
		Classifier cls = mgr.go("FileSystem", path, null, 0);
		System.out.println("Hello World!");
	}
}
