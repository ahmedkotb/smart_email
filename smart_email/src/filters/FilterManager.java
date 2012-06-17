package filters;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import general.Email;
import java.util.ArrayList;

public class FilterManager {
	
	private Filter[] filters;
	private Instances dataset;
	private int attsNum;
	private FastVector attributes;
	private boolean isTrainingPhase;
	
	public FilterManager(Filter[] filters, boolean isTrainingPhase){
		this.filters = filters;
		this.isTrainingPhase = isTrainingPhase;
		attributes = new FastVector();
		
		for(int i=0; i<filters.length; i++){
			ArrayList<Attribute> tmp = filters[i].getAttributes();
			attsNum += tmp.size();
			for(int j=0; j<tmp.size(); j++)
				attributes.addElement(tmp.get(j));
		}
		
		dataset = new Instances("dataset", attributes, 0);
		dataset.setClassIndex(attsNum-1);
	}
	
	public FastVector getAttributes(){
		return attributes;
	}
	
	public Instances getDataset(ArrayList<Email> emails){
		dataset.delete();
		for(Email email : emails){
			Instance ins = makeInstance(email);
			ins.setDataset(dataset);
			dataset.add(ins);
		}
		
		// Assumption: class attribute is the last attribute
		dataset.setClassIndex(attsNum-1);
		return dataset;
	}
	
	public Instance makeInstance(Email email){
		ArrayList<Double> valList = new ArrayList<Double>();
		
		//exclude the label filter if it is not the training phase
		int end = filters.length - (isTrainingPhase? 0:1);
		
		for(int i=0; i<end; i++){
			double[] ar = filters[i].getAttValue(email);
			for(int j=0; j<ar.length; j++) valList.add(ar[j]);
		}
		
		double[] vals = new double[valList.size()];
		for(int i=valList.size()-1; i>=0; i--) vals[i] = valList.get(i);
		
		/* WEKA  3.6.6
		Instance instance = new Instance(1, vals);
		instance.setDataset(dataset);
		*/
		// WEKA 3.7.5
		Instance instance = new DenseInstance(1,vals);
		instance.setDataset(dataset);

		return instance;
	}
}