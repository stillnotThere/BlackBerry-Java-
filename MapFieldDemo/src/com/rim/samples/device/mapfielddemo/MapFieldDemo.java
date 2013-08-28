/*
 * MapFieldDemo.java
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

package com.rim.samples.device.mapfielddemo;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.UiApplication;

 /**
  * This sample application provides an example how to leverage the
  * capabilities of the MapField API. Location data is read from a
  * text file and parsed using a string tokenizer class. This data
  * is then used to construct MapFieldDemoSite objects corresponding
  * to RIM locations around the world. A given site is drawn as a
  * polygon super-imposed on the currently rendered map.  
  */
public class MapFieldDemo extends UiApplication 
{
    /**
     * Entry point for application
     * @param args Command line arguments (not used)
     */ 
    public static void main(String[] args) 
    {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new MapFieldDemo().enterEventDispatcher();
    }
    
    // Constructor
    public  MapFieldDemo() 
    {
        pushScreen(new MapFieldDemoScreen());
    }
    
    /**
     * Presents a dialog to the user with a given message
     * @param message The text to display
     */
    public static void errorDialog(final String message)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable()
        {
            public void run()
            {
                Dialog.alert(message);
            } 
        });
    }
}
