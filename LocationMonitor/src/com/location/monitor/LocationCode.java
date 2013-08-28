package com.location.monitor;

import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;


public class LocationCode  extends Thread {
	final static String AppName = "LocationTestOS5-2";
	final static String PackageClass = "com.location.set2.LocationTest2App";
	final static long GUID = 0x5f00f48ea5b6542L;
	
	private double latitude;
	private double longitude;
	private String satCountStr;
	private float accuracy;
	private double heading;
	private double altitude;
	private double speed;

	private int interval = 1; // time in seconds to get new gps data
	private boolean roaming;
	Mailing mail;

	public LocationCode() {
		EventLogger.register(GUID, AppName,EventLogger.VIEWER_STRING);

		Mailing mail = new Mailing();
		if (CurrentLocation()) {
			EventLogger.logEvent(GUID, ("Location Listener installation **Successful!!!").getBytes() ,EventLogger.DEBUG_INFO);
		}
		else{
			EventLogger.logEvent(GUID, ("Location Listener installation **Failed!!!").getBytes() ,EventLogger.DEBUG_INFO);
			mail.SendMail("\r\nDevice LocationListener: FAILED INSTALLATION!!! \r\n" +
					"DeviceID #"+Phone.getDevicePhoneNumber(true) + "\r\n");
		}
	}
	
	private boolean CurrentLocation() {
		boolean retval = true;
		try {
			LocationProvider locationprovider = LocationProvider.getInstance(null);
			if(locationprovider!=null) {
				locationprovider.setLocationListener(new LocationListenerImpl(), interval, 1, 1);
			} else { 
				retval = false;
			}
		} catch (LocationException e) {
			System.out.println("Error: " + e.toString());
		}

		return retval;
	}

	private class LocationListenerImpl implements LocationListener {
		public void locationUpdated(LocationProvider provider, Location location) {
			if (location.isValid()) {
				heading = location.getCourse();
				longitude = location.getQualifiedCoordinates().getLongitude();
				latitude = location.getQualifiedCoordinates().getLatitude();
				altitude = location.getQualifiedCoordinates().getAltitude();
				speed = location.getSpeed();
 
				// This is to get the Number of Satellites
				String NMEA_MIME = "application/X-jsr179-location-nmea";
				satCountStr = location.getExtraInfo("satellites");
				if (satCountStr == null) {
					satCountStr = location.getExtraInfo(NMEA_MIME);
				}

				// this is to get the accuracy of the GPS Cords
				QualifiedCoordinates qc = location.getQualifiedCoordinates();
				accuracy = qc.getHorizontalAccuracy();
			}
			if(RadioInfo.getNetworkService(RadioInfo.getActiveWAFs()) == RadioInfo.NETWORK_SERVICE_ROAMING)
				roaming = true; 
			else
				roaming = false;
		}

		public void providerStateChanged(LocationProvider provider, int newState) {
			// no-op
		}
	}

	/**
	 * Is the device roaming
	 * @return boolean Roaming
	 */
	public boolean isRoaming() {
		return roaming;
	}

	/**
	 * Returns the terminal's course made good in degrees relative to true north.
	 * The value is always in the range (0.0,360.0) degrees.
	 *
	 * @return double
	 */
	public double getHeading() {
		return heading;
	}

	/**
	 * Returns the altitude component of this coordinate.
	 * Altitude is defined to mean height above the WGS84 reference ellipsoid.
	 * 0.0 means a location at the ellipsoid surface, negative values mean the
	 * location is below the ellipsoid surface, Float.NaN that no altitude is
	 * available.
	 *
	 * @return double
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * Get the number of satellites that you are currently connected to
	 *
	 * @return String
	 */
	public String getSatCount() {
		return satCountStr;
	}

	/**
	 * Get the Accuracy of your current GPS location
	 *
	 * @return float
	 */
	public float getAccuracy() {
		return accuracy;
	}

	/**
	 * Returns the latitude component of this coordinate.
	 *
	 * Positive values indicate northern latitude and negative values southern latitude.
	 *
	 * @return double
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Returns the longitude component of this coordinate.
	 *
	 * Positive values indicate eastern longitude and negative values western longitude.
	 *
	 * @return double
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Get your current ground speed in meters per second (m/s) at the time of measurement
	 *
	 * @return double
	 */
	public double getSpeed() {
		return speed;
	}
}
