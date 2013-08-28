/*
 * MessagesViewScreen.java
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

package com.rim.samples.device.blackberrymaildemo;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.util.*;

import net.rim.blackberry.api.mail.*;
import net.rim.blackberry.api.mail.event.*;

/**
 * This class allows the user to view all of the messages. It uses the
 * MessagesListField class to display the messages. The messages are displayed 
 * in reverse chronological order. This class also implements a FolderListener 
 * to listen for any events in which messages are changed, and a FolderListener 
 * to listen for events in which messages are added or removed from a store 
 * folder. In addition to the menu options given by the parent class 
 * BlackBerryMailDemoScreen, this class allows the user to delete messages.
 */
public final class MessagesViewScreen extends BlackBerryMailDemoScreen implements FolderListener, MessageListener
{    
    private SimpleSortingVector _messages; 
    private UiApplication _uiApplication;
    
    /**
     * Creates a new MessagesViewScreen object
     */
    public MessagesViewScreen() 
    {
        setTitle("Messages View Screen");
        
        // Add this object as the folder listener for the store email service
        _store.addFolderListener(this);
        
        // Initialize the simple sorting vector to sort manually according to
        // most recent date.
        _messages = new SimpleSortingVector(); 
        _messages.setSortComparator(Util.SORT_BY_MOST_RECENT_DATE);
        _messages.setSort(false);
        
        // Fill the message vector then sort it
        populateList(_store.list(Folder.SUBTREE));   
        _messages.reSort();
        
        // Add the list field to display the messages
        _listField = new MessagesListField(_messages);
        add(_listField); 
        
        
        // Store the UiApplication for use in refreshing the message list field
        _uiApplication = UiApplication.getUiApplication();
        
        _changeViewMenuItem = new MenuItem("Folders View", 110, 10)
        {
            public void run()
            {
                _currentDisplayMode = FOLDERS_VIEW_MODE;
                
                Folder currentFolder = ((Message)getSelectedItem()).getFolder();
                                
                FoldersViewScreen foldersViewScreen = new FoldersViewScreen(currentFolder);
                UiApplication.getUiApplication().pushScreen(foldersViewScreen);
            }
        };      
    }
    
    
    /**
     * @see com.rim.samples.device.blackberrymaildemo.BlackBerryMailDemoScreen#makeMenu(Menu, int)
     */
    protected void makeMenu(Menu menu, int instance)
    {
        if(!_listField.isEmpty())
        {
            menu.add(_deleteMenuItem);
        }
        super.makeMenu(menu, instance);
    }
    
    
    /**
     * Refreshes the message store, messages, and the screen when the
     * message service is changed. 
     * 
     * @see com.rim.samples.device.blackberrymaildemo.BlackBerryMailDemoScreen#messageServiceChanged()
     */
    protected void messageServiceChanged()
    {
        _store.addFolderListener(this);
        _messages.removeAllElements();
        populateList(_store.list());   
        updateScreen();
    }  
    
    
    /**
     * Fills the list of messages with the messages stored in the specified 
     * folders, including their subfolders.
     *      
     * @param folders The folders to populate the list with
     */
    private void populateList(Folder[] folders)
    {
        for (int folderIndex = 0; folderIndex < folders.length; folderIndex++)
        {                  
            // Populate the list with the subfolders            
            Folder[] subfolders = folders[folderIndex].list();
            populateList(subfolders);            
        
            // Search the current folder for the message to delete
            Message[] messages;
            try
            {
                messages = folders[folderIndex].getMessages();
            }
            catch (MessagingException e)
            {
                BlackBerryMailDemo.errorDialog("Folder#getMessages() threw " + e.toString());
                return;
            }               
            
            // Populate the list with the messages in the current folder and 
            // add a message listener to them.
            for (int messageIndex = 0; messageIndex < messages.length; messageIndex++)
            {                   
                Message message = messages[messageIndex];
                message.addMessageListener(this);
                _messages.addElement(message);                                              
            }                   
        }       
    }
    
    
    /**
     * @see com.rim.samples.device.blackberrymaildemo.BlackBerryMailDemoScreen#updateScreen()
     */
    protected void updateScreen()
    {               
        // Update the screen when the application is free
        _uiApplication.invokeLater(new Runnable() 
        {
            public void run()
            {
                // Re-sort by most recent date and resize the list field
                _messages.reSort();                           
                _listField.setSize(_messages.size());           
            }
        });     
    }   
    
    
    /**
     * Gets the selected message
     *
     * @see com.rim.samples.device.blackberrymaildemo.BlackBerryMailDemoScreen#getSelectedItem()
     */
    protected Object getSelectedItem()
    {
        Object obj = null;
        
        int index = _listField.getSelectedIndex();
        if(index > -1)
        {
            obj = _messages.elementAt(index);
        }
                
        return obj;
    }
    
    
    /**
     * Opens the selected message
     *
     * @see com.rim.samples.device.blackberrymaildemo.BlackBerryMailDemoScreen#openAction()
     */
    protected void openAction()
    {
        Message selectedMessage = (Message) getSelectedItem();
        if(selectedMessage != null)
        {            
            openMessage(selectedMessage);
        }
    }
    
    
    /**
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close()
    {
        // De-register listeners before closing
        _store.removeFolderListener(this);            
        
        for(int index = _messages.size() - 1; index >= 0; index--)
        {
            Message msg = (Message) _messages.elementAt(index);
            msg.removeMessageListener(this);
        }        
                
        super.close();                      
    }    
    
    
    /**
     * MenuItem to delete a message
     */
    private MenuItem _deleteMenuItem = new MenuItem( "Delete Message" , 110, 14) 
    {
        public void run()
        {
            int choice = Dialog.ask(Dialog.D_YES_NO, "Delete message?", Dialog.YES);
            if(choice == Dialog.YES)
            {                  
                Message message = (Message) getSelectedItem();
                Folder folder = message.getFolder();
                folder.deleteMessage(message);                
                updateScreen(); 
            }
        }
    };      
    
    
    ////////////////////////////////////////////////////////////////////////////
    //  *********************  Message Listener  ***************************  //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * @see net.rim.blackberry.api.mail.event.MessageListener#changed(MessageEvent)
     */
    
    public void changed(MessageEvent e)
    {
        // Get the selected message to update screen at the message's row
        Message message = e.getMessage();        
        _listField.invalidate(_messages.indexOf(message));
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    //  **********************  Folder Listener  ***************************  //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * @see net.rim.blackberry.api.mail.event.FolderListener#messagesAdded(FolderEvent)
     */
    public void messagesAdded(FolderEvent e)
    {
        Message message = e.getMessage();      
        
        message.addMessageListener(this);    
        
        // Insert the message into the vector of messages, preserving
        // the sorted order.
        int indexToInsert = _messages.find(message);
        if (indexToInsert < 0)
        {
            indexToInsert = -indexToInsert - 1;
        }
        _messages.insertElementAt(message, indexToInsert);
        
        updateScreen();
    }
        
    
    
    /**
     * @see net.rim.blackberry.api.mail.event.FolderListener#messagesRemoved(FolderEvent)
     */
    public void messagesRemoved(FolderEvent e)
    {               
        Message msg = e.getMessage();        
        
        // Remove the listeners and delete the message from the list 
        msg.removeMessageListener(this);
        _messages.removeElement(msg);        
        
        updateScreen();         
    }
}

