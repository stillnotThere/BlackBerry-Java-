package com.location.monitor;

import net.rim.device.api.ui.UiApplication;

/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class LocationApp extends UiApplication
{

	public static void main(String[] args)
    {
        LocationApp theApp = new LocationApp();       
        theApp.enterEventDispatcher();
    }
    
    public LocationApp()
    {        
        // Push a screen onto the UI stack for rendering.
        pushScreen(new LocationScreen());
    }    
}
