package quality;

import java.util.ArrayList;

public class ExperimentRunner {

	private ExperimentTunerIF tuner;
	private ArrayList<ExperimentUnit> experimentUnits;
	private String description;
	private String[] usernames;
	private Double[][] resultMatrix;
	
	public ExperimentRunner(ExperimentTunerIF tuner, String[] usernames){
		this.tuner = tuner;
		this.usernames = usernames;
		this.description = "";
		this.resultMatrix = new Double[usernames.length][];
	}
	
	public ExperimentRunner(ExperimentTunerIF tuner, String[] usernames, String description){
		this.tuner = tuner;
		this.usernames = usernames;
		this.description = description;
		this.resultMatrix = new Double[usernames.length][];
	}

	public void runExperiment(){
		ArrayList<ExperimentUnit> units = tuner.getExperimentUnits();
		for(int i=0; i<resultMatrix.length; i++)
			resultMatrix [i] = new Double[units.size()];
		
		//Change QualityReporter so as it can take ArrayList<FilterCreators>, ArrayList<Preprocessors>
		//QualityReporter should return the summary as a string + return the accuracy value alone
		//Change FilterCreatorManager and PreprocessorManager to support taking ArrayList<E>
		
		/*
		 * For each user u
		 * 	For each ExperimentUnit unit
		 * 		initialize the quality reporter object and run it
		 *      add timeStamp + unit name + unit description + result summary to Log file
		 *      save the accuracy to the resultMatrix
		 * 
		 * use the resultMatrix to generate the JSON File
		 */
	}
}
