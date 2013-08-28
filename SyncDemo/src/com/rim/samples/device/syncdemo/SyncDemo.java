/**
 * SyncDemo.java
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

import java.io.*;
import java.util.*;
import net.rim.device.api.synchronization.*;
import net.rim.device.api.util.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.i18n.Locale;


/**
 * This application stores contact information in a PersistantObject which can
 * be synchronized with BlackBerry Desktop Manager using Backup and Restore. 
 * 
 * Note: This class always retrieves the most recent contact list by loading the
 *       contacts from the persistant store so any instance of the SyncDemo 
 *       class may be used for synchronization.
 */
public final class SyncDemo extends UiApplication implements SyncConverter, SyncCollection, ListFieldCallback
{
    // Members ------------------------------------------------------------------
    private ListField _listField;           
    private AddContactAction _addContactAction;    
    private ViewContactAction _viewContactAction;    
    
    // Statics ------------------------------------------------------------------ 
    private static final int FIELDTAG_FIRST_NAME = 1;
    private static final int FIELDTAG_LAST_NAME = 2;
    private static final int FIELDTAG_EMAIL_ADDRESS = 3;
    private static final long KEY = -2115940372; // Hash of com.rim.samples.device.syncdemo
    
    private static PersistentObject _persist;    
    private static Vector _contacts;
    

    /**
     * Entry point for application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) 
    { 
    
        _persist = PersistentStore.getPersistentObject(KEY);         
        _contacts = (Vector) _persist.getContents();        
        
        if (args != null && args.length >0 && args[0].equals("init")) 
        {                 
            // Initialize persistent store on startup.
            if (_contacts == null) 
            {
                _contacts = new Vector();
                _persist.setContents(_contacts);
                _persist.commit();
            }
            
            // Enable app for synchronization             
            SyncManager.getInstance().enableSynchronization(new SyncDemo());                 
        }
        else 
        {          
            // Create a new instance of the application and make the currently
            // running thread the application's event dispatch thread.
            SyncDemo app = new SyncDemo();
            app.enterEventDispatcher();
        }
    }       
    

    // Inner classes -----------------------------------------------------------    
    /**
     * Adds a contact to the persistent store.
     */
    private class AddContactAction extends MenuItem
    {
        /**
         * Default constructor
         */ 
        private AddContactAction()
        {            
            super("Add", 100000, 10);
        }
     
        public void run()
        {
            ContactScreen screen = new ContactScreen();
            UiApplication.getUiApplication().pushModalScreen(screen);
            
            ContactData contact = screen.getContact();
            
            if (contact != null)
            {
                _contacts.addElement(contact);
                _persist.setContents(_contacts);
                _persist.commit();
            }
            
            reloadContactList();
        }
    }
    
    /**
     * Views the selected contact's information.
     */
    private class ViewContactAction extends MenuItem
    {    
        /**
         * Default constructor
         */ 
        public ViewContactAction()
        {
            super("View", 100001, 10);            
        }
        
        public void run()
        {            
            ContactScreen screen = new ContactScreen((ContactData)_contacts.elementAt(_listField.getSelectedIndex()), false);
            UiApplication.getUiApplication().pushScreen(screen);
        }
    }
    
    /**
     * Edits a contact.
     */
    private class EditContactAction extends MenuItem
    {
        private int _index;
        
        /**
         * Constructs a menu item to edit a specific contact from the contact
         * list.
         * @param index The index of the contact in the contact list to edit
         */
        private EditContactAction(int index)
        {
            super("Edit" , 100000, 6);
            _index = index;
        }
        
        public void run()
        {
            ContactData oldContactData = (ContactData) _contacts.elementAt(_index);
            ContactScreen screen = new ContactScreen(oldContactData, true);
            UiApplication.getUiApplication().pushModalScreen(screen);
            
            ContactData newContactData = screen.getContact();
            
            if (newContactData != null)
            {
                if(_contacts.contains(oldContactData)) 
                    {
                        _contacts.setElementAt(newContactData, _contacts.indexOf(oldContactData));
                        PersistentObject.commit(_contacts);
                    }
            }
            
            reloadContactList();
        }
    }
    
    /**
     * Deletes a contact.
     */
    private class DeleteContactAction extends MenuItem 
    {
        private int _deleteIndex;
        
        /**
         * Constructs a menu item to delete a specific contact when invoked.
         * @param deleteIndex The index of the contact to delete.
         */
        private DeleteContactAction(int deleteIndex) 
        {
            super("Delete", 100000, 7);
            _deleteIndex = deleteIndex;
        }

        public void run() 
        {
            ContactData contactData = (ContactData)_contacts.elementAt(_deleteIndex);
            
            int result = Dialog.ask(Dialog.DELETE, "Delete " + contactData.getFirst() + " " + contactData.getLast() + "?");
            if(result == Dialog.YES)
            { 
                _contacts.removeElementAt(_deleteIndex);
                reloadContactList();
            }
        }
    }    
    
    /**
     * This screen acts as the main screen which allows the user to add and view
     * synchronized contacts.
     */
    private final class SyncDemoScreen extends MainScreen
    {
        /**
         * Default constructor
         */ 
        private SyncDemoScreen()
        {
            setTitle(new LabelField("Contacts", LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH));         
        }   
        
        /**
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(Menu menu, int instance)
        {
            menu.add(_addContactAction);
            menu.addSeparator();
            
            if(_contacts.size() > 0) 
            {
                
                EditContactAction _editContactAction = new EditContactAction(_listField.getSelectedIndex());
                menu.add(_editContactAction);
                
                DeleteContactAction _deleteContactAction = new DeleteContactAction (_listField.getSelectedIndex());
                menu.add(_deleteContactAction);
                
                menu.add(_viewContactAction);
            }        
            
            menu.addSeparator();
            
            super.makeMenu(menu, instance);
        }    
    }                      
    
    /**
     * Default constructor
     */  
    public SyncDemo() 
    {        
        
        _listField = new ListField();        
        _listField.setCallback(this); 
        
        _addContactAction = new AddContactAction();
        _viewContactAction = new ViewContactAction();
        
        // Create a new screen for the application.
        SyncDemoScreen screen = new SyncDemoScreen();
                
        screen.add(_listField);
                
        // Push the screen onto the UI stack for rendering.
        pushScreen(screen);        
        
        reloadContactList();        
    }    
    
    /**
     * Refreshes contact list on screen.
     * @return True
     */
    private void reloadContactList()
    {
        _listField.setSize(_contacts.size());    
    }
    
    // SyncConverter methods ----------------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.SyncConverter#convert(SyncObject,DataBuffer,int)
     */
    public boolean convert(SyncObject object, DataBuffer buffer, int version)
    {
        if (version == getSyncVersion())
        {
            if (object instanceof ContactData)
            {                
                String first = ((ContactData)object).getFirst();
                String last = ((ContactData)object).getLast();
                String email = ((ContactData)object).getEmail();
                
                // Write the contact information to the DataBuffer.
                ConverterUtilities.writeString(buffer, FIELDTAG_FIRST_NAME, first);
                ConverterUtilities.writeString(buffer, FIELDTAG_LAST_NAME, last);
                ConverterUtilities.writeString(buffer, FIELDTAG_EMAIL_ADDRESS, email);
                
                return true;            
            }
        }
        
        return false;
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncConverter#convert(DataBuffer,int,int)
     */
    public SyncObject convert(DataBuffer data, int version, int UID)
    {
        ContactData contact = new ContactData(UID);
        
        try
        {
            // Extract the contact information from the DataBuffer.
            while(data.available() > 0)
            {
                if(ConverterUtilities.isType(data, FIELDTAG_FIRST_NAME))
                {
                    contact.setFirst(new String(ConverterUtilities.readByteArray(data)).trim());
                }
                
                if(ConverterUtilities.isType(data, FIELDTAG_LAST_NAME))
                {
                    contact.setLast(new String(ConverterUtilities.readByteArray(data)).trim());
                }
                
                if(ConverterUtilities.isType(data, FIELDTAG_EMAIL_ADDRESS))
                {
                    contact.setEmail(new String(ConverterUtilities.readByteArray(data)).trim());
                }
            }
            
            return contact;
        }
        catch (EOFException e)
        {
            System.err.println(e.toString());
        }
        
        return null;        
    }
    
    // SyncCollection methods ----------------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#addSyncObject(SyncObject)
     */
    public boolean addSyncObject(SyncObject object)
    {
        _contacts.addElement(object);
        
        return true;
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#updateSyncObject(SyncObject,SyncObject)
     */    
    public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject)
    {
        return false; // NA
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#removeSyncObject(SyncObject)
     */
    public boolean removeSyncObject(SyncObject object)
    {
        return false; // NA
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#removeAllSyncObjects()
     */
    public boolean removeAllSyncObjects()
    {
        return false; // NA
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncObjects()
     */
    public SyncObject[] getSyncObjects()
    {
        SyncObject[] contactArray = new SyncObject[_contacts.size()];
        
        for (int i = _contacts.size() - 1; i >= 0; --i)
        {
            contactArray[i] = (SyncObject)_contacts.elementAt(i);
        }
        
        return contactArray;
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncObject(int)
     */
    public SyncObject getSyncObject(int uid)
    {
        for (int i = _contacts.size() - 1; i >= 0; --i)
        {
            SyncObject so = (SyncObject)_contacts.elementAt(i);
            
            if (so.getUID() == uid) 
            {
                return so;
            }
        }
        
        return null;
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#isSyncObjectDirty(SyncObject)
     */
    public boolean isSyncObjectDirty(SyncObject object)
    {
        return false; // NA
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#setSyncObjectDirty(SyncObject)
     */
    public void setSyncObjectDirty(SyncObject object)
    {
        // NA
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#clearSyncObjectDirty(SyncObject)
     */
    public void clearSyncObjectDirty(SyncObject object)
    {
        // NA
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncObjectCount()
     */
    public int getSyncObjectCount()
    {
        _persist = PersistentStore.getPersistentObject(KEY);        
        _contacts = (Vector)_persist.getContents();  
              
        return _contacts.size();
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncVersion()
     */
    public int getSyncVersion()
    {
        return 1;
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncName()
     */    
    public String getSyncName()
    {
        return "Contacts";
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncName(Locale)
     */
    public String getSyncName(Locale locale)
    {
        return null;
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncConverter()
     */    
    public SyncConverter getSyncConverter()
    {
        return this;
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#beginTransaction()
     */
    public void beginTransaction()
    {
        _persist = PersistentStore.getPersistentObject(KEY);        
        _contacts = (Vector)_persist.getContents();
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#endTransaction()
     */
    public void endTransaction()
    {
        _persist.setContents(_contacts);
        _persist.commit();
    }
    
    // ListFieldCallback methods ------------------------------------------------
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
     */
    public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) 
    {
        if (listField == _listField && index < _contacts.size())
        {
            ContactData contact = (ContactData)_contacts.elementAt(index);
            String personal = contact.getFirst() + " " + contact.getLast();
            graphics.drawText(personal, 0, y, 0, width);
        }
    }
    
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField , int)
     */
    public Object get(ListField listField, int index)
    {
        if (listField == _listField)
        {
            // If index is out of bounds an exception will be thrown, but that's the 
            // behaviour we want in that case.
            return _contacts.elementAt(index);
        }
        
        return null;
    }
    
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(ListField listField) 
    {
        // Use all the width of the current LCD.
        return Display.getWidth();
    }
 
    
    public int indexOfList(ListField listField, String prefix, int start) 
    {
        return -1; // Not implemented.
    }       
}
