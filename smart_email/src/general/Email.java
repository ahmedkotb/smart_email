package general;

import java.util.Arrays;
import java.util.Date;

public class Email {
	private String from;
	private String[] to;
	private String[] cc;
	private String[] bcc;
	private String subject;
	private String content;
	private int size;
	private Date date;
	private String label;
	
	public Email(String from, String[] to, String[] cc, String[] bcc,
			String subject, String content, int size, Date date) {
		
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		this.content = content;
		this.size = size;
		this.date = date;
	}
	
	public Email(String from, String to, String subject, String content, int size, Date date){
		
		this(from, new String[]{to}, new String[]{}, new String[]{}, subject,
				content, size, date);
	}
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String[] getTo() {
		return to;
	}
	public void setTo(String[] to) {
		this.to = to;
	}
	public String[] getCc() {
		return cc;
	}
	public void setCc(String[] cc) {
		this.cc = cc;
	}
	public String[] getBcc() {
		return bcc;
	}
	public void setBcc(String[] bcc) {
		this.bcc = bcc;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public Date getDate(){
		return this.date;
	}
	public void setDate(Date date){
		this.date = date;
	}
	public String getLabel(){
		return this.label;
	}
	public void setLabel(String label){
		this.label = label;
	}
	
	@Override
	public String toString() {
		String ret = "Email:\n======\n";
		ret += "from : " + from + '\n';
		ret += "to : " + Arrays.toString(to) + '\n';
		ret += "cc : " + Arrays.toString(cc) + '\n';
		ret += "bcc : " + Arrays.toString(bcc) + '\n';
		ret += "subject : " + subject + '\n';
		ret += "content :\n" + content + '\n';
		ret += "size : " + size + '\n';
		ret += "date : " + date + '\n';
		return ret;
	}
	
	
}
