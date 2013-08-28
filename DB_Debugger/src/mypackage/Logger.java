package mypackage;

import net.rim.device.api.system.EventLogger;

public class Logger {

	final long GUID = 0x6affee1fae060ac8L;
	final String AppName = "**DB Debugger**";
	
	public Logger()
	{
		EventLogger.register(GUID, AppName,EventLogger.VIEWER_STRING);
	}
	
	public void LogMessage(String msg)
	{
		EventLogger.logEvent(GUID, msg.getBytes(), EventLogger.ALWAYS_LOG);
	}
	
}
