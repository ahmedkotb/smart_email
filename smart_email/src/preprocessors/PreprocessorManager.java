package preprocessors;

import general.Email;

import java.util.ArrayList;
import java.util.List;

/**
 * The PreprocessorManager is used to create the specified preprocessors and
 * apply all of them (in order) to the given email
 * 
 * @author ahmedkotb
 * 
 */
public class PreprocessorManager {

	private List<Preprocessor> preprocessorsList;

	/**
	 * creates the required preprocessors
	 * 
	 * @param preprocessors
	 *            an array of the required preprocessors names
	 */
	public PreprocessorManager(String[] preprocessors) {
		preprocessorsList = new ArrayList<Preprocessor>();
		for (String preprocessor : preprocessors) {
			try {
				Preprocessor processor = (Preprocessor) Class.forName(
						preprocessor).newInstance();
				preprocessorsList.add(processor);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This constructor should be used only in the testing phase (Experiments)
	 * 
	 * @param preprocessorsList
	 *            List of preprocessors to be used
	 */
	public PreprocessorManager(ArrayList<Preprocessor> preprocessorsList) {
		this.preprocessorsList = preprocessorsList;
	}

	/**
	 * applies the preprocessors on the given email
	 * 
	 * @param email
	 *            the email to be processed
	 */
	public void apply(Email email) {
		for (Preprocessor processor : preprocessorsList) {
			processor.apply(email);
		}
	}

	/**
	 * applies the preprocessors on the given email
	 * 
	 * @param email
	 *            the email to be processed
	 */
	public void apply(ArrayList<Email> emails) {
		for (Email email : emails) {
			for (Preprocessor processor : preprocessorsList) {
				processor.apply(email);
			}
		}
	}

}
