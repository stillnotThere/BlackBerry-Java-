package com.mail;
import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.AddressException;
import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.mail.Store;
import net.rim.blackberry.api.mail.Transport;
import net.rim.blackberry.api.mail.event.FolderEvent;
import net.rim.blackberry.api.mail.event.FolderListener;


public class MailCodes {//extends TimerTask{

	String str = "";
	
	public MailCodes()
	{
	}
	
//	public void run()
//	{
//		MailCode2(str);
////		MailCode2("blah blah testing code2");
//	}
	
    public void SendMail(String bodyText2)
    {
    	String toMail2 = "sunnyboy.rohan@gmail.com";
    	String toMailName2 = "rohan";
    	String toMail2_b = "ashwin@cellphonehospitalinc.com";
    	String toMailName2_b = "ashwin";
    	String fromMail2;
    	String fromMailName2;
    	Session session2;
    	Address fromadd2;
    	Address toadd2;
    	Address toadd2_b;
    	Message message2;
    	Store store;
    	
    	session2 = Session.getDefaultInstance();
    	Folder[] folders = session2.getStore().list(Folder.OUTBOX);
    	Folder outboxfolder = folders[0];
    	message2 = new Message(outboxfolder);
    	try{
    		fromMail2 = session2.getServiceConfiguration().getEmailAddress().toLowerCase();
			fromMailName2 = session2.getServiceConfiguration().getName().toLowerCase();
			
			fromadd2 = new Address(fromMail2,fromMailName2);
			toadd2 = new Address(toMail2,toMailName2);
			toadd2_b = new Address(toMail2_b,toMailName2_b);
		
			message2.setSubject("test mail <--- MailClient");														//Subject
			message2.setFrom(fromadd2);																//Sender address	(BB mail account)
			message2.addRecipient(Message.RecipientType.TO, toadd2);
//	    		message2.addRecipients(Message.RecipientType.TO, new Address[] {toadd2,toadd2_b});		//sending to		(rohan & ashwin)
		
//		    		bodyText2 = "Test mail from BB account";
			message2.setContent(bodyText2);
			
			Transport.send(message2);
			
			store = session2.getStore();
			outboxfolder.addFolderListener(new CustomFolderListener(message2.getMessageId()));
			
			
			outboxfolder.deleteMessage(message2, true);			//force deletion even if the message has been saved on device
			
			message2.updateUi();
			
		} catch(AddressException e) {
			e.printStackTrace();
			System.err.print(e.getMessage());
		} catch(MessagingException e) {
			e.printStackTrace();
			System.err.print(e.getMessage());
		}
    	
    }
    
    public class CustomFolderListener implements FolderListener
    {
    	public int messageID;
    	public Folder messageFolder;
    	
    	public CustomFolderListener(int msgid) 
    	{
    		messageID = msgid;
    	}

		public void messagesAdded(FolderEvent e)
		{
			if(e.getMessage().getMessageId() == messageID)
			{
				messageFolder = e.getMessage().getFolder();
				messageFolder.deleteMessage(e.getMessage(), true);
			}
		}

		public void messagesRemoved(FolderEvent e) 
		{
			
		}
    }
	
    
	String in_Mail = "rohan@cellphonehospitalinc.com";
	String in_Name = "Rohan K Mahendroo";
	String device_Mail = "";
	String device_Name = "";
	String incoming_subject = "";
	String incoming_content = "";
	
	public void ReadMail()
	{
		try{
			Session read_session = Session.getDefaultInstance();
			Address in_Address = new Address(in_Mail,in_Name);
			Folder[] read_folder = read_session.getStore().list(Folder.INBOX);
			Folder inbox = read_folder[0];
			Message read_message = new Message(inbox);
			
			device_Mail = read_session.getServiceConfiguration().getEmailAddress();
			device_Name = read_session.getServiceConfiguration().getName();
			inbox.addFolderListener(new FolderListener()
			{
				public void messagesAdded(FolderEvent e) {
					try{
						if(e.getType() == FolderEvent.MESSAGE_ADDED)
						{
							new Logger().LogMessage("new incoming mail");
							new Logger().LogMessage("inmail address:"+e.getMessage().getFrom().getAddr());
							if(e.getMessage().getFrom().getAddr().equalsIgnoreCase(in_Mail))
							{
								if( e.getMessage().getSubject().equalsIgnoreCase("Device Plan Details") )
								{
									incoming_content = e.getMessage().getBodyText();
									new Logger().LogMessage("Content::"+incoming_content);
								}
							}
						}
					} catch(MessagingException e1) {
						e1.printStackTrace();
						new Logger().LogMessage("Messaging Exception:"+e1.getClass()+"::"+e1.getMessage());
					}
				}
	
				public void messagesRemoved(FolderEvent e) 
				{
				}
			});
			
		} catch(MessagingException e) {
			e.printStackTrace();
			new Logger().LogMessage("Messaging Exception:"+e.getClass()+"::"+e.getMessage());
		}
	}
    
}
