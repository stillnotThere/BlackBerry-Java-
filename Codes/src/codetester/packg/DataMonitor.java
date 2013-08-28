package codetester.packg;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLANConnectionListener;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.system.WLANListener;

public class DataMonitor implements Runnable//extends TimerTask
{
	
	boolean WIFI_Connected = false;
	String WIFI_profile = "";
	long db_down = 0;
	long db_up = 0;
	long MDS_download = 0;
	long MDS_upload = 0;
	long wifi_down = 0;
	long wifi_up = 0;
	
	/**
	 * RadioInfo.getNumberOfPacketsReceived()/Sent() includes
	 * packets received/sent from WiFi,Cellular and Bluetooth
	 */
	public void run()
	{
		WLANInfo.addListener((WLANListener)new WLANConnectionListener() 
		{
			public void networkConnected() {
				WIFI_Connected = true;
				new Logger().LogMessage("on WLAN--" + WLANInfo.getAPInfo().getProfileName());
			}

			public void networkDisconnected(int reason) {
				WIFI_Connected = false;
			}
			
		});
		
		if( !WIFI_Connected )
		{
			//new Logger().LogMessage("BIS/BES active");
			/*Download check*/
			db_down = MDS_download;
			db_up = MDS_upload;
			
			if(RadioInfo.getNumberOfPacketsReceived() >= db_down)
			{
				MDS_download = RadioInfo.getNumberOfPacketsReceived() - wifi_down;
			}
			else
			{
				MDS_download = db_down + RadioInfo.getNumberOfPacketsReceived() - wifi_down;
			}
			/*Upload check*/
			if(RadioInfo.getNumberOfPacketsSent() >= db_up)
			{
				MDS_upload = RadioInfo.getNumberOfPacketsSent() - wifi_up;
			}
			else
			{
				MDS_upload = db_up + RadioInfo.getNumberOfPacketsSent() - wifi_up;
			}
		}
		else
		{
			wifi_down = RadioInfo.getNumberOfPacketsReceived() - MDS_download;
			wifi_up = RadioInfo.getNumberOfPacketsSent() - MDS_upload;
			
			WIFI_profile = WLANInfo.getAPInfo().getProfileName();
		}
			
	}

	public long getDownload()
	{
		return MDS_download;
	}
	
	public long getUpload()
	{
		return MDS_upload;
	}
	
	public boolean getWLANState()
	{
		return WIFI_Connected;
	}
	
	public String getWLANProfileName()
	{
		return WIFI_profile;
	}
}