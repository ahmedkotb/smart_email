package datasource;

import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Store;
import general.Email;
import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import com.sun.mail.imap.IMAPFolder;

public class ImapDAO extends DAO {

	private String username;
	private String password;
	private Store store;

	public ImapDAO(String username, String password) {
		this.username = username;
		this.password = password;
		this.store = connect(username, password);
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public ArrayList<String> getClasses() {

		ArrayList<String> classes = new ArrayList<String>(50);
		try {
			Folder[] labels = store.getDefaultFolder().list("*");
			for (Folder label : labels) {
				classes.add(label.getName());
			}

		} catch (MessagingException e) {
			e.printStackTrace();
			return null;
		}
		return classes;
	}

	@Override
	public ArrayList<Email> getClassifiedEmails(String label, int limit) {
		return getEmails(label, limit);
	}

	@Override
	public ArrayList<Email> getUnclassified(int limit) {
		return getEmails("Inbox", limit);
	}

	public ArrayList<Email> getEmails(String label, int limit) {

		Folder folder = null;
		Message messages[] = null;
		ArrayList<Email> emails = null ;
		try {
			folder = store.getFolder(label);
			folder.open(Folder.READ_WRITE);
			//get the number of messages in this folder
			int count = folder.getMessageCount();

			int start = Math.max(count - limit + 1,1);
			int end = count;

			//fetch the messages
			messages = folder.getMessages(start,end);

			//Emails are sorted from the newest to oldest 
			emails = new ArrayList<Email>(messages.length);
			for (int i=messages.length-1;i>-1;i--){
				MimeMessage m = new MimeMessage((MimeMessage)messages[i]);
				Email e = new Email(m);
				e.setUid(((IMAPFolder)folder).getUID(messages[i]));
				e.setHeader("X-label", label);
				emails.add(e);
			}
			
		} catch (MessagingException e) {
			e.printStackTrace();
			return null;
		}

		return emails;
	}

	@Override
	public void applyLabel(long emailId, String labelName) {
		try {
			IMAPFolder inbox = (IMAPFolder) this.store.getFolder("Inbox");
			inbox.open(Folder.READ_WRITE);
			Message message = inbox.getMessageByUID(emailId);

			inbox.copyMessages(new Message[] { message },
					this.store.getFolder(labelName));
			
			//TODO UNComment this to archieve email
			//message.setFlag(Flag.DELETED, true);

		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Store connect(String username, String password) {
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		Store store = null;
		try {
			Session session = Session.getDefaultInstance(props, null);
			store = session.getStore("imaps");
			store.connect("imap.gmail.com", username, password);
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return store;
	}

	public String showMultiPart(Message m) {
		try {
			MimeMultipart content = (MimeMultipart) m.getContent();
			BodyPart part = content.getBodyPart(0);
			return part.getContent().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}