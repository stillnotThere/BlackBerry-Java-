/*
 * MeetingScreen.java
 *
 * Copyright � 1998-2010 Research In Motion Ltd.
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
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import java.util.*;

/**
 * This screen allows the user to update the meeting information stored in an 
 * associated Meeting object.
 */
public final class MeetingScreen extends MainScreen implements ListFieldCallback
{
    private EditField _nameField;
    private EditField _descField;
    private EditField _dateField;
    private EditField _timeField;
    private EditField _notesField; 
    private PopupScreen _popUp; 
    private EditField _addAttendeeField;
    private PersistentStoreDemo _uiApp;
    private ListField _attendeesList;       
    private MeetingScreen _screen;    
    private Meeting _meeting;    
    private int _index;           
   
    /**
     * Creates a new MeetingScreen object   
     * @param meeting The Meeting object associated with this screen
     * @param index The position of the meeting in the list.  A value of -1 represents a new meeting.
     * @param editable Indicates whether the screens fields are editable
     */
    public MeetingScreen(Meeting meeting, int index, boolean editable)
    {   
        _meeting = meeting;        
        _index = index;          
        
        // We need references to our application and this screen
        _uiApp = (PersistentStoreDemo)UiApplication.getUiApplication();
        _screen = this;
        
        // Initialize UI components       
        _nameField = new EditField("Meeting Name: ",_meeting.getField(Meeting.MEETING_NAME));
        _descField = new EditField("Description: ",_meeting.getField(Meeting.DESC));
        _dateField = new EditField("Date: ",_meeting.getField(Meeting.DATE));        
        _timeField = new EditField("Time: ",_meeting.getField(Meeting.TIME));        
        _notesField = new EditField("Notes: ",_meeting.getField(Meeting.NOTES));             
        add(_nameField);
        add(_descField);
        add(_dateField);
        add(_timeField);
        add(_notesField);   
        
        
        // Customize screen based on our editable state
        if(editable)
        {            
            setTitle("Edit Screen");   
            addMenuItem(saveItem);
            addMenuItem(addAttendeeItem);         
        }
        else
        {
            setTitle(new LabelField("View Screen"));            
            _nameField.setEditable(false);
            _descField.setEditable(false);
            _dateField.setEditable(false);
            _timeField.setEditable(false);
            _notesField.setEditable(false);
        }
        
        // Initialize the attendees list field
        _attendeesList = new ListField();        
        add(new RichTextField("Attendees:",Field.NON_FOCUSABLE));
        add(_attendeesList); 
        
        
        // Set callback and update list of attendees
        _attendeesList.setCallback(this);
        updateList();  
    }
         
    
    /**
     * Refreshes the attendees list field
     */
    private void updateList()
    {  
        _attendeesList.setSize(_meeting.getAttendees().size());              
    }
    
    
    /**
     * Saves the current field contents in the associated Meeting object
     * @see net.rim.device.api.ui.Screen#onSave()
     */
    protected boolean onSave()
    {
        if(!(_nameField.getText().equals("")))
        {
            _meeting.setField(Meeting.MEETING_NAME,_nameField.getText());
            _meeting.setField(Meeting.DESC,_descField.getText());            
            _meeting.setField(Meeting.DATE,_dateField.getText());
            _meeting.setField(Meeting.TIME,_timeField.getText());
            _meeting.setField(Meeting.NOTES,_notesField.getText());                       
            _uiApp.saveMeeting(_meeting, _index);               
            return super.onSave();        
        }
        else
        {
            Dialog.alert("Meeting name required");
            return false;
        }
    }  
     
    
    //Inner classes------------------------------------------------------------
    
    private MenuItem addAttendeeItem = new MenuItem("Add Attendee", 11000, 10)
    {
        /**
         * Display popup screen which allows user to enter the name
         * of an attendee.
         */
        public void run()
        {            
            VerticalFieldManager vfm = new VerticalFieldManager();
            _popUp = new PopupScreen(vfm);
            _addAttendeeField = new EditField("Enter Name: ","");
            _popUp.add(_addAttendeeField);            
            HorizontalFieldManager hfm = new HorizontalFieldManager(HorizontalFieldManager.FIELD_HCENTER);                  
            hfm.add(new AddButton());
            hfm.add(new CancelButton());
            _popUp.add(hfm);
            _uiApp.pushScreen(_popUp);                    
        }
    };
    
    
    private MenuItem saveItem = new MenuItem("Save", 11000, 11)
    {
        /**
         * Saves the meeting and closes this screen.
         */
        public void run()
        {
            if(onSave())
            {
                close();                                     
            }
        }
    };   
     
     
    /**
     * Represents Add button in Add Attendee pop up.  Adds attendee name to
     * the new Meeting object.
     */ 
    private final class AddButton extends ButtonField
    {   
        /**
         * Creates a new AddButton object
         */
        public AddButton()
        {
            super("Add",ButtonField.CONSUME_CLICK);
        }
        
        
        /**
         * @see net.rim.device.api.ui.Field#fieldChangeNotify(int)
         */
        protected void fieldChangeNotify(int context)
        {
            if ((context & FieldChangeListener.PROGRAMMATIC) == 0) 
            {     
                // Add attendee name and refresh list                                
                _meeting.addAttendee(_addAttendeeField.getText());
                _screen.updateList();
                                                                                
                // If no other fields have been edited, we need to set the
                // screen's state to dirty so that a save dialog will be
                // displayed when the screen is closed.
                if(!_screen.isDirty())
                {
                    _screen.setDirty(true);                              
                }                
                _popUp.close();                
             }
        }        
    }
    
    
    /**
     * Represents Cancel button in Add Attendee pop up.  Closes the pop up screen. 
     */
    private final class CancelButton extends ButtonField
    {
        /**
         * Creates a new CancelButton object
         */
        public CancelButton()
        {
            super("Cancel",ButtonField.CONSUME_CLICK);           
        }
        
        /**          
         * @see net.rim.device.api.ui.Field#fieldChangeNotify(int)
         */
        protected void fieldChangeNotify(int context)
        {
            if ((context & FieldChangeListener.PROGRAMMATIC) == 0) 
            {
                _popUp.close();                
            }
        }
    } 
    
    
    // ListFieldCallback methods ----------------------------------------------------------------------
    
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
     */
    public void drawListRow(ListField list, Graphics graphics, int index, int y, int w)
    {
        Vector attendees = _meeting.getAttendees();
        String text = (String)attendees.elementAt(index);
        graphics.drawText(text, 0, y, 0, w);
    }
    

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField , int)
     */
    public Object get(ListField list, int index)
    {
        return null; // Not implemented
    }
    

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField , String , int)
     */   
    public int indexOfList(ListField list, String p, int s)
    {
        return 0; // Not implemented
    }
    

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(ListField list)
    {
        return Display.getWidth();
    }    
} 
