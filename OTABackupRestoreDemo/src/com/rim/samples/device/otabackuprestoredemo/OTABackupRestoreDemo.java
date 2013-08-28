/**
 * OTABackupRestoreDemo.java
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

import net.rim.device.api.synchronization.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

/**
 * This application demonstrates how to use the 
 * OTABackUpRestoreContactCollection class to back 
 * up contacts over the air onto a BES. See the
 * "readme.txt" file in this project for setup details.
 */
public class OTABackupRestoreDemo extends UiApplication implements ListFieldCallback
{    
    private static ListField _listField;
    private static AddContactAction _addContactAction;

    private static OTABackupRestoreContactCollection _contacts;

    /**
     * Adds a contact to the contact list
     */      
    private class AddContactAction extends MenuItem
    {
        /**
         * Creates a new AddContactAction object
         */
        private AddContactAction()
        {            
            super("Add" , 100000, 10);
        }
             
        
        /**
         * Adds a contact to the contact list
         */
        public void run()
        {
            // Retrieve the contact's information from the user
            OTAContactScreen screen = new OTAContactScreen();
            UiApplication.getUiApplication().pushModalScreen(screen);
            
            OTAContactData contact = screen.getContact();
            
            // Add the contact
            if (contact != null)
            {
                // Create a unique id for the contact - required for ota sync.
                contact.setUID(UIDGenerator.getUID());
                
                // Add the contact to the collection.
                _contacts.addSyncObject(contact);
            }
            
            reloadContactList();
        }
    }
    
    
    /**
     * Views a contact from the contact list
     */
    private static class ViewContactAction extends MenuItem
    {
        private int _index;
        
        /**
         * Constructs a menu item to view a specific contact from the contact list
         * @param index The index of the contact from the contact list to view
         */
        private ViewContactAction(int index)
        {
            super("View" , 100000, 5);
            _index = index;
        }
        
        
        /**
         * Displays the contact information. 
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            OTAContactScreen screen = new OTAContactScreen(_contacts.contactAt(_index), false);
            UiApplication.getUiApplication().pushScreen(screen);
        }
    }
    
    
    /**
     * A class to edits a contact
     */
    private static class EditContactAction extends MenuItem
    {
        private int _index;
        
        /**
         * Constructs a menu item to edit a specific contact from the contact list
         * @param index The index of the contact in the contact list to edit
         */
        private EditContactAction( int index )
        {
            super( "Edit" , 100000, 6 );
            _index = index;
        }
        
        
        /**
         * Edits the contact
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            OTAContactData oldContact = _contacts.contactAt(_index);
            OTAContactScreen screen = new OTAContactScreen( oldContact, true );
            UiApplication.getUiApplication().pushModalScreen( screen );
            
            // Get the newly updated contact
            OTAContactData newContact = screen.getContact();
            
            // Update the contact in the collection.
            _contacts.updateSyncObject( oldContact, newContact );
        }
    }
    
    
    /**
     * This is the main screen which displays the contact list and creates the
     * menu to let the user manipulate the contacts.
     */
    private static class OTABackupRestoreDemoScreen extends MainScreen
    {        
        /**
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(Menu menu, int instance)
        {
            menu.add(_addContactAction);        
            
            menu.addSeparator();
            
            int index = _listField.getSelectedIndex();
            
            if(index >= 0)
            {
                menu.add(new ViewContactAction(index));
                menu.add(new EditContactAction(index));
            }
            
            menu.addSeparator();
            
            super.makeMenu(menu, instance);
        }
    }
                  
    
    /**
     * Creates a new OTABackupRestoreDemo object
     */
    public OTABackupRestoreDemo() 
    {        
        // Create a new screen for the application
        OTABackupRestoreDemoScreen screen = new OTABackupRestoreDemoScreen();
                
        _addContactAction = new AddContactAction();
        
        screen.setTitle("OTA Backup/Restore Contacts");
        
        _listField = new ListField();        
        _listField.setCallback(this);        
        screen.add(_listField);

        // Push the screen onto the UI stack for rendering
        pushScreen(screen);        
        
        reloadContactList();
    } 
       
    
    /**
     * Refreshes the contact list on screen 
     */
    private void reloadContactList()
    {        
        _listField.setSize(_contacts.size());                                             
    }
    
    
    // ListFieldCallback methods ------------------------------------------------    
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField, Graphics, int, int, int)
     */
    public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) 
    {
        if ( listField == _listField && index < _contacts.size())
        {
            OTAContactData contact = _contacts.contactAt(index);
            String personal = contact.getFirst() + " " + contact.getLast();
            graphics.drawText(personal, 0, y, 0, width);
        }
    }
    
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField, int)
     */
    public Object get(ListField listField, int index)
    {
        if ( listField == _listField )
        {
            // If index is out of bounds an exception will be thrown, but
            // that's the behaviour we want in that case.
            return _contacts.contactAt(index);
        }
        
        return null;
    }
    
    
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(ListField listField) 
    {
        // Use all the width of the current LCD
        return Display.getWidth();
    }
 
 
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField , String, int)
     */        
    public int indexOfList(ListField listField, String prefix, int start) 
    {
        return -1; // Not implemented.
    }  
              
    
    /**
     * Entry point for the application.
     * @param args Command line arguments
     */
    public static void main(String[] args) 
    {
        boolean startup = false;
        
        for (int i=0; i<args.length; ++i) 
        {
            if (args[i].startsWith("init")) 
            {
                startup = true;
            }
        }
        
        // Get the collection enabled for ota backup/restore
        _contacts = OTABackupRestoreContactCollection.getInstance();
             
        if (startup) 
        {
            // Enable app for synchronization
            SyncManager.getInstance().enableSynchronization(_contacts);                 
        }
        else 
        {   
            // Create a new instance of the application and make the currently
            // running thread the application's event dispatch thread.
            OTABackupRestoreDemo app = new OTABackupRestoreDemo();
            app.enterEventDispatcher();
        }
    }       
}
