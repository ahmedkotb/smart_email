package messages;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DeleteAccountMessage {

	private String username;

	public DeleteAccountMessage(){
		//default constructor
	}
	
	public DeleteAccountMessage(String username){
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
