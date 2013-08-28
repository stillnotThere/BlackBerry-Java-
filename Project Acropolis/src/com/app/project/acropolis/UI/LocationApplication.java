package com.app.project.acropolis.UI;

import java.util.Enumeration;

import javax.microedition.io.file.FileSystemRegistry;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import com.app.project.acropolis.model.ModelFactory;

/**
 * @vendor CellPhoneHospitalInc
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * 
 * ---RELEASE NOTES---
 * @version 1.0.1
 * @desc Advices corporate head office of the device 
 * 		location when on Roaming (Strictly for Enterprise users only)
 */

/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class LocationApplication extends UiApplication
{
	final static long GUID = 0xa0d8b6e395774fc8L;
	final static String AppName = "**Project Acropolis**";

	final static String AppBG = "enter_background";
	final static String AppFG = "enter_foreground";
	
	public static String operator = "";
	static boolean BG_Icon = false;
	static boolean Starting = false;
   	static boolean PowerON = false;
	static boolean RadioON = false;
   	
	static boolean HALTED_APPLICATION = false;
	
   	static Thread thread; 
   	
   	public static boolean Roaming = false;
   	
   //	public static LocationApplication theApp = new LocationApplication();
   	public static ModelFactory theModel = new ModelFactory();
   	
   	public static void main(String[] args)
    {
		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
    
		ApplicationManager.getApplicationManager().setCurrentPowerOnBehavior(ApplicationDescriptor.FLAG_RUN_ON_STARTUP);
		
		if(ApplicationManager.getApplicationManager().inStartup())
		{//for startup
			try {
				Thread.sleep(1*60*1000);		//sleeps for 4Minutes while starting up
				EventLogger.logEvent(GUID, ("slept 1mins at ...inStartup()!!").getBytes(),EventLogger.ALWAYS_LOG);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LocationApplication theApp = new LocationApplication();
		EventLogger.logEvent(GUID, ("LocationApplication event dispatching!!!").getBytes(),EventLogger.ALWAYS_LOG);
		theApp.enterEventDispatcher();
    }

    /**
     * Creates a new LocationApplication object
     */
    public LocationApplication()
    {    
//    	FetchDeviceProperties();
        // Push a screen onto the UI stack for rendering.
        pushScreen(new UIScreen());
    }
   
	public void FetchDeviceProperties()
	{
    	boolean sdCardPresent = false;
    	String root = null;
    	
    	Enumeration enum = FileSystemRegistry.listRoots();
    	while (enum.hasMoreElements())
    	{
    		root = (String)enum.nextElement();
    		if(root.equalsIgnoreCase("sdcard/"))											//SDCard presence check
    		{
    			sdCardPresent = true;
    		}  
    	}            
    	if(!sdCardPresent)
    	{
    		UiApplication.getUiApplication().invokeLater(new Runnable()
    		{
    			public void run()
    			{
    				EventLogger.logEvent(GUID, ("SDCard absent").getBytes(),EventLogger.ALWAYS_LOG);
    				Dialog.alert("This application requires an SD card to be present. Exiting application...");
    				System.exit(0);            
    			} 
    		});        
    	}  
    	else
    	{
    		EventLogger.logEvent(GUID, ("SDCard present").getBytes(),EventLogger.ALWAYS_LOG);
    	}
		
	}
	
	public void DatabaseIntegration()
	{
		
	}
	
}