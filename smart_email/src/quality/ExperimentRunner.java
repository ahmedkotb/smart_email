package quality;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import training.Trainer;
import training.TrainingType;
import weka.core.Instances;
import classification.Classifier;

public class ExperimentRunner {

	private ExperimentTunerIF tuner;
	// private ArrayList<ExperimentUnit> experimentUnits;
	private String title;
	private String description;
	private String[] experimentCategories;
	private Double[][] resultMatrix;

	private static final String EXPERIMENTS_LOG_PATH = "../../../Experiments_Log";
	private static String graphColors[] = { "b", "r", "g", "m", "c", "y", "k" };

	/*
	 * b : blue r : red g : green m : magenta c : cyan y : yellow k : black
	 */
	/**
	 * Constructor for ExperimentRunner
	 * 
	 * @param tuner
	 *            ExperimentTuner is the source for the ExepriementUnits of the
	 *            experiment
	 * @param experimentCategories
	 *            This would represent the different graph lines (categories) of
	 *            the experiment (e.g: usernames or algorithms)
	 * @param title
	 *            This represents either the title of the experiment (in case of
	 *            usernames as categories) OR represents the username (in case
	 *            of algorithms as categories)
	 */
	public ExperimentRunner(ExperimentTunerIF tuner,
			String[] experimentCategories, String title) {
		this.tuner = tuner;
		this.experimentCategories = experimentCategories;
		this.title = title;
		this.description = "";
		this.resultMatrix = new Double[experimentCategories.length][];
	}

	/**
	 * Constructor for ExperimentRunner
	 * 
	 * @param tuner
	 *            ExperimentTuner is the source for the ExepriementUnits of the
	 *            experiment
	 * @param experimentCategories
	 *            This would represent the different graph lines (categories) of
	 *            the experiment (e.g: usernames or algorithms)
	 * @param title
	 *            This represents either the title of the experiment (in case of
	 *            usernames as categories) OR represents the username (in case
	 *            of algorithms as categories)
	 * @param description description of the Experiment
	 */
	public ExperimentRunner(ExperimentTunerIF tuner, String[] experimentCategories,
			String title, String description) {
		this.tuner = tuner;
		this.experimentCategories = experimentCategories;
		this.title = title;
		this.description = description;
		this.resultMatrix = new Double[experimentCategories.length][];
	}

	private void printResultMatrix() {
		for (int i = 0; i < experimentCategories.length; i++) {
			System.out.print(experimentCategories[i] + ": ");
			for (int j = 0; j < resultMatrix[i].length; j++)
				System.out.printf(" %2.2f", resultMatrix[i][j]);
			System.out.println();
		}
	}

	private void ExportJSONObject(ArrayList<ExperimentUnit> units)
			throws JSONException, IOException {
		if (experimentCategories.length > graphColors.length) {
			System.err
					.println("Warning: ExperimentRunner.java: No sufficient colors to assign a unique color to each user in this Experiment");
		}

		JSONObject jsonRoot = new JSONObject();

		JSONArray categoryArr = new JSONArray();
		for (int i = 0; i < experimentCategories.length; i++) {
			JSONObject jsonCategory = new JSONObject();

			jsonCategory.put("color", graphColors[i % experimentCategories.length]);

			JSONArray jsonCategoriesValues = new JSONArray();
			for (int j = 0; j < units.size(); j++)
				jsonCategoriesValues.put(resultMatrix[i][j]);

			jsonCategory.put("values", jsonCategoriesValues);
			jsonCategory.put("name", experimentCategories[i]);
			categoryArr.put(jsonCategory);
		}

		JSONArray jsonCombinationsArr = new JSONArray();
		for (ExperimentUnit unit : units)
			jsonCombinationsArr.put(unit.getTitle());

		jsonRoot.put("criterias", categoryArr);
		jsonRoot.put("title", this.title);
		jsonRoot.put("combinations_names", jsonCombinationsArr);
		jsonRoot.put("ylabel", "Accuracy %");

		JSONArray jsonArrayWrapper = new JSONArray();
		jsonArrayWrapper.put(jsonRoot);

		String filename = EXPERIMENTS_LOG_PATH + "/" + this.title + "_"
				+ System.currentTimeMillis();
		PrintWriter pw = new PrintWriter(new FileWriter(filename));
		pw.println(jsonArrayWrapper.toString(4));
		pw.close();
	}

	public void runExperiment() throws Exception {
		long start = System.currentTimeMillis();
		ArrayList<ExperimentUnit> units = tuner.getExperimentUnits();
		for (int i = 0; i < resultMatrix.length; i++)
			resultMatrix[i] = new Double[units.size()];

		for (int i = 0; i < experimentCategories.length; i++) {
			for (int j = 0; j < units.size(); j++) {
				System.out.println("running user #" + (i + 1) + " ("
						+ experimentCategories[i] + ") with experiment unit #"
						+ (j + 1));
				ExperimentUnit unit = units.get(j);

				Trainer trainer = null;

				if (unit.getTrainingType() == TrainingType.PERCENTAGE) {
					trainer = new Trainer(experimentCategories[i],
							unit.getClassifierType(),
							unit.getTrainingSetPercentage(),
							unit.getPreprocessors(), unit.getFilterCreators());
				} else {
					String username = this.title;
					String classifierType = experimentCategories[i];
					trainer = new Trainer(username, classifierType,
							unit.getN(), unit.getK(), unit.getPreprocessors(),
							unit.getFilterCreators());
				}

				trainer.init();
				Classifier classifier = trainer.trainModel();
				Instances trainingInstances = trainer.getTrainedInstances();
				Instances testingInstances = trainer.getTestInstances();
				QualityReporter reporter = new WekaQualityReporter(
						trainingInstances);
				reporter.evaluateModel(classifier, testingInstances);
				resultMatrix[i][j] = reporter.getAccuracy();
				// XXX add timeStamp + unit name + unit description + result
				// summary to Log file

				System.out.println(reporter.toSummaryString());
			}
			// re-fetch the ExperimentUnits, as the current one is used and
			// can't be reused (e.g: WF filter is initialized in the above loop)
			units = tuner.getExperimentUnits();
		}

		ExportJSONObject(units);

		double time = (System.currentTimeMillis() - start) / 1000.0;
		System.out.println("Experiment took " + time + " seconds to run");

		printResultMatrix();
	}
}
