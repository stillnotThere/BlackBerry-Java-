package com.roaming.checker;

import net.rim.device.api.system.EventLogger;

public class Logger {

	final long GUID = 0xa539b3005fe3d43eL;
	final String AppName = "**Roaming Checker**";
	
	public Logger()
	{
		EventLogger.register(GUID, AppName,EventLogger.VIEWER_STRING);
	}
	
	public void LogMessage(String msg)
	{
		EventLogger.logEvent(GUID, msg.getBytes(), EventLogger.ALWAYS_LOG);
	}
	
}
