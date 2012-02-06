package filters;

import general.Email;
import general.FeatureAttribute;

public interface Filter {
	
	public FeatureAttribute makeFeaturesInstance(Email email);
}
