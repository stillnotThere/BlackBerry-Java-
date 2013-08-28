/**
 * OTAContactData.java
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
import net.rim.device.api.util.Persistable;

/**
 * This class represents a contact, encapsulating the contact's information.
 * It also implements the SyncObject and Persistable interfaces which allow the 
 * OTAContactData to synchronize to a BES and allow the information to be
 * commited to a persistant store.
 */
public class OTAContactData implements SyncObject, Persistable
{
    private int _uid;
    private String _first, _last, _email;
    
    /**
     * Default constructor
     */
    public OTAContactData()
    {       
    }
    
    
    /**
     * Creates a new OTAContactData object
     * @param uid The UID of the contact
     */
    public OTAContactData(int uid)
    {
        _uid = uid;
    }
    
    
    /**
     * Sets the UID
     * @param uid The UID to set
     */
    void setUID(int uid)
    {
        _uid = uid;
    }
    

    /**
     * @see SyncObject#getUID()     
     */
    public int getUID()
    {
        return _uid;
    }
    
    
    /**
     * Sets the first name of this contact
     * @param first The first name of this contact
     */
    void setFirst(String first)
    {
        _first = first;
    }
    
    
    /**
     * Gets the first name of this contact
     * @return The first name of this contact
     */
    String getFirst()
    {
        return _first;
    }
    
    
    /**
     * Sets the last name of this contact
     * @param last The last name of this contact
     */
    void setLast(String last)
    {
        _last = last;
    }
    
    
    /**
     * Gets the last name of this contact
     * @return The last name of this contact
     */
    String getLast()
    {
        return _last;
    }
    
    
    /**
     * Sets the contact's email address
     * @param email The contact's email address
     */
    void setEmail(String email)
    {
        _email = email;
    }
    
    
    /**
     * Gets the contact's email address
     * @return The contact's email address
     */
    String getEmail()
    {
        return _email;
    }
    
    
    /**
     * Determines equality by matching the first and last names
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object o)
    {
        if(o instanceof OTAContactData)
        {
            OTAContactData otherContact = (OTAContactData) o;          
            
            if (getFirst() == null) 
            {
                if (otherContact.getFirst() == null) 
                {
                    if (getLast() != null) 
                    {
                        return getLast().equals(otherContact.getLast());
                    } 
                    else 
                    {
                        return (otherContact.getLast() == null);
                    }                    
                } 
                
                return false;
            } 
            else if (getFirst().equals(otherContact.getFirst())) 
            {
                if (getLast() != null) 
                {
                    return getLast().equals(otherContact.getLast());
                } 
                else 
                {
                    return (otherContact.getLast() == null);
                }
            }
                     
        }
        
        return false;
    }
}    
