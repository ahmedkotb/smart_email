package services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBElement;

import weka.core.Instance;
import weka.core.Instances;

import classification.Classifier;

import datasource.ImapDAO;

import entities.Account;
//import entities.Filter;
import entities.Model;
import filters.Filter;
import filters.FilterManager;
import general.Email;

import messages.*;

@Path("provider")
public class ClassificationResource {

	@PUT
	@Path("register")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes(MediaType.APPLICATION_XML)
	public Response addAccount(JAXBElement<Account> account) {	
		EntityManager entityManager = Persistence.createEntityManagerFactory("smart_email").createEntityManager();
	    EntityTransaction entr = entityManager.getTransaction();
		entr.begin();
		Account msg = account.getValue();
	    entityManager.persist(msg);
	    entr.commit();

		//TODO: implementation
		System.out.println("Add Account: " + msg.getEmail() + ", " + msg.getToken());

		return Response.created(getBaseURI()).build();
	}

	@PUT
	@Path("classify")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response requestClassification(JAXBElement<IncomingEmailMessage> message) {		
		IncomingEmailMessage msg = message.getValue();	

		final long emailId = Long.parseLong(msg.getEmailId());
		
		EntityManager entityManager = Persistence.createEntityManagerFactory("smart_email").createEntityManager();
		List<Account> accounts = entityManager.createQuery("select c from Account c where c.email = '" + msg.getUsername() + "'", Account.class).getResultList();
		List<entities.Filter>  filtersBlob = entityManager.createQuery("select c from Filter c where c.email = '" + msg.getUsername() + "'", entities.Filter.class).getResultList();
		List<Model> modelsBlob = entityManager.createQuery("select c from Model c", Model.class).getResultList();
		
		final Account account = accounts.get(0);
		Classifier constructedModel = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(modelsBlob.get(0).getModel());
		ObjectInputStream ois;
		ArrayList<Filter> filtersList = new ArrayList<Filter>();
		try {
			ois = new ObjectInputStream(bais);
			constructedModel = (Classifier) ois.readObject();
			ois.close();
			
			for(int i=0; i<filtersBlob.size(); i++){
				bais = new ByteArrayInputStream(filtersBlob.get(i).getFilter());
				filtersList.add((Filter) ois.readObject());
				ois.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		final Classifier model = constructedModel;
		final Filter[] filters = new Filter[filtersList.size()];
		//TODO: order of filters must be determined, especially for the class filter!!
		filtersList.toArray(filters);
		
		new Thread(new Runnable() {
			public void run() {
				ImapDAO dao = new ImapDAO(account.getEmail(), account.getToken());
				Email email = dao.getEmailByUID(emailId);
//				Filter[] filters = null; 
//				Classifier model = null; 
				FilterManager filterManager = new FilterManager(filters);
				Instance instance = filterManager.makeInstance(email);
				int labelIndex = (int) model.classifyInstance(instance);
				String labelName = instance.classAttribute().value(labelIndex);
				dao.applyLabel(emailId, labelName);				
			}
		}).start();
		
		return Response.ok().build();
	}

	@PUT
	@Path("unregister")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response deleteAccount(JAXBElement<DeleteAccountMessage> message) {		
		DeleteAccountMessage msg = message.getValue();	
		
		//TODO: implementation
		System.out.println("Delete Account: " + msg.getUsername());

		return Response.ok().build();
	}
	
	@PUT
	@Path("feedback")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response classificationFeedback(JAXBElement<ClassificationFeedbackMessage> message) {		
		ClassificationFeedbackMessage msg = message.getValue();	
		
		//TODO: implementation
		System.out.println("feedback: " + msg.getEmailId() + ", labels list size = " + msg.getLabels().size());

		return Response.ok().build();
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		return "<html> " + "<title>" + "Hello Classifier" + "</title>"
				+ "<body><h1>" + "Hello Classifier" + "</body></h1>" + "</html> ";
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri(
				"http://localhost:8080/smart_email").build();
	}
}
