package com.roaming.checker;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import net.rim.device.api.gps.BlackBerryCriteria;
import net.rim.device.api.gps.BlackBerryLocationProvider;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;

public class LocationThread implements Runnable{

	final long GUID = 0x9a08af94634487e2L;
	
	public double latitude;
	public double longitude;
	public float accuracy;
	public float altitude;
	public int interval = 1; // time in seconds to get new gps data
	
	public BlackBerryCriteria bbcriteria;
	public BlackBerryLocationProvider bblocationprovider;
	
	int counter = 0;
	
	public void run()
	{
		CurrentLocation();
	}
	
	public boolean CurrentLocation() 
	{
		boolean retval = true;
		EventLogger.logEvent(GUID, ("Autonomous scanning initiated...").getBytes(), EventLogger.DEBUG_INFO);
		
		try{
			bbcriteria = new BlackBerryCriteria();
			bbcriteria.setCostAllowed(true);		//default "TRUE" dependent on device-cum-operator 
			bbcriteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH);
			//HIGH == autonomous
			//MEDIUM == assist
			//LOW == cell site
			bblocationprovider = (BlackBerryLocationProvider) LocationProvider.getInstance(bbcriteria);
			if(bblocationprovider.getState() == BlackBerryLocationProvider.AVAILABLE)
			{
				bblocationprovider.setLocationListener(new LocationListenerActivity(), interval, 1, 1);
				retval = true;
			}
			else
			{
				EventLogger.logEvent(GUID, ("LBS not supported!!!!").getBytes(), EventLogger.VIEWER_STRING);
				retval = false;
			}
		} catch(LocationException e) {
			e.printStackTrace();
		}
		
		return retval;
	}
	
	public class LocationListenerActivity implements LocationListener {
		public void locationUpdated(LocationProvider provider, Location location) {
			if (location.isValid()) {
				setLongitude(location.getQualifiedCoordinates().getLongitude());
				setLatitude(location.getQualifiedCoordinates().getLatitude());
				setAltitude(location.getQualifiedCoordinates().getAltitude());
				
				// this is to get the accuracy of the GPS Cords
				QualifiedCoordinates qc = location.getQualifiedCoordinates();
				accuracy = (qc.getVerticalAccuracy() + qc.getHorizontalAccuracy()) / 2;
			}
		}

		public void providerStateChanged(LocationProvider provider, int newState) {
			// no-op
		}
	}
	
	public void PauseTracking(int interval)
	{
		bblocationprovider.pauseLocationTracking(interval);
	}
	
	public void ResumeTracking()
	{
		bblocationprovider.resumeLocationTracking();
	}
	
	public void StopTracking()
	{
		bblocationprovider.stopLocationTracking();
	}
	
	public void ResetTracking()
	{
		bblocationprovider.reset();
	}
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public double getAccuracy()
	{
		return accuracy;
	}
    
	public float getAltitude()
	{
		return altitude;
	}
	
	public void setLatitude(double lat)
	{
		latitude = lat;
	}
    
	public void setLongitude(double lng)
	{
		longitude = lng;
	}
	
	public void setAccuracy(float acc)
	{
		accuracy = acc;
	}
    
	public void setAltitude(float alt)
	{
		altitude = alt;
	}
	
}
