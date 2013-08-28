/*
 * InvokeMapViewScreen.java
 *
 * Copyright © 1998-2010 Research In Motion Ltd.
 * 
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

package com.rim.samples.device.blackberrymapsdemo;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.blackberry.api.maps.MapView;

/**
 * This example uses of a MapView object when invoking the the BlackBerryMaps
 * application.
 */
public final class InvokeMapViewScreen extends MainScreen
{      
    // Constructor
    InvokeMapViewScreen() 
    {
        setTitle("Invoke Map View");  
          
        LabelField instructions = new LabelField("Select 'View Map' from the menu.  The MapView object is set to Zoom Level 3. Location is Ottawa, ON, Canada at Latitude 45.42349, Longitude -75.69792");
        add(instructions);    
        
        addMenuItem(viewMapItem);   
    }    
        
    /**
     * Invokes BlackBerry Maps application using a MapView object
     */
    private MenuItem viewMapItem = new MenuItem("View Map" , 1000, 10) 
    {
        public void run() 
        {
            MapView mapview = new MapView();
            mapview.setLatitude(4542349);
            mapview.setLongitude(-7569792);
            mapview.setZoom(3);
            
            // Invoke maps application with specified MapView
            Invoke.invokeApplication(Invoke.APP_TYPE_MAPS, new MapsArguments(mapview));
        }
    };
}

