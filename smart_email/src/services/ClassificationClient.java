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

	public static void main(String[] args) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		// Use this for debugging the response sent by the client
		client.addFilter(new LoggingFilter(System.out));

		WebResource service = client.resource(getBaseURI());

		// The HTML
		System.out.println(service.path("rest/service").path("provider")
				.accept(MediaType.TEXT_HTML).get(String.class));

		ClientResponse response = null;

		// Add Account
		Account registerMsg = new Account();
		registerMsg.setEmail("gp.term.project@gmail.com");
		registerMsg.setToken("gptermproject");
		response = service.path("rest/service").path("provider")
				.path("register").put(ClientResponse.class, registerMsg);
		System.out.println(response);
		System.out.println("Location: " + response.getLocation());

		// Delete Account
		DeleteAccountMessage unregisterMsg = new DeleteAccountMessage(
				"myUsername");
		response = service.path("rest/service").path("provider")
				.path("unregister").put(ClientResponse.class, unregisterMsg);
		System.out.println(response);

		// classification request
		IncomingEmailMessage classificationRequestMsg = new IncomingEmailMessage(
				"myUsername", "someEmailId");
		response = service.path("rest/service").path("provider")
				.path("classify")
				.put(ClientResponse.class, classificationRequestMsg);
		System.out.println(response);

		// feedback
		java.util.ArrayList<String> labels = new java.util.ArrayList<String>();
		labels.add("label x");
		labels.add("label y");
		ClassificationFeedbackMessage feedbackMsg = new ClassificationFeedbackMessage(
				"someEmailId", labels);
		response = service.path("rest/service").path("provider")
				.path("feedback")
				.put(ClientResponse.class, feedbackMsg);
		System.out.println(response);

	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/smart_email").build();
	}
}
