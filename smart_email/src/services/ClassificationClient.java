package services;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;

import entities.Account;

import messages.*;

public class ClassificationClient {
	private ClientConfig config = null;
	private Client client = null;
	private WebResource service = null;
	
	public ClientResponse addAccount(String username, String token){
		Account registerMsg = new Account();
		registerMsg.setEmail(username);
		registerMsg.setToken(token);
		ClientResponse response = service.path("rest/service").path("provider")
				.path("register").post(ClientResponse.class, registerMsg);

		System.out.println(response);
		System.out.println("Location: " + response.getLocation());
		
		return response;
	}

	public ClientResponse deleteAccount(String username){
		ClientResponse response = service.path("rest/service").path("provider").path(username).delete(ClientResponse.class);
		System.out.println(response);

		return response;
	}

	public ClientResponse requestClassification(String username, String emailId){
		IncomingEmailMessage classificationRequestMsg = new IncomingEmailMessage(username, emailId);
		ClientResponse response = service.path("rest/service").path("provider").path("classify").post(ClientResponse.class, classificationRequestMsg);
		System.out.println(response);

		return response;
	}
	
	public ClientResponse sendFeedback(){
		//XXX: where is teh username in the feedback message??
		java.util.ArrayList<String> labels = new java.util.ArrayList<String>();
		labels.add("label x");
		labels.add("label y");
		ClassificationFeedbackMessage feedbackMsg = new ClassificationFeedbackMessage(
				"someEmailId", labels);
		ClientResponse response = service.path("rest/service").path("provider")
				.path("feedback")
				.put(ClientResponse.class, feedbackMsg);
		System.out.println(response);
		
		return response;
	}
	
	public void run(){
		config = new DefaultClientConfig();
		client = Client.create(config);
		// Use this for debugging the response sent by the client
		client.addFilter(new LoggingFilter(System.out));

		service = client.resource(getBaseURI());

		// The HTML
		System.out.println(service.path("rest/service").path("provider")
				.accept(MediaType.TEXT_HTML).get(String.class));

		ClientResponse response = null;

		// Add Account
//		response = addAccount("gp.term.project@gmail.com", "gptermproject");
		
		// Delete Account
//		response = deleteAccount("gp.term.project@gmail.com");
		
		// classification request
		response = requestClassification("gp.term.project@gmail.com", "2");
		
		// feedback
//		response = sendFeedback();
	}
	
	public static void main(String[] args) {
		new ClassificationClient().run();
	}

	private URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/smart_email").build();
	}
}
