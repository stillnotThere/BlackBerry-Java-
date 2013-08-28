/*
 * InvokeDefaultScreen.java
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

/**
 * This example invokes the BlackBerry Map application with a MapArgument object
 * constructed with no arguments.  The resulting map view will be the last map 
 * view displayed by BlackBerry Maps or the default map view if BlackBerry Maps
 * is being run for the first time.  
 */

public final class InvokeDefaultScreen extends MainScreen
{
    //Constructor
    public InvokeDefaultScreen() 
    {           
        setTitle("Invoke Default");
        
        LabelField instructions = new LabelField("Select 'View Map' from the menu to see the default map view.");
        add(instructions);     
        
        addMenuItem(viewMapItem);     
    }    
    
    /**
     * Displays the default map
     */
    private MenuItem viewMapItem = new MenuItem("View Map" , 1000, 10) 
    {
        // Invoke maps application with default map                    
        public void run() 
        {
            Invoke.invokeApplication(Invoke.APP_TYPE_MAPS, new MapsArguments());
        }
    };     
}

