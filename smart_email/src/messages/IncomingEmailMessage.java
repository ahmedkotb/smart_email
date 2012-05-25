package messages;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IncomingEmailMessage {

	private String username;
	private String emailId;
	
	public IncomingEmailMessage(){
		//default constructor
	}
	
	public IncomingEmailMessage(String username, String emailId){
		this.username = username;
		this.emailId = emailId;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
}
