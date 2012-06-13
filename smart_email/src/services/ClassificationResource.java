package services;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBElement;

import training.AccountTrainer;

import entities.Account;

import messages.*;

@Path("provider")
public class ClassificationResource {

	@PUT
	@Path("register")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes(MediaType.APPLICATION_XML)
	public Response addAccount(JAXBElement<Account> account) {
		Account message = account.getValue();
		Thread registerationThread = new AccountTrainer(message.getEmail(),
				message.getToken());
		registerationThread.start();
		// Prepare the response
		ResponseBuilder responseBuilder = Response.created(getBaseURI());
		responseBuilder.status(202);
		return responseBuilder.build();
	}

	@PUT
	@Path("classify")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response requestClassification(
			JAXBElement<IncomingEmailMessage> message) {
		IncomingEmailMessage msg = message.getValue();

		// TODO: implementation
		System.out.println("Classification Request: " + msg.getUsername()
				+ ", " + msg.getEmailId());

		return Response.ok().build();
	}

	@PUT
	@Path("unregister")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response deleteAccount(JAXBElement<DeleteAccountMessage> message) {
		DeleteAccountMessage msg = message.getValue();

		// TODO: implementation
		System.out.println("Delete Account: " + msg.getUsername());

		return Response.ok().build();
	}

	@PUT
	@Path("feedback")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response classificationFeedback(
			JAXBElement<ClassificationFeedbackMessage> message) {
		ClassificationFeedbackMessage msg = message.getValue();

		// TODO: implementation
		System.out.println("feedback: " + msg.getEmailId()
				+ ", labels list size = " + msg.getLabels().size());

		return Response.ok().build();
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		return "<html> " + "<title>" + "Hello Classifier" + "</title>"
				+ "<body><h1>" + "Hello Classifier" + "</body></h1>"
				+ "</html> ";
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/smart_email").build();
	}
}
