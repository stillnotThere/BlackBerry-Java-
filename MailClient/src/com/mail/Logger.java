package com.mail;

import net.rim.device.api.system.EventLogger;

public class Logger {

	final long GUID = 0xd7c9740a55e26c51L;
	final String AppName = "**Mail Debugger**";
	
	public Logger()
	{
		EventLogger.register(GUID, AppName,EventLogger.VIEWER_STRING);
	}
	
	public void LogMessage(String msg)
	{
		EventLogger.logEvent(GUID, msg.getBytes(), EventLogger.ALWAYS_LOG);
	}
	
}
