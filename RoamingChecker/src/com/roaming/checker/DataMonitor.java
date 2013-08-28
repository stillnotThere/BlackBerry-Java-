package com.roaming.checker;

import net.rim.device.api.system.RadioInfo;

public class DataMonitor implements Runnable 
{
	
	long data_down = 0;
	long data_up = 0;
	
	public DataMonitor()
	{
		
	}
	
	public void run()
	{
		data_down = RadioInfo.getNumberOfPacketsReceived();
		data_up = RadioInfo.getNumberOfPacketsSent();
		new Logger().LogMessage("Down:"+data_down + " Up:"+data_up);
	}
	
}
