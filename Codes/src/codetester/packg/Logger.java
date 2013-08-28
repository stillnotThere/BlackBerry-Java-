package codetester.packg;

import net.rim.device.api.system.EventLogger;

public class Logger {

	final long GUID = 0x1184e771e2d70d4bL;
	final String AppName = "**Code Debugger**";
	
	public Logger()
	{
		EventLogger.register(GUID, AppName,EventLogger.VIEWER_STRING);
	}
	
	public void LogMessage(String msg)
	{
		EventLogger.logEvent(GUID, msg.getBytes(), EventLogger.ALWAYS_LOG);
	}
	
}
