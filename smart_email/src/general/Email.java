package general;

import java.io.InputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class Email extends MimeMessage {

	public Email(Session session, InputStream is) throws MessagingException {
		super(session, is);
	}	
	
}
