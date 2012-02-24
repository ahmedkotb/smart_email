package datasource;

import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Store;

import general.Email;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.UIDFolder;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.ParseException;

import com.sun.mail.imap.IMAPFolder;
//import com.sun.xml.internal.messaging.saaj.packaging.mime.Header;

public class ImapDAO extends DAO{
	
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

		ArrayList<String> classes= new ArrayList<String>(50);
		try {
			Folder[] labels = store.getDefaultFolder().list("*");
			int i =0;
		    for(Folder label:labels){
		    	classes.add(label.getName());
		    	i++;
		    	System.out.println("Label "+i+": "+label.getName());
		    }
		    		
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return classes;
	}

	@Override
	public Email[] getClassifiedEmails(String label, int limit) {
		return getEmails(label, limit);
	}

	@Override
	public Email[] getUnclassified(int limit) {
		return getEmails("Inbox", limit);
		 
	}

	public Email[] getEmails(String label , int limit){
		  Folder folder = null;
		   Message messages[] = null;
		try {
			folder = store.getFolder(label);
	        folder.open(Folder.READ_WRITE);
	        messages = folder.getMessages();
	        
	        if(messages.length <limit)
	        	limit = messages.length;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          Email[] emails = new Email[limit];
          for (int j=0;j<limit;j++) {
              ContentType ct = null;
              Email email = (Email) messages[j];

			try {
				ct = new ContentType(messages[j].getContentType());
              if (ct.getPrimaryType().equals("multipart")) {
                  String content = showMultiPart(messages[j]);
                  
              } else {
            	 //  email.setContent(messages[j].getContent().toString());
            	   
              }
			
              String[] bcc = getEmailAttr(messages[j],Message.RecipientType.BCC);
              String[] cc = getEmailAttr(messages[j],Message.RecipientType.CC);
              String[] to = getEmailAttr(messages[j], Message.RecipientType.TO);
           //   email.setBcc(bcc);
              System.out.println("Printing BCCs");
             if(bcc!=null) printStringArray(bcc);
              
             // email.setCc(cc);
              System.out.println(("Printing CCs"));
              if(cc!=null) printStringArray(cc);
              
              //email.setFrom(((InternetAddress)(messages[j].getFrom()[0])).getAddress());
              System.out.println("Printing from");
              System.out.println(email.getFrom());
              
              //email.setTo(to);
              System.out.println("Printing Tos");
              printStringArray(to);
              
              email.setSubject(messages[j].getSubject().toString());
              System.out.println("Printing Subject");
              System.out.println(email.getSubject());
              
              long id = ((UIDFolder) folder).getUID(messages[j]);              
             // email.setId(id);
                     
              System.out.println("Printing Id");
             // System.out.println(email.getId());
              
             //email.setDate(messages[j].getReceivedDate());
             System.out.println("Printing Date");
             //System.out.println(email.getDate());
              
              System.out.println("*************");
              
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
             	   
          }
      return emails;
		
	}
	
	private String[] getEmailAttr(Message message,Message.RecipientType type){
		Address[] addresses = null;
		String[] array = null;
		try {
			addresses = message.getRecipients(type);
			if(addresses!=null){
				array = new String[addresses.length];
				
				for(int i=0;i<addresses.length;i++){
					
					array[i] = ((InternetAddress)addresses[i]).getAddress();
				}
				return array;
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void printStringArray(String[] array){
		
		for(int i=0;i<array.length;i++){
			
			System.out.println("Element "+i+": "+array[i]);
			
		}
		
	}

	@Override
	public void applyLabel(long emailId, String labelName) {
        try {
			IMAPFolder inbox = (IMAPFolder) this.store.getFolder("Inbox");
			inbox.open(Folder.READ_WRITE);
			Message message = inbox.getMessageByUID(emailId);

			inbox.copyMessages(new Message[]{message}, this.store.getFolder(labelName));
			message.setFlag(Flag.DELETED, true);
			
        } catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
    public Store connect(String username , String password){
    	ArrayList<String> emails = new ArrayList<String>();
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Store store = null;
        try {
            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", username, password);
            System.out.println(store);
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
                ContentType ct = new ContentType(part.getContentType());
                    return part.getContent().toString();  
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
    }
  

}
