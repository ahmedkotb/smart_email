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

	@Id
	@Column(name="model_id")
	private int modelId;

	private String email;

    @Lob()
	private byte[] model;

    public Model() {
    }

	public int getModelId() {
		return this.modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte[] getModel() {
		return this.model;
	}

	public void setModel(byte[] model) {
		this.model = model;
	}

}