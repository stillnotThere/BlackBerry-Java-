package com.roaming.checker;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.location.AddressInfo;
import javax.microedition.location.Coordinates;

import net.rim.blackberry.api.maps.MapView;
import net.rim.device.api.io.URI;
import net.rim.device.api.lbs.maps.MapDimensions;
import net.rim.device.api.lbs.maps.MapFactory;
import net.rim.device.api.lbs.maps.model.MapDataModel;
import net.rim.device.api.lbs.maps.model.MapLocation;
import net.rim.device.api.lbs.maps.model.MapPoint;
import net.rim.device.api.lbs.maps.model.Mappable;
import net.rim.device.api.lbs.maps.ui.RichMapField;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class Screen extends MainScreen
{
	final long GUID = 0xa7511f8f3bf61f27L;
	final String AppName = "RoamingChecker";
	
	RichTextField roam;
	RichTextField maps_rtf;
	RichTextField lat;
	RichTextField lng;
	RichTextField acc;
	RichTextField country;
	RichTextField city;
//	GridFieldManager map_grid = new GridFieldManager(5,1,Field.USE_ALL_WIDTH);	
	
	/*9700 Screen Resolution	480*360*/
	/*9800 Resolution			480*360*/
	/*9810(Torch - 253ppi)		640*480*/
	/*9900 (286ppi)				640*480*/
	/*MapField -- screen height and width */
	AppMap_5 mf = new AppMap_5();
	MapView Map = new MapView();
	int Device_Orientation = 0;
	int[] Map_SIZE = new int[1];
	final int ZOOM_MIN = 3;					//supported MAX = 15 && MIN = 0
	final int ZOOM_MAX = 8;					//supported MAX = 15 && MIN = 0
	Coordinates map_coord;
	int mf_width = Display.getWidth();		//Width 9700 - 480px
	int mf_height = 100;					//Height 9700 - 100px(for application only)
	
  	double latitude=43.641679;
	double longitude=-79.627809;
	float accuracy;
	float altitude;
	int interval = 1;
	
	int counter = 0;
	
	public String DBName = "ACTIVITY_ACROPOLIS.txt";
	public String DBPath = "/databases/ProjectAcropolis/Database/"+DBName;
	public URI db_uri;
	
	LocationCode location;
//	LocationThread loc;
	ModelFactory model = new ModelFactory();
	
    /**
     * Creates a new MyScreen object
     */
    public Screen()
    {        
    	super(Screen.VERTICAL_SCROLLBAR_MASK|Screen.HORIZONTAL_SCROLLBAR_MASK);
        setTitle("Roaming Checker");
        String currentNetwork = RadioInfo.getCurrentNetworkName();
        add(new RichTextField("Current Operator:"+currentNetwork, Field.FOCUSABLE));
        
        RichTextField roam_rtf = new RichTextField("",Field.FOCUSABLE);
        
    	if(Check_NON_CAN_Operator())
		synchronized(Application.getEventLock())
		{
			roam_rtf.setText("Roaming");
		}
    	else
		synchronized(Application.getEventLock())
		{
			roam_rtf.setText("Not on roaming");
		}
        
		Timer timer = new Timer();
		timer.schedule(new LocationTimer(), 100, 10*1000);
    	
    	add(roam_rtf);

    	roam = new RichTextField("Roaming - " + String.valueOf(this.Check_NON_CAN_Operator()));
    	maps_rtf = new RichTextField("Waiting on current location...");
    	lat = new RichTextField("Latitude - waiting..");
    	lng = new RichTextField("Longitude - waiting..");
    	acc = new RichTextField("Accuracy - waiting..");
    	country = new RichTextField("Country - waiting ..");
    	city = new RichTextField("City - waiting..");
    	
//    	loc = new LocationThread();
//    	loc.run();
    	location = new LocationCode();
    	location.run();
    	
		add(maps_rtf);
		add(lat);
		add(lng);
		add(acc);
		add(country);
		add(city);
		
		Maps_6(latitude,longitude);
//		mf.setPreferredSize(Display.getWidth(), Display.getHeight()/3);
//		add(mf);
		
    }
    
    public class LocationTimer extends TimerTask
    {
    	public void run()
    	{
    		if(location.getLatitude()==0 && location.getLongitude()==0)
	    	{
    			mf.moveTo((int)(latitude*100*1000), (int)(longitude*100*1000));
    			mf.setZoom(8);
    			synchronized(App.getEventLock())
				{
					if(maps_rtf.getText().length() > 26)
					{
						if(maps_rtf.getText().length() >  32)
							maps_rtf.setText("Waiting on current location.");
						else
							maps_rtf.setText(maps_rtf.getText() + ".");
					}		
					else
						maps_rtf.setText("Waiting on current location.");
				}
	    	}
    		else
    		{
    			if(counter==0)
	    			synchronized(App.getEventLock())
	    			{
	    				maps_rtf.setText("");
	    				Maps_6(location.getLatitude(),location.getLongitude());
//	    				BBMaps(location.getLatitude(),location.getLongitude(),location.getAltitude());
	    				counter++;
	    			}
    			else
    				synchronized(App.getEventLock())
	    			{
	    				maps_rtf.setText("");
//	    				map_coord = new Coordinates(loc.getLatitude(),loc.getLongitude(),loc.getAltitude());
	    				lat.setText("Latitude - " + String.valueOf(location.getLatitude()));
	    				lng.setText("Longitude - " + String.valueOf(location.getLongitude()));
	    				acc.setText("Accuracy - "+String.valueOf(location.getAccuracy()));
	    				
	    				AddressInfo addressinfo;
	    				Maps_6(location.getLatitude(),location.getLongitude());
//	    				BBMaps(location.getLatitude(),location.getLongitude(),location.getAltitude());
	    				counter++;
	    			}
    		}
    	}
    }
    
    public boolean Check_NON_CAN_Operator()
   	{
   		boolean NON_CANOperatorCheck = true;
      	
   		final String CanadianOperators[] = {"Rogers Wireless" , "Telus" , "Bell"};
   		    	
   		String CurrentNetworkName = "";
   		    	
   		CurrentNetworkName = RadioInfo.getCurrentNetworkName();
   		    	
   		if( CurrentNetworkName.equalsIgnoreCase(CanadianOperators[0]) 
   		  			|| CurrentNetworkName.equalsIgnoreCase(CanadianOperators[1])
   		   			||CurrentNetworkName.equalsIgnoreCase(CanadianOperators[2]) )
   			NON_CANOperatorCheck = false;				//if Current Operator is CANADIAN then **FALSE**
   		else
   			NON_CANOperatorCheck = true;				//if Current Operator is not CANADIAN then **TRUE** hence ROAMING
   		    	
   		return NON_CANOperatorCheck;
   	 }
    
    public void BBMaps(double Lat,double Lng,float Alt)
    {
    	synchronized(Application.getEventLock())
		{
    		Device_Orientation = Display.getOrientation();
    		switch(Device_Orientation)
    		{
    		case Display.ORIENTATION_SQUARE:
    			mf_height = Display.getHeight()/3;
    			mf_width = Display.getWidth();
    			mf.setMapLabel("");
    			mf.setPreferredSize(mf_width, mf_height);
    			this.updateDisplay();
    			map_coord = new Coordinates(Lat,Lng, Alt);
    	    	mf.moveTo(map_coord);		//Maps pointing on the current location(if found unless Greenland)
    	    	mf.setZoom(ZOOM_MIN);
    			;
    		
    		case Display.ORIENTATION_PORTRAIT:
    			mf_height = Display.getHeight()/3;
    			mf_width = Display.getWidth();
    			mf.setMapLabel("");
    			mf.setPreferredSize(mf_width, mf_height);
    			this.updateDisplay();
    			map_coord = new Coordinates(Lat,Lng, Alt);
    	    	mf.moveTo(map_coord);		//Maps pointing on the current location(if found unless Greenland)
    	    	mf.setZoom(ZOOM_MIN);
    			;
    			
    		case Display.ORIENTATION_LANDSCAPE:
    			mf_height = Display.getHeight()/3;
    			mf_width = Display.getWidth();
//    			this.updateDisplay();
    			mf.setMapLabel("");
    			mf.setPreferredSize(mf_width, mf_height);	
    			this.updateDisplay();
    			map_coord = new Coordinates(Lat,Lng, Alt);
    	    	mf.moveTo(map_coord);		//Maps pointing on the current location(if found unless Greenland)
    	    	mf.setZoom(ZOOM_MIN);
    			;
    		}
		}
    }
    
    public void Maps_6(double latitude,double longitude)
    {
    	RichMapField richMap = MapFactory.getInstance().generateRichMapField();
    	MapDimensions dimension = new MapDimensions(Display.getWidth(),Display.getHeight()/3);
    	dimension.setZoom(ZOOM_MIN);
    	richMap.getMapField().setDimensions(dimension);
    	MapDataModel mapDataModel = richMap.getModel();
    	MapPoint mapPoint = new MapPoint(latitude,longitude);
    	MapLocation mapLocation = new MapLocation(mapPoint,"Last know location","something something");
    	int mapDataID = mapDataModel.add((Mappable)mapLocation, "Last location ", true);
    	mapDataModel.tag(mapDataID, "location");
    	add(richMap);
    }
    
    
}