package messages;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ClassificationFeedbackMessage {

	private String username;
	private String rawEmail;
	private String label;
	
	public ClassificationFeedbackMessage(){
		//default constructor
	}
	
	public ClassificationFeedbackMessage(String username, String rawEmail, String label){
		this.username = username;
		this.rawEmail = rawEmail;
		this.label = label;
	}
	
	public String getUsername(){
		return this.username;
	}
	public void setUsername(String username){
		this.username = username;
	}
	public String getRawEmail() {
		return rawEmail;
	}
	public void setRawEmail(String rawEmail) {
		this.rawEmail = rawEmail;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
