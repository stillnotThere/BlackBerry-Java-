/**
 * ContactScreen.java
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

package com.rim.samples.device.syncdemo;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;


/**
 * This screen allows the user to view and edit a contact's information.
 */
public final class ContactScreen extends MainScreen
{
    // Members ------------------------------------------------------------------
    private EditField _first, _last, _email;
    private SaveMenuItem _saveMenuItem;    
    private ContactData _contact;
    
    // Inner classes ------------------------------------------------------------    
    /**
     * Saves the current contact's information.
     */
    private class SaveMenuItem extends MenuItem
    {
        /**
         * Default constructor
         */
        private SaveMenuItem()
        {
            super("Save" , 100000, 5);
        }
        
        /**
         * Saves and closes this screen.
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
     * Default constructor
     */
    public ContactScreen() 
    {
        _saveMenuItem = new SaveMenuItem();
        
        setTitle(new LabelField("Contact" , LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH));
        
        _first = new EditField("First: " , "");
        add(_first);
        
        _last = new EditField("Last: " , "");
        add(_last);
        
        _email = new EditField("Email: " , "", BasicEditField.DEFAULT_MAXCHARS, BasicEditField.FILTER_EMAIL);
        add(_email);
        
        addMenuItem(_saveMenuItem);
    }
    
    /**
     * Constructor to display an existing contact and allow the user to edit the
     * information.
     * @param contact The contact data to view and edit
     * @param editable True if the contact is editable, otherwise false
     */
    public ContactScreen(ContactData contact, boolean editable)
    {
        this();     
        
        _contact = contact;
        _first.setText(_contact.getFirst());
        _first.setEditable(editable);        
        _last.setText(_contact.getLast());
        _last.setEditable(editable);
        _email.setText(_contact.getEmail());
        _email.setEditable(editable);        
    }
    
    /**
     * Gets the contact information displayed by this screen.
     * @return The contact information displayed by this screen
     */
    public ContactData getContact()
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
        
        // Check that a name and an email has been entered.
        if (( firstName.length() == 0 && lastName.length() == 0 ) || email.length() == 0)
        {
            Dialog.inform("First or Last Name and Email required");
            return false;
        }
        else
        {
            _contact = new ContactData();
            
            _contact.setFirst(firstName);
            _contact.setLast(lastName);
            _contact.setEmail(email);
         
            return true;        
        }        
    }

}
