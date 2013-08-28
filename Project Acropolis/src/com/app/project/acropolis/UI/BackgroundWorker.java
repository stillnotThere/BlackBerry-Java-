package com.app.project.acropolis.UI;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;

/**
 * 
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 *
 */

public class BackgroundWorker implements Runnable{
	
	final long GUID = 0x8cac0a9b91bd97b5L;	
	final String AppName = "Project Acropolis";
	
	CodesHandler codes;

	public boolean Roaming = false;
	
	public BackgroundWorker()
	{
		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
		Application.getApplication().setAcceptEvents(false);			//not an event dispatching thread
	}
	
	public void run()
	{
	
		LocationApplication.getApplication().addRadioListener((RadioListener)new RadioStatusListener()
    	{
			public void baseStationChange() {}

			public void networkScanComplete(boolean success) {}

			public void networkServiceChange(int networkId, int service) 
			{
				// TODO Auto-generated method stub
				if(service == RadioInfo.NETWORK_SERVICE_ROAMING)
					Roaming = true;
				else 
					Roaming = false;
			}

			public void networkStarted(int networkId, int service) 
			{
				if(service == RadioInfo.NETWORK_SERVICE_ROAMING)
					Roaming = true;
				else 
					Roaming = false;
			}

			public void networkStateChange(int state) {}

			public void pdpStateChange(int apn, int state, int cause) {}

			public void radioTurnedOff(){}

			public void signalLevel(int level){}
    	});
		
		
		new CodesHandler().run();
		
		Timer timer = new Timer();
		
		if(getRoamingState())
		{
			timer.schedule(new TimerTask() 
			{
				public void run()
				{
					new CodesHandler().run();
				}
			}, 1000, 1*60*60*1000);
		}
		else
		{
			timer.schedule(new TimerTask() 
			{
				public void run()
				{
					new CodesHandler().run();
				}
			}, 1000, 1*60*60*1000);
		}
	}
	
	 public boolean getRoamingState()
		{
			boolean roaming = ( (RadioInfo.getState() & RadioInfo.NETWORK_SERVICE_ROAMING) != 0 );
			
			String homeOperatorName = "";
	    	String currentOperatorName = "";
	    	
	    	currentOperatorName = RadioInfo.getCurrentNetworkName();
			
			return roaming;
			
			
		}
	
}