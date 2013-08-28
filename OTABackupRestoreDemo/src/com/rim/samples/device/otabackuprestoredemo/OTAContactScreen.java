/**
 * OTAContactScreen.java
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

package com.rim.samples.device.otabackuprestoredemo;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

/**
 * This screen allows the user to view and edit contact information
 */
public final class OTAContactScreen extends MainScreen
{    
    private EditField _first, _last, _email;
    private int _uid = -1;
    private SaveMenuItem _saveMenuItem;   
    private BackMenuItem _backMenuItem; 
    private OTAContactData _contact;
        
    /**
     * A MenuItem class to saves the current contact
     */
    private class SaveMenuItem extends MenuItem
    {
        /**
         * Creates a new SaveMenuItem object
         */
        private SaveMenuItem()
        {
            super("Save" , 100000, 5);
        }
        
        
        /**
         * Saves the contact and closes this screen
         * @see java.lang.Runnable#run()
         */
        public void run()
        {            
            // If successful, return to contact list.
            if (onSave())
            {
                UiApplication uiapp = UiApplication.getUiApplication();
                uiapp.popScreen(uiapp.getActiveScreen());
            }
        }
    }
    
    
    /**
     * Closes this screen and goes back one screen
     */
    private static class BackMenuItem extends MenuItem
    {
        /**
         * Creates a new BackMenuItem object
         */
        private BackMenuItem()
        {
            super("Back" , 100000, 5);
        }
        
        
        /**
         * Closes this screen
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            UiApplication uiapp = UiApplication.getUiApplication();
            uiapp.popScreen(uiapp.getActiveScreen());
        }
    }
    
    
    /**
     * Creates a new OTAContactScreen object
     */
    public OTAContactScreen() 
    {
        _saveMenuItem = new SaveMenuItem();
        
        setTitle("Contact");
        
        _first = new EditField("First: " , "");
        add(_first);
        
        _last = new EditField("Last: " , "");
        add(_last);
        
        _email = new EditField("Email: " , "", BasicEditField.DEFAULT_MAXCHARS, BasicEditField.FILTER_EMAIL);
        add(_email);
    }
    
    
    /**
     * Creates a new OTAContactScreen object, specifying an existing contact to view/edit
     * @param contact The contact to display
     * @param editable True if the contact information is editable, otherwise false
     */
    public OTAContactScreen(OTAContactData contact, boolean editable)
    {
        this();
        
        _backMenuItem = new BackMenuItem();
        
        _contact = contact;
        _first.setText(_contact.getFirst());
        _first.setEditable(editable);
        _last.setText(_contact.getLast());
        _last.setEditable(editable);
        _email.setText(_contact.getEmail());
        _email.setEditable(editable);
        _uid = contact.getUID();
    }
    
    
    /**
     * Retrieves the contact being displayed on this screen
     * @return The contact being displayed on this screen
     */
    OTAContactData getContact()
    {
        return _contact;
    }
    
    
    /**
     * @see net.rim.device.api.ui.Screen#onSave()
     */
    protected boolean onSave()
    {
        String firstName = _first.getText().trim();
        String lastName = _last.getText().trim();
        String email = _email.getText().trim();
        
        // Check that first or last name and email has been entered
        if ((firstName.length() == 0 && lastName.length() == 0) || email.length() == 0)
        {
            Dialog.inform("Please enter a first or last name and an email address.");
            
            return false;
        }
        else
        {
            if( _uid == -1 ) 
            {
                // uid == -1 -> This is a new contact.
                _contact = new OTAContactData();
            }
            
            _contact.setFirst(firstName);
            _contact.setLast(lastName);
            _contact.setEmail(email);
         
            return true;        
        }        
    }
    
    
    /**
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
     */
    protected void makeMenu(Menu menu, int instance)
    {
        // If we are viewing a contact, we aren't able to edit it. In that case we
        // just want a menu item enabling us to go back to the contact list.
        if(_contact == null || _uid != -1)
        {
            menu.add(_saveMenuItem);
        }
        else
        {
            menu.add(_backMenuItem);
        }
        
        super.makeMenu(menu, instance);
    }         
}
