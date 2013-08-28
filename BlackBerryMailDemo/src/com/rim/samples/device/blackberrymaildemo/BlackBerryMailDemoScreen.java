/*
 * BlackBerryMailDemoScreen.java
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

import net.rim.blackberry.api.mail.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.servicebook.*;
import net.rim.device.api.system.*;

/**
 * The parent class for MessagesListScreen and FoldersViewScreen. This class 
 * implements a menu system that allows the user to view/edit messages, switch 
 * mailing services if available, change message views, and compose messages.
 * 
 * This class is to be subclassed by the MessageViewScreen and FolderViewScreen
 * classes. The major feature inherited by these two classes are the menu items
 * common to both view screens. This class contains four methods which its 
 * subclasses may implement to provide proper menu functionality. These methods 
 * act as listeners and are called through menu or keypad actions. They are: 
 * openAction(), updateScreen(), getSelectedItem() and messageServiceChanged().
 * 
 * This class also contains a constructor to initialize the application by 
 * setting up the messaging service store accessed by this demo.
 */
public class BlackBerryMailDemoScreen extends MainScreen
{
    protected static final int MESSAGES_VIEW_MODE = 0;
    protected static final int FOLDERS_VIEW_MODE = 1;

    protected static int _currentDisplayMode;
    protected static Store _store;

    private static ServiceRecord[] _mailServiceRecords;
    private MessagesViewScreen _messagesViewScreen;

    protected MenuItem _changeViewMenuItem;
    protected ListField _listField;

    
    /**
     * Default Constructor for subclasses
     */
    protected BlackBerryMailDemoScreen()
    {}

    
    /**
     * Constructor to initialize the application
     * @param title Screen title
     */
    public BlackBerryMailDemoScreen(String title)
    {
        setTitle(title);
        _currentDisplayMode = MESSAGES_VIEW_MODE;
        
        UiApplication.getApplication().invokeLater(new Runnable()
        {
            public void run()
            {
                initialize();
            }
        });
    }
    

    /**
     * Initializes the store
     */     
    private void initialize()
    {
        // Open the service book and get the mail service records
        ServiceBook serviceBook = ServiceBook.getSB();       
        _mailServiceRecords = serviceBook.findRecordsByCid("CMIME");
        
        // Determine if there is more than one CMIME (email) service book        
        if( _mailServiceRecords.length > 1 )
        {
            selectServiceDialog();
        }
        else if( _mailServiceRecords.length == 1 )
        {
            // There is only one message service, so just work with that.
            // Get the default mail store.
            _store = Session.getDefaultInstance().getStore();
        }
        else
        {
            // No email message services were found. We have nothing to display
            Dialog.alert("No email message services found");
            close();
        }

        // Open the messages view using the message store
        _messagesViewScreen = new MessagesViewScreen();
        UiApplication.getUiApplication().pushScreen(_messagesViewScreen);
        close();
    }
    

    /**
     * Spawns a dialog to allow a user to choose a specific mail service
     */
    private void selectServiceDialog()
    {
        // Get the name of the ServiceRecords
        String[] names = new String[ _mailServiceRecords.length ];
        for( int count = _mailServiceRecords.length - 1; count >= 0; --count )
        {
            names[ count ] = _mailServiceRecords[ count ].getName();
        }

        // Create a Dialog to ask the user to choose a mailbox to open
        int choice = Dialog.ask("Select a Mailbox", names, 0);

        // If the user pressed escape, close the program
        if( choice == Dialog.CANCEL )
        {
            Dialog.alert("Goodbye");
            close();
        }

        // Open the ServiceConfiguration
        ServiceConfiguration sc = new ServiceConfiguration(_mailServiceRecords[ choice ]);

        // Get the mail store        
        _store = Session.getInstance(sc).getStore();
    }
    

    /**
     * Handles opening the selected item. To be overidden by subclasses.
     */
    protected void openAction() {} 

    
    /**
     * Updates the screen. To be overiden by subclasses.
     */
    protected void updateScreen() {} 

    
    /**
     * Gets the selected item in the list field. To be overiden by  
     * subclasses.
     * 
     * @return The selected item
     */
    protected Object getSelectedItem() { return null; } 
    
    
    /**
     * A listener called when the message service is changed. To be overiden by
     * subclasses.
     */
    protected void messageServiceChanged() {}

    
    /**
     * Displays the message to edit if it is in the process of being composed.
     * If the message is not being composed then this method opens a read only 
     * view of the message.
     * 
     * @param message The message to view/edit
     */
    protected void openMessage(Message message)
    {
        // If the message is still in the process of being composed, then open 
        // the composing screen. If not, then open the message in a read only
        // screen.
        MessageScreen messageScreen;
        if( message.getStatus() == Message.Status.TX_COMPOSING )
        {
            messageScreen = new ComposeScreen(message, _store);
        }        
        else
        {
            messageScreen = new MessageScreen(message, false);
        }

        // Show the message screen
        UiApplication.getUiApplication().pushScreen(messageScreen);
        updateScreen();
    }
    
    /**
     * Menu item to push a ComposeScreen
     */
    private MenuItem _composeMenuItem = new MenuItem("Compose Email", 110, 11)
    {
        public void run()
        {
            ComposeScreen composeScreen = new ComposeScreen(null, _store);
            UiApplication.getUiApplication().pushScreen(composeScreen);
            updateScreen();
        }
    };

    
    /**
     * Menu item to open folder
     */
    private MenuItem _openMenuItem = new MenuItem("Open Item", 110, 10)
    {
        public void run()
        {
            openAction();
        }
    };

    
    /**
     * Menu item to open a message for editing
     */
    private MenuItem _editMenuItem = new MenuItem("Edit", 110, 12)
    {
        public void run()
        {
            Message msg = (Message) getSelectedItem();
            MessageScreen messageScreen = new MessageScreen(msg, true);
            UiApplication.getUiApplication().pushScreen(messageScreen);
            updateScreen();
        }
    };

    
    /**
     * Menu item allows user to choose message service
     */
    private MenuItem _selectServiceMenuItem = new MenuItem("Choose Message Service", 110, 10)
    {
        public void run()
        {
            if( _currentDisplayMode == MESSAGES_VIEW_MODE )
            {
                _store.removeFolderListener(_messagesViewScreen);
            }

            selectServiceDialog();
            messageServiceChanged();
        }
    };

    
    /**
     * @see net.rim.device.api.ui.Screen#makeMenu(Menu, int)
     */
    protected void makeMenu(Menu menu, int instance)
    {
        // Have the compose menu item on top
        menu.add(_composeMenuItem);
        menu.addSeparator();

        // If there are messages in the list, add the menu item for viewing 
        // different messages.
        if( !_listField.isEmpty() )
        {
            // Allow the user to change the view modes
            menu.add(_changeViewMenuItem);

            // If the selected item is a message then allow the user to open it
            Object obj = getSelectedItem();
            if( obj instanceof Message )
            {
                Message msg = (Message) obj;

                // If the message is a saved message allow the user to edit it
                if( msg.getStatus() == Message.Status.TX_COMPOSING )
                {
                    menu.add(_editMenuItem);
                }
                else // Allow the user to view the message                
                {
                    menu.add(_openMenuItem);
                }
            }
            else // Allow the user to open the non-message object            
            {
                menu.add(_openMenuItem);
            }

            menu.addSeparator();
        }

        // Add a menu item to select the message service if there is more than
        // one CMIME service book.
        if( _mailServiceRecords != null )
        {
            menu.add(_selectServiceMenuItem);
        }

        super.makeMenu(menu, instance);
    }

   
    /**
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    protected boolean invokeAction(int action)
    {
        switch( action )
        {
            case ACTION_INVOKE: // Trackball click
            {
                openAction();
                return true;
            }
        }

        return super.invokeAction(action);
    }

    
    /**
     * Overrides default. Enter key will display the message.
     * 
     * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
     */
    public boolean keyChar(char c, int status, int time)
    {
        switch( c )
        {
            case Characters.ENTER:
                openAction();
                return true;

            default:
                return super.keyChar(c, status, time);
        }
    }

   
    /**
     * Prevent the save dialog from being displayed.
     * 
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt()
    {
        return true;
    }
}
