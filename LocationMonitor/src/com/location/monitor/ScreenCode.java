package com.location.monitor;

import java.util.Date;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;

public class ScreenCode {

	LocationScreen screen;
	String txt = "";
	String stream = "";
	
	double lat=0,lng=0;
	boolean roam=false;
	Date date;
	SimpleDateFormat sdf = new SimpleDateFormat("MMMM/dd/yyyy HH:mm:ss a");
	
	LocationCode location;
	Mailing mail;
	
	public ScreenCode()
	{
		location = new LocationCode();
		mail = new Mailing();
		
     	txt = "Waiting for Co-ordinates";
     	RetrieveCoordinates();
//         	screen = new LocationScreen(stream);
         	try{
         		Thread.sleep(3000);
         	} catch(InterruptedException e){
         		e.printStackTrace();
         	}
	}
	
	public String getLocationCoordinates()
	{
		return stream;
	}
	
	public void RetrieveCoordinates()
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
				mail.SendMail(stream);			//uses String(possibility of string flush)
//				txtlocation.setText(stream);
				
//					new ServerCommunication().StartandSend(stream);
//					server.StartandSend(stream);
//					mail.SendMail2(stream);			//alternative code sending outputstream from bytes
			}			
		}
		else
		{
			txt = "Waiting for Co-ordinates.";
		}
    }
	
}