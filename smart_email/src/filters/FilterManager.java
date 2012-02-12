package filters;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import general.Email;
import java.util.ArrayList;

public class FilterManager {
	
	private Filter[] filters;
	private Instances dataset;
	private int attsNum;
	
	public FilterManager(Filter[] filters){
		this.filters = filters;
		FastVector atts = new FastVector();
		
		for(int i=0; i<filters.length; i++){
			ArrayList<Attribute> tmp = filters[i].getAttributes();
			attsNum += tmp.size();
			for(int j=0; j<tmp.size(); j++)
				atts.addElement(tmp.get(j));
		}
		
		//XXX do we need to make the dataset name user-dependent?
		dataset = new Instances("dataset", atts, 0);
	}
	
	public Instances getDataset(ArrayList<Email> emails){
		dataset.delete();
		for(int i=0; i<emails.size(); i++){
			Instance ins = makeInstance(emails.get(i));
			ins.setDataset(dataset);
		}
		
		return dataset;
	}
	
	///XXX this function returns an instance without assigning it to a dataset
	public Instance makeInstance(Email email){
		ArrayList<Double> valList = new ArrayList<Double>();
		for(int i=0; i<filters.length; i++){
			double[] ar = filters[i].getAttValue(email);
			for(int j=0; j<ar.length; j++) valList.add(ar[j]);
		}
		
		double[] vals = new double[valList.size()];
		for(int i=valList.size()-1; i>=0; i--) vals[i] = valList.get(i);
		
		Instance instance = new Instance(1, vals);
		
//		instance.setDataset(dataset);
		
		return instance;
	}
}
