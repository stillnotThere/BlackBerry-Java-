/*
* PersistentStoreDemoScreen.java
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

package com.rim.samples.device.persistentstoredemo;

import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component .*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import java.util.*;

/**
 * This screen displays a list of Meetings
 */
public final class PersistentStoreDemoScreen extends MainScreen 
{
    private ListField _meetingList;       
    private PersistentStoreDemo _uiApp;     
     
    /**
     * Creates a new PersistentStoreDemoScreen object
     * @param meetings A vector of persistable Meeting objects
     */
    public PersistentStoreDemoScreen(Vector meetings)
    {        
        _uiApp = (PersistentStoreDemo)UiApplication.getUiApplication();
        
        // Initialize UI components
        setTitle(new LabelField("Persistent Store Demo", LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH));        
        _meetingList = new ListField();        
        add(_meetingList); 
        
        // Set list field callback and update meeting list
        _meetingList.setCallback(_uiApp);
        updateList();        
        addMenuItem(newMeetingItem);         
        addMenuItem(retrieveItem);      
    }
    
    
    /**
     * Method to refresh our meetings list field
     */
    void updateList()
    {                
        _meetingList.setSize(_uiApp.getMeetings().size());               
    } 
    
    
    /**
     * Pushes a MeetingScreen to display the selected meeting
     * @param editable True if the meeting displayed should be editable, false if the meeting should be read only
     */
    void displayMeeting(boolean editable)
    {
        if( !_meetingList.isEmpty() )
        {
            int index = _meetingList.getSelectedIndex();   
            Vector meetings = _uiApp.getMeetings();        
            _uiApp.pushScreen( new MeetingScreen((Meeting)meetings.elementAt(index), index, editable) ); 
        }        
    } 
   
   /**
    * @see net.rim.device.api.ui.Screen#makeMenu(Menu,int)
    */ 
    protected void makeMenu(Menu menu, int instance)
    {
        if(_meetingList.getSize() > 0)
        {
            menu.add(viewItem);
            menu.add(editItem);
            menu.add(deleteItem);              
        }  
        
        super.makeMenu(menu, instance);      
    }
    
   
   /**
    * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
    */   
    protected boolean keyChar(char key, int status, int time)
     {
         // Intercept the ENTER key 
        if (key == Characters.ENTER)
        {
            displayMeeting(false);  
            return true;          
        }
        
        // Intercept the ESC key - exit the app on its receipt.
        if(key == Characters.ESCAPE)
        {
            _uiApp.persist();
            close();
            return true;    
        }
        return super.keyChar(key, status, time);
    }
    
    
    /**
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    protected boolean invokeAction(int action)
    {        
        switch(action)
        {
            case ACTION_INVOKE: // Trackball click
                displayMeeting(false);                
                return true; // We've consumed the event
        }    
        return  super.invokeAction(action);
    }  
     
    
    //Inner classes------------------------------------------------------------
    
    private MenuItem newMeetingItem = new MenuItem("New Meeting", 100, 1)
    {
        /**
         * Creates a new Meeting object and passes it to a new instance
         * of a MeetingScreen.
         */
        public void run()
        {
            Meeting meeting = new Meeting();
            _uiApp.pushScreen(new MeetingScreen(meeting,-1,true));
        }
    };
    
    
    private MenuItem viewItem = new MenuItem("View", 65636, 1)
    {
        /**
         * Displays the selected meeting for viewing
         */
        public void run()
        {            
            displayMeeting(false);
        }
    };
    
    
    private MenuItem editItem = new MenuItem("Edit", 65636, 2)
    {
        /**
         * Displays the selected meeting for editing
         */
        public void run()
        {            
            displayMeeting(true);
        }
    };
    
    
    private MenuItem deleteItem = new MenuItem("Delete", 65636, 3)
    {
        /**
         * Retrieves the highlighted Meeting object and removes it from the
         * vector, then updates the list field to reflect the change.
         */
        public void run()
        {            
            int i = _meetingList.getSelectedIndex();                
            String meetingName = ((Meeting)_uiApp.getMeetings().elementAt(i)).getField(Meeting.MEETING_NAME);
            int result = Dialog.ask(Dialog.DELETE, "Delete " + meetingName + "?");                
            if(result == Dialog.YES)
            {
                _uiApp.getMeetings().removeElementAt(i);
                updateList();            
            }            
        }
    }; 
    
    
    private MenuItem retrieveItem = new MenuItem("Access controlled object", 131172, 1)
    {      
        /**
         * Attempt to gain access to the controlled object.  If the module has
         * been signed with the ACME private key, the attempt will succeed.
         */
        public void run()
        {           
            PersistentObject controlledStore = PersistentStore.getPersistentObject(PersistentStoreDemo.PERSISTENT_STORE_DEMO_CONTROLLED_ID);              
            if(controlledStore != null)
            {
                try
                {                
                    Vector vector = (Vector)controlledStore.getContents();
                    if(vector != null)
                    {
                        Dialog.alert("Successfully accessed controlled object");
                    }
                }
                catch(final SecurityException se)
                {            
                    UiApplication.getUiApplication().invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            Dialog.alert("PersistentObject#getContents() threw " + se.toString());
                        } 
                    });
                }
            }                                
        }
    };  
}    
