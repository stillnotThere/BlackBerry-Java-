/*
 * OTASyncDemo.java
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

package com.rim.samples.device.otasyncdemo;

import net.rim.device.api.synchronization.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

/**
 * Sample to demonstrate synchronization of contact data with a simulated BES
 * environment.  This sample requires the BlackBerry Sync Server SDK and the
 * BlackBerry Email simulator.  For more information on how to use this sample
 * see the Synchronization Server SDK Development Guide and the readme.txt file
 * included in this project. 
 */

public class OTASyncDemo extends UiApplication implements ListFieldCallback
{
    // Members ------------------------------------------------------------------
    private ListField _listField;
    private AddContactAction _addContactAction;
    private RefreshAction _refreshAction;
    
    // Statics ------------------------------------------------------------------
    private static OTAContactCollection _otaContactCollection;    

    // Inner Classes ------------------------------------------------------------    
    private class AddContactAction extends MenuItem
    {
        /**
         * Default constructor
         */
        private AddContactAction()
        {            
            super("Add" , 100000, 5);
        }
     
        /**
         * Adds a new contact to the contact collection
         * @see java.lang.Runnable@run()
         */
        public void run()
        {
            OTAContactScreen screen = new OTAContactScreen();
            UiApplication.getUiApplication().pushModalScreen(screen);
            
            OTAContactData contact = screen.getContact();
            
            if (contact != null)
            {
                contact.setGUID( UIDGenerator.getUID() );
                _otaContactCollection.addSyncObject( contact );
            }
            
            reloadContactList();
        }
    }
    
    private class EditContactAction extends MenuItem
    {
        private int _contactIndex;
        
        /**
         * Constructs a menu item to edit a specific contact when invoked
         * @param contactIndex The index of the contact to edit
         */
        private EditContactAction( int contactIndex ) 
        {
            super("Edit" , 100000, 6);
            _contactIndex = contactIndex;
        }
        
        /**
         * Display a screen to allow the user to edit the contact
         * @see java.lang.Runnable#run()
         */
        public void run() 
        {
            OTAContactData oldContactData = (OTAContactData)_otaContactCollection.getAt(_contactIndex);
            OTAContactScreen screen = new OTAContactScreen( oldContactData );
            UiApplication.getUiApplication().pushModalScreen( screen );
            
            OTAContactData newContactData = screen.getContact();
            
            if( newContactData != null ) 
            {
                _otaContactCollection.updateSyncObject( oldContactData, newContactData );
            }
            
            reloadContactList();
        }
    }
    
    private class DeleteContactAction extends MenuItem 
    {
        private int _deleteIndex;
        
        /**
         * Constructs a menu item to delete a specific contact when invoked
         * @param contactIndex The index of the contact to delete
         */
        private DeleteContactAction( int deleteIndex ) 
        {
            super("Delete" , 100000, 7 );
            _deleteIndex = deleteIndex;
        }
        
        /**
         * Delete the contact
         * @see java.lang.Runnable#run()
         */
        public void run() 
        {
            OTAContactData contactData = (OTAContactData)_otaContactCollection.getAt(_deleteIndex);
            
            int result = Dialog.ask(Dialog.DELETE, "Delete " + contactData.getFirst() + " " + contactData.getLast() + "?");
            if(result == Dialog.YES)
            { 
                _otaContactCollection.removeSyncObject( (SyncObject)_otaContactCollection.getAt(_deleteIndex) );
                reloadContactList();
            }
        }
    }
    
    /**
     * An action to refresh the screen's contact list
     */
    private class RefreshAction extends MenuItem 
    {
        /**
         * Default constructor
         */
        private RefreshAction() 
        {
            super("Refresh" , 100000, 8 );
        }        
       
        /**
         * Refreshes the contact list
         * @see java.lang.Runnable#run()
         */
        public void run() 
        {
            reloadContactList();
        }
    }
    
    /**
     * The main screen to display the menu and contact list
     */
    private class OTASyncDemoScreen extends MainScreen
    {
        
        /**
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(Menu menu, int instance)
        {
            menu.add(_addContactAction);
            menu.add(_refreshAction);
            
            if( _otaContactCollection.getSyncObjectCount() > 0 ) 
            {
                EditContactAction _editContactAction = new EditContactAction( _listField.getSelectedIndex() );
                menu.add( _editContactAction );
                
                DeleteContactAction _deleteContactAction = new DeleteContactAction ( _listField.getSelectedIndex() );
                menu.add( _deleteContactAction );
            }        
            
            menu.addSeparator();
            
            super.makeMenu(menu, instance);
        }
    }
            
    
    /**
     * Default constructor.
     */
    public OTASyncDemo() 
    {
        // Create a new screen for the application
        OTASyncDemoScreen screen = new OTASyncDemoScreen();
                
        _addContactAction = new AddContactAction();
        _refreshAction = new RefreshAction();
        
        screen.setTitle("OTA Sync Demo Contacts");
        
        _listField = new ListField();        
        _listField.setCallback(this);        
        screen.add(_listField);

        // Push the screen onto the UI stack for rendering.
        pushScreen(screen);        
        
        reloadContactList();
    }    
    
    /**
     * Refreshes the contact list displayed on the screen
     * @return True
     */
    private boolean reloadContactList()
    {           
            // Refreshes contact list on screen.
            _listField.setSize(_otaContactCollection.getSyncObjectCount());            
            
            return true;            
    }    
    
    // ListFieldCallback methods ------------------------------------------------
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
     */
    public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) 
    {
        if ( listField == _listField && index < _otaContactCollection.getSyncObjectCount())
        {
            OTAContactData contact = (OTAContactData)_otaContactCollection.getAt(index);
            String personal = contact.getFirst() + " " + contact.getLast();
            graphics.drawText(personal, 0, y, 0, width);
        }
    }
    
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField , int)
     */
    public Object get(ListField listField, int index)
    {
        if ( listField == _listField )
        {
            // If index is out of bounds an exception will be thrown, but 
            // that's the behaviour we want in that case.
            return _otaContactCollection.getAt(index);
        }
        
        return null;
    }
    
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(ListField listField) 
    {
        // use all the width of the current LCD
        return Display.getWidth();
    }
 
    
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField , String , int)
     */    
    public int indexOfList(ListField listField, String prefix, int start) 
    {
        return -1; // Not implemented
    }        
    
    /**
     * Entry point of the application
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
        
        _otaContactCollection = OTAContactCollection.getInstance();
             
        if (startup)
        {
            // Enable app for synchronization
            SyncManager.getInstance().enableSynchronization( _otaContactCollection );                 
        }
        else
        {
            // Create a new instance of the application and make the currently
            // running thread the application's event dispatch thread.
            OTASyncDemo app = new OTASyncDemo();
            app.enterEventDispatcher();
        }
    }       
}
