/*
 * OTAContactCollection.java
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

import java.io.*;
import java.util.*;
import net.rim.device.api.collection.*;
import net.rim.device.api.synchronization.*;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.*;
import net.rim.device.api.system.*;
import net.rim.device.api.i18n.Locale;

/**
 * A Collection that is completely synchable over the air.
 */
public class OTAContactCollection implements OTASyncCapable, OTASyncPriorityProvider,
      OTASyncParametersProvider, SyncConverter, SyncCollection, CollectionEventSource
{

    private static final long PERSISTENT_KEY = 0xb7abba0f9ef77e29L; // Hash of com.rim.samples.device.otasyncdemo
    private static final long COLLECTION_KEY = 0x8781a172fb82e0caL; // Hash of com.rim.samples.device.otasyncdemo.OTAContactCollection

    private static final int FIELDTAG_FIRST_NAME = 1;
    private static final int FIELDTAG_LAST_NAME = 2;
    private static final int FIELDTAG_EMAIL_ADDRESS = 3;
    private static final int FIELDTAG_PHONE_NUMBER = 4;

    private Vector _contacts;           // The collection container.
    private PersistentObject _persist;  // Persistent object for the collection.
    private Vector _listeners;          // Listeners to notify when the collection changes.

    /**
     * Constructor
     */
    public OTAContactCollection()
    {
        _persist = PersistentStore.getPersistentObject( PERSISTENT_KEY );
        _contacts = (Vector)_persist.getContents();
        
        if( _contacts == null )
        {
            _contacts = new Vector();
            _persist.setContents( _contacts );
            _persist.commit();
        }
        
        _listeners = new CloneableVector();
    }

    /**
     * Returns an instance of this Collection.
     */
    static OTAContactCollection getInstance()
    {
        RuntimeStore rs = RuntimeStore.getRuntimeStore();
        
        synchronized( rs )
        {
            OTAContactCollection collection = (OTAContactCollection)rs.get( COLLECTION_KEY );
            
            if( collection == null )
            {
                collection = new OTAContactCollection();
                rs.put( COLLECTION_KEY, collection );
            }
            
            return collection;
        }
    }

    // SyncCollection methods ----------------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#addSyncObject(SyncObject)
     */
    public boolean addSyncObject(SyncObject object)
    {
        // Add a contact to the persistent store.
        _contacts.addElement(object);
        _persist.setContents( _contacts );
        _persist.commit();

        // We want to let any collection listeners we have that the collection has been changed.
        int numListeners = _listeners.size();
        
        for( int i=0; i<numListeners; i++ )
        {
            CollectionListener cl = (CollectionListener)_listeners.elementAt( i );
            cl.elementAdded( this, object );
        }
        
        return true;
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#updateSyncObject(SyncObject,SyncObject)
     */
    public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject)
    {
        // Remove an object.
        if( _contacts.contains( oldObject ) )
        {
            int index = _contacts.indexOf( oldObject );
            _contacts.setElementAt( newObject, index );
        }

        // Persist
        _persist.setContents( _contacts );
        _persist.commit();

        // Notify listeners.
        int numListeners = _listeners.size();
        
        for( int i=0; i<numListeners; i++ )
        {
            CollectionListener cl = (CollectionListener)_listeners.elementAt( i );
            cl.elementUpdated( this, oldObject, newObject );
        }
        
        return true;
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#removeSyncObject(SyncObject)
     */
    public boolean removeSyncObject(SyncObject object)
    {
        // Remove the object.
        if( _contacts.contains( object ) )
        {
            _contacts.removeElement( object );
        }

        // Persist
        _persist.setContents( _contacts );
        _persist.commit();

        // Notify listeners.
        int numListeners = _listeners.size();
        
        for( int i=0; i<numListeners; i++ )
        {
            CollectionListener cl = (CollectionListener)_listeners.elementAt( i );
            cl.elementRemoved( this, object );
        }
        
        return true;
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#removeAllSyncObjects()
     */
    public boolean removeAllSyncObjects()
    {
        // Clear the Vector.
        _contacts.removeAllElements();
        _persist.setContents( _contacts );
        _persist.commit();

        // Notify listeners.
        int numListeners = _listeners.size();
        
        for( int i=0; i<numListeners; i++ )
        {
            CollectionListener cl = (CollectionListener)_listeners.elementAt( i );
            cl.reset( this );
        }
        
        return true;
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
            
            if ( so.getUID() == uid ) 
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
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncVersion()
     */
    public int getSyncVersion()
    {
        return 0;
    }
    
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncObjectCount()
     */
    public int getSyncObjectCount()
    {
        return _contacts.size();
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncName()
     */
    public String getSyncName()
    {
        return "Sample Contacts";
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
        _persist = PersistentStore.getPersistentObject( PERSISTENT_KEY );
        _contacts = (Vector)_persist.getContents();
    }
    
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#endTransaction()
     */
    public void endTransaction()
    {
        _persist.setContents( _contacts );
        _persist.commit();
    }

    // OTASyncPriorityProvider methods ------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.OTASyncPriorityProvider#getSyncPriority()
     */
    public int getSyncPriority()
    {
        return OTASyncPriorityProvider.MIN_PRIORITY - 1;
    }

    // SyncConverter methods ----------------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.SyncConverter#convert(SyncObject,DataBuffer,int)
     */
    public boolean convert(SyncObject object, DataBuffer buffer, int version)
    {
        if (version == getSyncVersion())
        {
            if (object instanceof OTAContactData)
            {
                String first = ((OTAContactData)object).getFirst();
                String last = ((OTAContactData)object).getLast();
                String email = ((OTAContactData)object).getEmail();
                String phone = ((OTAContactData)object).getPhone();

                // In compliance with desktop sync format.
                buffer.writeShort(first.length()+1);
                buffer.writeByte(FIELDTAG_FIRST_NAME);
                buffer.write(first.getBytes());
                buffer.writeByte(0);
                buffer.writeShort(last.length()+1);
                buffer.writeByte(FIELDTAG_LAST_NAME);
                buffer.write(last.getBytes());
                buffer.writeByte(0);
                buffer.writeShort(email.length()+1);
                buffer.writeByte(FIELDTAG_EMAIL_ADDRESS);
                buffer.write(email.getBytes());
                buffer.writeByte(0);
                buffer.writeShort(phone.length()+1);
                buffer.writeByte(FIELDTAG_PHONE_NUMBER);
                buffer.write(phone.getBytes());
                buffer.writeByte(0);

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
        try 
        {
            OTAContactData contact = new OTAContactData(UID);
            
            while(data.available() > 0)
            {
                int length = data.readShort();
                byte[] bytes = new byte[length];
                
                switch (data.readByte())
                {
                    case FIELDTAG_FIRST_NAME:
                        data.readFully(bytes);
                        // Trim null-terminator.
                        String fname = new String(bytes).trim();
                        
                        if( fname != null ) 
                        {
                            contact.setFirst( fname );
                        } 
                        else 
                        {
                            contact.setFirst( "" );
                        }
                        break;
                        
                    case FIELDTAG_LAST_NAME:
                        data.readFully(bytes);
                        String lname = new String(bytes).trim();
                        
                        if( lname != null ) 
                        {
                            contact.setLast( lname );
                        } 
                        else 
                        {
                            contact.setLast( "" );
                        }
                        break;
                        
                    case FIELDTAG_EMAIL_ADDRESS:
                        data.readFully(bytes);
                        String email = new String(bytes).trim();
                        
                        if( email != null ) 
                        {
                            contact.setEmail( email );
                        } 
                        else 
                        {
                            contact.setEmail( "" );
                        }
                        break;
                        
                    case FIELDTAG_PHONE_NUMBER:
                        data.readFully(bytes);
                        String phone = new String(bytes).trim();
                        
                        if( phone != null ) 
                        {
                            contact.setPhone( phone );
                        } 
                        else 
                        {
                            contact.setPhone( "" );
                        }
                        break;
                        
                    default:
                        data.readFully(bytes);
                        break;
                }
            }
            
            return contact;
        }
        catch (final EOFException e)
        {
            UiApplication.getUiApplication().invokeLater(new Runnable()
            {
                public void run()
                {
                    Dialog.alert(e.toString());
                } 
            });
        }
        
        return null;
    }
    
    // CollectionEventSource methods --------------------------------------------
    /**
     * @see net.rim.device.api.collection.CollectionEventSource#addCollectionListener(Object)
     */
    public void addCollectionListener(Object listener)
    {
        _listeners = ListenerUtilities.fastAddListener( _listeners, listener );
    }
    
    /**
     * @see net.rim.device.api.collection.CollectionEventSource#removeCollectionListener(Object)
     */
    public void removeCollectionListener(Object listener)
    {
        _listeners = ListenerUtilities.removeListener( _listeners, listener );
    }

    // OTASyncParametersProvider methods ----------------------------------------
    /**
     * @see net.rim.device.api.synchronization.OTASyncParametersProvider#getUserSystemId()
     */
    public String getUserSystemId()
    {
        return null;
    }
    
    /**
     * @see net.rim.device.api.synchronization.OTASyncParametersProvider#getDataSourceName()
     */
    public String getDataSourceName()
    {
        return "Sample";
    }
    
    /**
     * @see net.rim.device.api.synchronization.OTASyncParametersProvider#getDatabaseName()
     */
    public String getDatabaseName()
    {
        return "Sample Contacts";
    }

    // OTASyncCapable methods ---------------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.OTASyncCapable#getSchema()
     */
    public SyncCollectionSchema getSchema()
    {
        return null;
    }

    // Class-specific methods ---------------------------------------------------
    /**
     * Returns the contact from the contact list at a specific index.
     * @param index The index of the contact to retrieve
     * @return The contact from the list at the specific index
     */
    Object getAt( int index )
    {
        return _contacts.elementAt( index );
    }
}
