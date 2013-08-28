package com.mail;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class MailScreen extends MainScreen
{
	Timer buttontimer;
//    IncomingMail incomingMail;
	
    /**
     * Creates a new MyScreen object
     */
    public MailScreen()
    {        
        super(MainScreen.HORIZONTAL_SCROLLBAR);
        setTitle("Test Mail Client");
     
        ButtonField send2 = new ButtonField("Send Mail" , ButtonField.CONSUME_CLICK);
        send2.setRunnable(new ButtonTask());
        add(send2);
        new MailCodes().ReadMail();
    }
    
    public class ButtonTask implements Runnable
    {
    	public void run()
    	{
    		new MailCodes().SendMail("test test test test test test");
    	}
    }
    
    public boolean onSavePrompt()
    {
		return false;
    }
    
    public boolean onClose()
    {
    	super.close();
    	return true;
    }
    
}
