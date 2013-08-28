package com.location.monitor; 

import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.AddressException;
import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.blackberry.api.mail.Multipart;
import net.rim.blackberry.api.mail.ServiceConfiguration;
import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.mail.Store;
import net.rim.blackberry.api.mail.TextBodyPart;
import net.rim.blackberry.api.mail.Transport;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;

public class Mailing {

	public Session session; 
	public Store store;
	public Folder[] folders;
	public Folder sent;
	public ServiceConfiguration serviceConfig = null;
	public ServiceRecord[] serviceRecords; 
	
	String mailfrom ;
	String mailfromName;
	final String mailto = "rohan@cellphonehospitalinc.com";
	final String mailto2 = "ashwin@cellphonehospitalinc.com";
	final String mailtoName = "rohan";
	final String mailtoName2 = "ashwin";
	public Address fromAddress;
	public Address toAddress_A;
	public Address toAddress_B;
	Message message;
	
	String mailSubject = "Location Info";
	Multipart mailBodyPart = new Multipart();
	TextBodyPart mailBodyText = null;
	
	public Mailing() 
	{
		
	}
	
	public void SendMail(String locationinfo)
	{	
		try{
			session = Session.getDefaultInstance();			//takes Default mailing address or Enterprise address
			store = session.getStore();						//mailbox
			
			folders = store.list(Folder.SENT);
			sent = folders[0];
			
			message = new Message(sent);		//ALT-- message = new Message() __without any folders
			
			mailfrom = session.getServiceConfiguration().getEmailAddress().toLowerCase();
			mailfromName = session.getServiceConfiguration().getName().toLowerCase();
			
			fromAddress = new Address(mailfrom, mailfromName);
			toAddress_A = new Address(mailto,mailtoName);
//			toAddress_B = new Address(mailto2,mailtoName2);
			
//			message.addRecipients(Message.RecipientType.TO, new Address[] {toAddress_A,toAddress_B});
			message.addRecipient(Message.RecipientType.TO, toAddress_A);
			message.setSubject(mailSubject);
//			message.setSentDate(Calendar.getInstance().getTime());
			message.setContent(locationinfo.getBytes());
			message.setPriority(Message.Priority.HIGH);
			
			Transport.send(message);
		} catch(AddressException e) {
			e.printStackTrace();
		} catch(MessagingException e) {
			e.printStackTrace();
		}
		
	}
	
}
