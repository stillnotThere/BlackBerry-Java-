package codetester.packg;

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
import net.rim.blackberry.api.mail.event.MessageEvent;
import net.rim.blackberry.api.mail.event.MessageListener;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.component.Dialog;

public class MailCode {

	public Session session; 
	public Message message;
	public Store store;
	public Folder[] folders;
	public Folder sent;
	public ServiceConfiguration serviceConfig = null;
	public ServiceRecord[] serviceRecords; 
	
	String mailfrom_SC = "";			//TODO - to be discussed
	String mailfromName_SC = "";
	final String mailfrom = "rohan.mahendroo@gmail.com";
	final String mailto = "rohan@cellphonehospitalinc.com";
	final String mailto2 = "ashwin@cellphonehospitalinc.com";
	final String mailtoName = "rohan";
	final String mailtoName2 = "ashwin";
	final String mailSubject ="";
	public Address fromAddress;
	public Address toAddress;
	public Address toAddress2;
	
	public Multipart mailBodyPart;
	public TextBodyPart mailBodyText;
	
	 public void MailCode(String data) 
	    {
			serviceRecords = ServiceBook.getSB().getRecords();
			
			for(int i=0;i<serviceRecords.length;i++)
			{
				if(serviceRecords[i].getCid().equalsIgnoreCase("CMIME") && !serviceRecords[i].isDisabled() && serviceRecords[i].isValid() )
				{
					serviceConfig = new ServiceConfiguration(serviceRecords[i]);
					if(serviceConfig.getEmailAddress().equalsIgnoreCase("rohan8@telus.blakcberry.net"))
					{
						ServiceConfiguration service3 = new ServiceConfiguration(serviceRecords[i]);
						mailfrom_SC = service3.getEmailAddress().toLowerCase();
						mailfromName_SC = serviceConfig.getName().toLowerCase();
						serviceConfig = service3;
						Dialog.inform( "Mail sent to " + mailfromName_SC + "\r\n@@:" + mailfrom_SC );
						break;
					}
//					ServiceConfiguration serviceConfiguration = new ServiceConfiguration(serviceRecords[i]);
//					mailfrom_SC = serviceConfiguration.getEmailAddress();
//					if(mailfrom_SC.equalsIgnoreCase("rohan8@telus.blackberry.net"))
//					{
//						serviceConfig = serviceConfiguration;
//						break;
//					}
				}
			}
			if(serviceConfig != null)
			{
				session = Session.getDefaultInstance(serviceConfig);
				store = session.getStore();
				folders = store.list(Folder.SENT);
				sent = folders[0];
				
				if(sent!=null)
				{
					try{
						message = new Message(sent);
						
						toAddress = new Address(mailto,mailtoName);
						toAddress2 = new Address(mailto2,mailtoName2);
						fromAddress = new Address(mailfrom_SC, mailfromName_SC);
						
						message.setFrom(fromAddress);
						message.addRecipients(Message.RecipientType.TO, new Address[]{ toAddress , toAddress2} ) ;			//sends to ashwin@cellphonehospitalinc.com and rohan@cellphonehospitalinc.com
		//    					message.addRecipient(Message.RecipientType.TO, toAddress);
						message.setSubject(mailSubject);
						
						mailBodyText = new TextBodyPart(mailBodyPart,data);
						mailBodyPart.addBodyPart(mailBodyText);
						/*
						 * iff === for ATTACHMENTS to be sent
						 * StringBuffer strbuf = new StringBuffer();
						 * SupportedAttachmentPart attachments = new SupportedAttachmentPart(mailBodyPart, 
						 * 					"text/plain", <"file name"> , strbuf.toString().getBytes("UTF-8"); 
						 * mailBodyPart.add(attachments);
						 */
						
						message.setContent(mailBodyPart);
						message.setPriority(Message.Priority.HIGH);
						message.addMessageListener(new MessageListener() {
								public void changed(MessageEvent event) 
								{
									if(event.getMessage().getStatus() == Message.Status.TX_SENT)
									{
										try{
											event.getMessage().removeMessageListener(this);
										} catch(Exception e) {
											e.printStackTrace();
										}
									}
								}
						});
						Transport.send(message);
						
					} catch(AddressException ae) {
						ae.printStackTrace();
					} catch(MessagingException me) {
						me.printStackTrace();
					}
				}
			
			}
			
		}
	
	
}
