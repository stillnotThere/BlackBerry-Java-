/*
 * ComposeScreen.java
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
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.*;
import java.util.*;

/**
 * The ComposeScreen is a screen which displays either a new or saved message.
 * It adds the functionality of saving and sending messages to its parent class,
 * MessageScreen.
 */
public final class ComposeScreen extends MessageScreen
{    
    private static final int FIRST = 0;
    private static final int SEND_MENU_ITEM_INDEX = 0;

    private AddHeaderFieldAction _addToMenuItem;
    private AddHeaderFieldAction _addCcMenuItem;
    private AddHeaderFieldAction _addBccMenuItem;
    
    private Store _store;

    
    /**
     * Creates a new ComposeScreen object
     * @param message A message in the process of being composed, or null if a new message is to be composed
     * @param store The message store for this application
     */
    public ComposeScreen(Message message, Store store)
    {
        super(message, true);
        
        _store = store;

        // If a new message is to be created, indicate this in the title
        if( message == null )
        {
            setTitle("New Message");
        }

        // Create and add menu items specific to the Compose action (addTo,
        // addBcc, addCc, etc...).
        _addToMenuItem = new AddHeaderFieldAction(Message.RecipientType.TO, "Add To: ", "To: ");
        _addCcMenuItem = new AddHeaderFieldAction(Message.RecipientType.CC, "Add Cc: ", "Cc: ");
        _addBccMenuItem = new AddHeaderFieldAction(Message.RecipientType.BCC, "Add Bcc: ", "Bcc: ");

        addMenuItem(_sendMenuItem);
        addMenuItem(_saveMenuItem);
        addMenuItem(_addToMenuItem);
        addMenuItem(_addCcMenuItem);
        addMenuItem(_addBccMenuItem);
    }
    

    /**
     * MenuItem to send a message
     */
    private MenuItem _sendMenuItem = new MenuItem("Send", 110, 11)
    {
        public void run()
        {
            try
            {
                _message = getMessage();
                if( _message != null )
                {
                    // Send the message
                    Transport.send(_message); 

                    // Close the screen
                    close();
                }
            }
            catch( MessagingException e )
            {                
                BlackBerryMailDemo.errorDialog("Transport.send(Message) threw " + e.toString());
            }
        }
    };


    /**
     * MenuItem to save a message
     */
    private MenuItem _saveMenuItem = new MenuItem("Save Message", 110, 11)
    {
        public void run()
        {
            // If the save is completed, then discard this screen            
            if( onSave() )
            {
                close();
            }
            else
            // If the message could not be saved, alert the user
            {
                UiApplication.getUiApplication().invokeLater(new Runnable()
                {
                    public void run()
                    {
                        Dialog.alert("Message could not be saved");
                    }
                });
            }
        }
    };

    
    /**
     * Overrides MessageScreen.displayMessage(). The message's 'sent' properties
     * are not displayed since the message is still in the process of editing.
     */
    void displayMessage()
    {
        // If the message does not exist then compose a new message
        if( _message == null )
        {
            // Add a To line
            EditField toField = new EditField("To: ", "", 40, BasicEditField.FILTER_EMAIL);
            addTextFieldToTableAndScreen(toField, Message.RecipientType.TO);

            // Add a subject line
            EditField subjectField = new EditField("Subject: ", "");
            addTextFieldToTableAndScreen(subjectField, SUBJECT);

            // Add a separator between the body and the headers
            add(new SeparatorField());

            // Add a body field
            EditField bodyField = new EditField();
            addTextFieldToTableAndScreen(bodyField, BODY);
        }
        else
        // The message exists so display it
        {
            displayHeader();
            add(new SeparatorField());
            displayMessageBody();
        }
    }    

    
    /**
     * Gets a message for sending or saving
     * @return A new message
     */
    Message getMessage()
    {
        // Find an outbox folder and use it to construct a new message      
        Folder outbox = _store.findFolder("Outbox")[ FIRST ];
        Message message = new Message(outbox);

        // Add all the current headers
        for( int keyNo = 0; keyNo < HEADER_KEYS.length; keyNo++ )
        {
            Vector fieldsByType = (Vector) _fieldTable.get(HEADER_KEYS[ keyNo ]);

            if( fieldsByType != null )
            {
                // Build a vector of all the addresses
                Vector addressVector = new Vector();
                int size = fieldsByType.size();
                for( int fieldNo = 0; fieldNo < size; fieldNo++ )
                {
                    TextField addressField = (TextField) fieldsByType.elementAt(fieldNo);

                    // Try to create a new address object wrapping the email
                    // address and add it to the address vector.
                    try
                    {
                        addressVector.addElement(new Address(addressField.getText(), ""));
                    }
                    catch( AddressException e ) // Invalid address
                    {
                        BlackBerryMailDemo.errorDialog("Address(String, String) threw " + e.toString());
                    }
                }

                // Dump the vector of addresses into an array to send the message
                Address[] addresses = new Address[ addressVector.size() ];
                addressVector.copyInto(addresses);

                // Try to add the addresses to the message's list of recipients
                try
                {
                    message.addRecipients(HEADER_KEYS[ keyNo ], addresses);
                }
                catch( MessagingException e )
                {                    
                    BlackBerryMailDemo.errorDialog("Message#addRecipients(int, Address[]) threw " + e.toString());
                }
            }
        }

        // Add the subject
        Vector subjectFields = (Vector) _fieldTable.get(SUBJECT);
        TextField subjectField = (TextField) subjectFields.elementAt(FIRST);

        if( subjectFields != null && subjectFields.size() > 0 )
        {
            message.setSubject(subjectField.getText());
        }

        // Add the body by adding all the body fields into one multipart
        Vector bodyFields = (Vector) _fieldTable.get(BODY);
        if( bodyFields != null )
        {
            int size = bodyFields.size();
            Multipart content = new Multipart();
            for( int fieldNo = 0; fieldNo < size; fieldNo++ )
            {
                TextField body = (TextField) bodyFields.elementAt(fieldNo);
                content.addBodyPart(new TextBodyPart(content, body.getText()));
            }
            try
            {
                message.setContent(content);
            }
            catch( MessagingException e )
            {                
                BlackBerryMailDemo.errorDialog("Message#setContent(Object) threw " + e.toString());
            }
        }
        else
        {
            BlackBerryMailDemo.errorDialog("Error: no body field available");
            return null;
        }

        // Set the date
        message.setSentDate(Calendar.getInstance().getTime());

        return message;
    }

    
    /**
     * @see net.rim.device.api.ui.Screen#onSave()
     */
    protected boolean onSave()
    {
        // Save the message to the outbox
        try
        {
            Message newMessage = getMessage();
            if( newMessage != null )
            {
                // Retrieve an outbox to save the message in
                Store store = Session.waitForDefaultSession().getStore();
                Folder[] allOutboxFolders = store.list(Folder.OUTBOX);

                Folder outbox = null;                
                for( int i = allOutboxFolders.length - 1; i >= 0 ; --i )
                {
                    if( allOutboxFolders[ i ].getParent().getName().startsWith("Mailbox") )
                    {
                        outbox = allOutboxFolders[ i ];
                        break;
                    }
                }

                // Save the new message and replace the old one if it exists
                outbox.appendMessage(newMessage);
                if( _message != null )
                {
                    outbox.deleteMessage(_message, true);
                }
                _message = newMessage;

                // Set the status to composing and flag that it has been saved
                _message.setStatus(Message.Status.TX_COMPOSING, Message.Status.TX_ERROR);
                _message.setFlag(Message.Flag.SAVED, true);

                return true;
            }

            return false;
        }
        catch( MessagingException e )
        {
            return false;
        }
    }

    
    /**
     * Make "Send" the default menu item.
     * 
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
     */
    protected void makeMenu(Menu menu, int instance)
    {
        super.makeMenu(menu, instance);

        menu.setDefault(SEND_MENU_ITEM_INDEX);
    }

    
    /**
     * This class is responsible for adding the various header fields to the
     * compose screen (To, Bcc, CC).
     */
    private final class AddHeaderFieldAction extends MenuItem
    {
        private String _fieldLabelText;
        private int _headerType;

        /**
         * Constructs a menu item which adds a header of a specified type to the
         * compose screen.
         * 
         * @param headerType One of the Message.RecipientType fields
         * @param menuItemText String to use for the menu item
         * @param fieldLabelText String to use for the label of this field
         */
        AddHeaderFieldAction(int headerType, String menuItemText, String fieldLabelText)
        {
            super(menuItemText, 100000, 10);

            _fieldLabelText = fieldLabelText;
            _headerType = headerType;
        }

        /**
         * Adds a new header field to the message. The field is placed so that
         * the header types are grouped together and the most recently added one
         * is closest to the bottom.         
         */
        public void run()
        {
            EditField newField = new EditField(_fieldLabelText, "");

            // Find out where the last field of this type was added to the
            // screen.
            Vector fieldsByType = (Vector) _fieldTable.get(_headerType);
            int lastInsertedIndex;
            if( fieldsByType == null )
            {
                // If a field of _headerType was not made yet, then create the
                // vector which contains all of the fields of _headerType.
                fieldsByType = new Vector();
                _fieldTable.put(_headerType, fieldsByType);
                lastInsertedIndex = getIndexForNewFieldType();
            }
            else
            {
                lastInsertedIndex = getIndexOfLastFieldOfType(_headerType);
            }

            // Add the new field to both the screen and the vector keeping track
            // of all the fields of the same type.
            ComposeScreen.this.insert(newField, lastInsertedIndex + 1);
            fieldsByType.addElement(newField);
            newField.setFocus();
        }

        
        /**
         * Given the existing field type of this instance of the class,
         * determine where to place new header fields in the screen.
         * 
         * @return The index at which to insert the new header field
         */
        private int getIndexForNewFieldType()
        {
            // Note: we don't handle TO here since there ALWAYS must be one TO
            // field.
            switch( _headerType )
            {
                // Find the last TO field and use the next index as the
                // insertion point.
                case Message.RecipientType.CC:
                    return getIndexOfLastFieldOfType(Message.RecipientType.TO);

                    // Try to find the last CC field and use it as the next
                    // insertion point. If no CC field exists then find the last
                    // TO field and use its index as the insertion point.
                case Message.RecipientType.BCC:
                    int index = getIndexOfLastFieldOfType(Message.RecipientType.CC);
                    if( index == -1 )
                        return getIndexOfLastFieldOfType(Message.RecipientType.TO);
                    return index;

                default:
                    throw new IllegalStateException("Mail Demo: Unrecognized recipient type");
            }
        }

        
        /**
         * Retrieves the index of the last field added of a specified type.
         * 
         * @param type The type of header field to retrieve the last index of
         * @return The index of the most recently added field of the specified 
         *         type, -1 if a field of the specified type has not been added
         *         yet.
         */
        private int getIndexOfLastFieldOfType(int type)
        {
            Vector fields = (Vector) _fieldTable.get(type);
            if( fields == null )
            {
                return -1;
            }

            Field field = (Field) fields.lastElement();

            return field.getIndex();
        }
    }
}
