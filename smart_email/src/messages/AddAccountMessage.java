package messages;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AddAccountMessage {

	private String username;
	private String token;
	
	public AddAccountMessage(){
		//default constructor (required)!
	}
	
	public AddAccountMessage(String username, String token){
		this.username = username;
		this.token = token;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
