package messages;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IncomingEmailMessage {

	private String username;
	private String emailId;
	private String emailContent;

	public IncomingEmailMessage() {
		// default constructor
	}

	public IncomingEmailMessage(String username, String emailId,
			String emailContent) {
		this.username = username;
		this.emailId = emailId;
		this.emailContent = emailContent;
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
	
	public String getEmailContent() {
		return emailContent;
	}
	
	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}
}
