package datasource;

import java.util.ArrayList;
import general.Email;

/**
 * A class used for representing a data access object.
 * 
 * @author Ahmed El-Sharakasy, Amr Sharaf.
 * 
 */
public abstract class DAO {

	/**
	 * Returns a new DAO instance given the source string. For IMAP DAO the
	 * source string format should be: IMAP:username:password and for the file
	 * system DAO: FileSystems:path.
	 * 
	 * @param source
	 *            Data source path.
	 * @return A new DAO instance.
	 */
	public static DAO getInstance(String source) {
		String[] sourceTokens = source.split(":");

		if (sourceTokens.length > 2)
			return new ImapDAO(sourceTokens[1], sourceTokens[2]);
		else
			return new FileSystemDAO(sourceTokens[1]);
	}

	/**
	 * Returns the list of email classes (labels)
	 * 
	 * @return The list of email classes.
	 */
	public abstract ArrayList<String> getClasses();

	/**
	 * Returns an array of classified emails.
	 * 
	 * @param labelName
	 *            Label Name
	 * @param limit
	 *            The limit on the number of emails.
	 * @return Array of classified emails.
	 */
	public abstract ArrayList<Email> getClassifiedEmails(String labelName,
			int limit);

	/**
	 * Returns an array of unclassified emails.
	 * 
	 * @param limit
	 *            The limit on the number of emails.
	 * @return Array of unclassified emails.
	 */
	public abstract ArrayList<Email> getUnclassified(int limit);

	/**
	 * Applies the specified label on a certain email given the email ID.
	 * 
	 * @param emailId
	 *            Email ID.
	 * @param labelName
	 *            Label Name.
	 */
	public abstract void applyLabel(long emailId, String labelName);
}
