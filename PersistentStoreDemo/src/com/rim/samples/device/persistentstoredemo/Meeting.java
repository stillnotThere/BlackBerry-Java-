/*
 * Meeting.java
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

import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import java.util.*;

/**
 * This class represents a persistable meeting object.  It contains information
 * such as the name of the meeting, a description, date and time as well as
 * names of those who were in attendance.  This information is encoded and
 * stored in a pair of Vectors, _fields and _attendees. Classes to be persisted
 * must implement interface Persistable and can only can contain members which
 * themselves implement Persistable or are inherently persistable. 
 */ 
public final class Meeting implements Persistable
{
    static final int MEETING_NAME = 0;
    static final int DESC = 1;
    static final int DATE = 2;
    static final int TIME = 3;        
    static final int NOTES = 4;
        
    // Change this value if any fields are added to or removed from this class
    private static final int NUM_FIELDS = 5;
        
    private Vector _fields;
    private Vector _attendees; 
        
    // Primitive data types can be persisted.  The following class members are
    // included for demonstration purposes only, they have no functional use in
    // this class.
    private int demoInt; 
    private boolean demoBool;
    private byte demoByte;
    private short demoShort;
    private long demoLong;
    private float demoFloat;
    private double demoDouble;
    private char demoChar;
    
    
    /**
     * Creates a new Meeting object
     */
    public Meeting()
    {
        _attendees = new Vector();               
        _fields = new Vector(NUM_FIELDS);
        for (int i = 0; i < NUM_FIELDS; ++i)
        {
            _fields.addElement("");
        }
    } 
    
    
    /**
    * Retrieves an encoded object and returns it as a plaintext string. 
     * @param id The ID of the field from which the encoding should be retrieved
     * @return A plaintext string
     */
    String getField(int id)
    {        
        Object encoding = _fields.elementAt(id);
        
        // Acquiring a reference to a ticket guarantees access to encrypted data
        // even if the device locks during the decoding operation.
        Object ticket  = PersistentContent.getTicket();
        
        if(ticket != null)
        {
            return PersistentContent.decodeString(encoding);
        }
        else
        {
            return null;
        }    
    }
    
    
    /**
     * Stores a string as an encoded object according to device content
     * protection/compression settings.  
     * @param id The ID of the field where the encoding is to be stored
     * @param value The plaintext string to be encoded and stored
     */
    void setField(int id, String value)
    {
        Object encoding = PersistentContent.encode(value);
        _fields.setElementAt(encoding, id);
    }  
    
     
   /**
    *  Encodes a string and adds it to the attendees vector
    * @param attendee String to be added to the attendees vector
    */     
    void addAttendee(String attendee)
    {
        Object encoding = PersistentContent.encode(attendee);        
        _attendees.addElement(encoding);        
    }   
    
    
    /**
     * Returns a vector containing all attendees.
     * @return Vector of decoded strings
     */
    Vector getAttendees()
    {         
        Object encoding;
        Vector decodedAttendees = new Vector();
        
        // Acquiring a reference to a ticket guarantees access to encrypted data
        // even if the device locks during the decoding operation operation.
        Object ticket  = PersistentContent.getTicket();
        
        if(ticket != null)
        {
            for(int i = 0; i < _attendees.size(); i++)
            {
                decodedAttendees.addElement(PersistentContent.decodeString(_attendees.elementAt(i)));
            }
        }
        return decodedAttendees;       
    }
        
    
    /**
     * Forces a re-encoding of the information stored in this Meeting object. 
     */
    void reEncode()
    {
        // Acquiring a reference to a ticket guarantees access to encrypted data
        // even if the device locks during the re-encoding operation.
        Object ticket  = PersistentContent.getTicket();
        
        if(ticket != null)
        {
            for (int i = 0; i < NUM_FIELDS; ++i)
            {
                Object encoding = _fields.elementAt(i);
                if(!PersistentContent.checkEncoding(encoding))
                {
                    encoding = PersistentContent.reEncode(encoding);
                    _fields.setElementAt(encoding, i);
                }
            }
        }
    }
}
