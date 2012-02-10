package datasource;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.mail.Store;

import general.Email;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.ParseException;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.StringTerm;
import javax.mail.search.SubjectTerm;

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
			Folder[] labels = store.getDefaultFolder().list();
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
              Email email = new Email();

			try {
				ct = new ContentType(messages[j].getContentType());
              if (ct.getPrimaryType().equals("multipart")) {
                  String content = showMultiPart(messages[j]);
                  
              } else {
            	   email.setContent(messages[j].getContent().toString());
            	   
              }
			
              String[] bcc = getEmailAttr(messages[j],Message.RecipientType.BCC);
              String[] cc = getEmailAttr(messages[j],Message.RecipientType.CC);
              String[] to = getEmailAttr(messages[j], Message.RecipientType.TO);
              email.setBcc(bcc);
              System.out.println("Printing BCCs");
             if(bcc!=null) printStringArray(bcc);
              
              email.setCc(cc);
              System.out.println(("Printing CCs"));
              if(cc!=null) printStringArray(cc);
              
              email.setFrom(((InternetAddress)(messages[j].getFrom()[0])).getAddress());
              System.out.println("Printing from");
              System.out.println(email.getFrom());
              
              email.setTo(to);
              System.out.println("Printing Tos");
              printStringArray(to);
              
              email.setSubject(messages[j].getSubject().toString());
              System.out.println("Printing Subject");
              System.out.println(email.getSubject());
              
              email.setId(messages[j].getMatchingHeaders(new String[]{"Message-D"}).toString());
              System.out.println("Printing Id");
              System.out.println(email.getId());
              
              System.out.println("*************");
              
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
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
	public void applyLabel(String emailId, String labelName) {
		// TODO Auto-generated method stub
		
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
            System.out.println("length: "+content.getBodyPart(1).getContent().toString());
                BodyPart part = content.getBodyPart(0);
                ContentType ct = new ContentType(part.getContentType());
                    return part.getContent().toString();  
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
    }
  

}
