package entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Date;


/**
 * The persistent class for the accounts database table.
 * 
 */
@XmlRootElement
@Entity
@Table(name="accounts")
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String email;

	private float accuracy;

	@Column(name="avg_response_time")
	private float avgResponseTime;

    @Lob()
	@Column(name="filters_list")
	private byte[] filtersList;

    @Temporal( TemporalType.DATE)
	@Column(name="last_visit")
	private Date lastVisit;

	private String status;

	private String token;

	@Column(name="total_classified")
	private int totalClassified;

	@Column(name="total_incorrect")
	private int totalIncorrect;

    public Account() {
    }

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public float getAccuracy() {
		return this.accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public float getAvgResponseTime() {
		return this.avgResponseTime;
	}

	public void setAvgResponseTime(float avgResponseTime) {
		this.avgResponseTime = avgResponseTime;
	}

	public byte[] getFiltersList() {
		return this.filtersList;
	}

	public void setFiltersList(byte[] filtersList) {
		this.filtersList = filtersList;
	}

	public Date getLastVisit() {
		return this.lastVisit;
	}

	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getTotalClassified() {
		return this.totalClassified;
	}

	public void setTotalClassified(int totalClassified) {
		this.totalClassified = totalClassified;
	}

	public int getTotalIncorrect() {
		return this.totalIncorrect;
	}

	public void setTotalIncorrect(int totalIncorrect) {
		this.totalIncorrect = totalIncorrect;
	}

}