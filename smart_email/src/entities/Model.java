package entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the models database table.
 * 
 */
@Entity
@Table(name="models")
public class Model implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ModelPK id;

    @Lob()
	private byte[] model;

    public Model() {
    }

	public ModelPK getId() {
		return this.id;
	}

	public void setId(ModelPK id) {
		this.id = id;
	}
	
	public byte[] getModel() {
		return this.model;
	}

	public void setModel(byte[] model) {
		this.model = model;
	}

}