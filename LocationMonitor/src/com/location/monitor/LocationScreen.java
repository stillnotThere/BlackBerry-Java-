package com.location.monitor;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class LocationScreen extends MainScreen
{
	String stream="Retreiving co-ordinates";
	double lat=0,lng=0;
	boolean roam=false;
	Date date;
	SimpleDateFormat sdf = new SimpleDateFormat("MMMM/dd/yyyy HH:mm:ss a");
	RichTextField txtlocation;
	
	LocationCode location;
	Mailing mail;
	ScreenCode screenCode;
	
    public LocationScreen()
    {
    	super(MainScreen.VERTICAL_SCROLLBAR|MainScreen.HORIZONTAL_SCROLLBAR);
    	
        setTitle("Location Monitor");
        
        screenCode = new ScreenCode();
        
//        location = new LocationCode();
        mail = new Mailing();

        txtlocation = new RichTextField("",Field.FOCUSABLE_MASK);

      /*  boolean roaming = ( (RadioInfo.getState() & RadioInfo.NETWORK_SERVICE_ROAMING) != 0 );
        if(roaming)
        {
	        while(RetrieveCoordinates() && true)
	        {
	        	add(txtlocation);
	        	
	        	try {
					Thread.sleep(60*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
        }
        else{
        	txtlocation.setText("Not on roaming");
        	add(txtlocation);
        }*/
        
        this.getApplication().requestBackground();
        new Mailing().SendMail("test");
        
//        while(RetrieveCoordinates() && true)
//        {
//        	add(txtlocation);
//        	
//        	try {
//				Thread.sleep(60*1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//        }
        
        
//        	while(true)
//        	{
//        		
//        		RetrieveCoordinates();
//        		txtlocation.setText(stream);
//        		add(txtlocation);
//        	}
        	
//        screenCode.notifyAll();
//        txtlocation = new RichTextField(screenCode.getLocationCoordinates(), Field.FOCUSABLE);
//        add(txtlocation);
//        try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        
//        UiApplication.getUiApplication().invokeLater(new Runnable()
//        {
//        	public void run()
//        	{
//	        	 while(true)
//	             {
//	             	txtlocation = new RichTextField("Waiting for Co-ordinates",Field.FOCUSABLE);
//	             	RetrieveCoordinates();
//	             	add(txtlocation);
//	             	try{
//	             		Thread.sleep(60*1000);
//	             	} catch(InterruptedException e){
//	             		e.printStackTrace();
//	             	}
//	             }
//        	}
//        });
        
    }
    
    public boolean RetrieveCoordinates()
    {
//		txtlocation = new RichTextField("", RichTextField.NON_FOCUSABLE);
		lat = location.getLatitude();
		lng = location.getLongitude();
		roam = location.isRoaming();
		
		if (lat != 0.0 & lng != 0.0)
		{
			synchronized (LocationApp.getEventLock()) 
			{
				double acc = location.getAccuracy();
				date = new Date();
				stream = "Timestamp:" + (sdf.formatLocal(date.getTime())).toString() + 
						"\r\nDevice Phone Number:" + Phone.getDevicePhoneNumber(true) + 
						"\r\nLatitude:"+lat + 
						"\r\nLongitude:" + lng +
						"\r\nTowers:" + location.getSatCount() + 
						"\r\nAccuracy:" + String.valueOf(acc) + 
						"\r\nRoaming:" + (roam ? "Yes":"No") ;
				txtlocation.setText(stream);
				mail.SendMail(stream);
//				txtlocation.setText(stream);
				
//					new ServerCommunication().StartandSend(stream);
//					server.StartandSend(stream);
//					mail.SendMail2(stream);			//alternative code sending outputstream from bytes
//				mail.SendMail(stream);			//uses String(possibility of string flush)
			}
			return true;
		}
		else
		{
			stream = "Waiting for Co-ordinates.";
			txtlocation.setText(stream);
			return false;
		}
    }
    
    public boolean onClose()
    {
    	UiApplication.getUiApplication().requestBackground();
    	return false;
    }
    
}