package entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the filters database table.
 * 
 */
@Entity
@Table(name="filters")
public class Filter implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="filter_id")
	private int filterId;

	private String email;

    @Lob()
	private byte[] filter;

    public Filter() {
    }

	public int getFilterId() {
		return this.filterId;
	}

	public void setFilterId(int filterId) {
		this.filterId = filterId;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte[] getFilter() {
		return this.filter;
	}

	public void setFilter(byte[] filter) {
		this.filter = filter;
	}

}