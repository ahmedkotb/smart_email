package entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the models database table.
 * 
 */
@Embeddable
public class ModelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String email;

	private String type;

    public ModelPK() {
    }
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ModelPK)) {
			return false;
		}
		ModelPK castOther = (ModelPK)other;
		return 
			this.email.equals(castOther.email)
			&& this.type.equals(castOther.type);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.email.hashCode();
		hash = hash * prime + this.type.hashCode();
		
		return hash;
    }
}