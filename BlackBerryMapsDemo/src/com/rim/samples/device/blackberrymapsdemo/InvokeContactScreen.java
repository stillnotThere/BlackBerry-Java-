/*
 * InvokeContactScreen.java
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

import java.util.Enumeration;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import javax.microedition.pim.*;

/**
 * This example looks for the first Contact  in the address book with a valid city
 * and region and displays a map for this address. If the address book contains no
 * addresses this program will simply display the default map location.  Also,
 * this example makes no attempt to exhaustively check all exception cases when
 * retrieving a Contact. For more information on retrieving and  manipulating
 * Contact information see the BlackBerry Development Guide.
 */
public final class InvokeContactScreen extends MainScreen
{
    // Constructor
    public InvokeContactScreen() 
    {              
        setTitle("Invoke Contact");
    
        LabelField instructions = new LabelField("Select 'View Map' from the menu.  The first Contact in the address book that has a valid address (at least a city and state/province defined) will be displayed.  If there are no valid addresses, map view will default to last view.");
        add(instructions);   
        
        addMenuItem(viewMapItem);
    }    
    
        
    /**
     * Displays a map based on an address from the address book
     */
    private MenuItem viewMapItem = new MenuItem("View Map" , 1000, 10) 
    {
        /**
         * Creates a list of Contacts from the address book and searches list
         * for first occurrence of a valid address.   
         */
        public void run() 
        {
            Contact c = null;       
            boolean foundAddress = false;   
              
            try 
            {
                // Create list of Contacts
                ContactList contactList = (ContactList)PIM.getInstance().openPIMList(PIM.CONTACT_LIST, PIM.READ_WRITE);
                Enumeration enumContact = contactList.items();                
                
                // Search for a valid address
                while ((enumContact.hasMoreElements())  && (!foundAddress)) 
                {
                    c = (Contact)enumContact.nextElement();                           
                                    
                    if (c.countValues(Contact.ADDR) > 0) 
                    {
                        String address[] = c.getStringArray(Contact.ADDR,0);
                        
                        if ((address[Contact.ADDR_LOCALITY] != null) && (address[Contact.ADDR_REGION] != null)) 
                        {
                            // Invoke maps application for current Contact
                            Invoke.invokeApplication(Invoke.APP_TYPE_MAPS, new MapsArguments(c,0)); 
                            foundAddress = true;
                        }
                    }
                }            
            } 
            catch (PIMException e) 
            {
                UiApplication.getUiApplication().invokeLater(new Runnable()
                {
                    public void run()
                    {
                        Dialog.alert("PIM#openPIMList() threw PIMException");
                    } 
                });
            }
            
            // Invoke maps application with default map                    
            if(!foundAddress) 
            {
                Invoke.invokeApplication(Invoke.APP_TYPE_MAPS, new MapsArguments());
            }
        }
    };
}

