package general;

import java.io.InputStream;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class Email extends MimeMessage {
	/**
	 * Create a new email instance from the input stream and the given session.
	 * @param session Mail session.
	 * @param inputStream data input stream.
	 * @throws MessagingException
	 */
	public Email(Session session, InputStream inputStream) throws MessagingException {
		super(session, inputStream);
	}
}
