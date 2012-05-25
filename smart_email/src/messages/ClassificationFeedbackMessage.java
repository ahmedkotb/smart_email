package messages;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ClassificationFeedbackMessage {

	private String emailId;
	private ArrayList<String> labels;
	
	public ClassificationFeedbackMessage(){
		//default constructor
	}
	
	public ClassificationFeedbackMessage(String emailId, ArrayList<String> labels){
		this.emailId = emailId;
		this.labels = labels;
	}
	
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public ArrayList<String> getLabels() {
		return labels;
	}
	public void setLabels(ArrayList<String> labels) {
		this.labels = labels;
	}
}
