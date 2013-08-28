/*
 * SQLiteDemoScreen.java
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

package com.rim.samples.device.sqlitedemo;

import java.util.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.util.*;  


/**
 * The main screen for the SQLiteDemo sample application
 */
public final class SQLiteDemoScreen extends MainScreen implements TreeFieldCallback    
{   
    private TreeField _treeField;        
    private IntHashtable _categoriesHashtable;
    private Vector _directoryItems;
    private SQLManager _sqlManager;     
    
    
    /**
     * Constructs a new SQLiteDemoScreen
     * @param sqlManager A sqlManager instance used to perform database operations
     */    
    public SQLiteDemoScreen(SQLManager sqlManager)    
    {
        super(Screen.DEFAULT_CLOSE);
                
        _sqlManager = sqlManager;       
        
        // Initialize UI components
        setTitle("SQLite Demo");
        _treeField = new TreeField(this, Field.FOCUSABLE);       
        _treeField.setDefaultExpanded( false );
        add( _treeField );      
        
        // Populate the tree field and vectors with categories and items
        populateCategories();        
        populateItems();                   
    } 
    
    
    /**
     * Obtains the category hash table from the SQLManager and adds a node
     * for each category to the tree field.
     */
    private void populateCategories()
    {        
        _categoriesHashtable = _sqlManager.getCategories();    
        
        IntEnumeration enumeration = _categoriesHashtable.keys();        
        
        int key;
        Category category;
        int categoryNode;
        while(enumeration.hasMoreElements())
        {
            key = enumeration.nextElement();
            category = (Category)_categoriesHashtable.get(key);                                
            categoryNode = _treeField.addChildNode(0, category);                
            category.setNode(categoryNode);              
        }       
    }
    
    /**
     * Obtains the DirectoryItems vector and adds a node for each item to the
     * respective category node in the tree field.
     */
    private void populateItems()
    {   
        _directoryItems = _sqlManager.getItems();   
        
        DirectoryItem directoryItem;
        Category category;
        int categoryNode;
        int itemNode;
        
        // For each directory item, obtain the corresponding Category object from
        // the hash table, then add a child node to the respective category node
        // in the tree field.
        for(int i = _directoryItems.size() - 1; i >= 0; --i)
        {
            directoryItem = (DirectoryItem)_directoryItems.elementAt(i); 
            category = (Category)_categoriesHashtable.get(directoryItem.getCategoryId());
            categoryNode = category.getNode();
            itemNode = _treeField.addChildNode(categoryNode, directoryItem.getName()); 
            directoryItem.setNode(itemNode);                                                              
        }                  
    } 
     
     
    /**
     * Pushes a modal screen to display an existing directory item's details
     * @param currentNode The currently highlighted node in the tree field
     */       
    private void displayItem(int currentNode)
    {     
        DirectoryItem item;
        
        // Loop through the DirectoryItems vector until the object is found which
        // corresponds to the currently highlighted node.
        for(int i = _directoryItems.size() -1; i >= 0; --i)
        {
            item = (DirectoryItem)_directoryItems.elementAt(i);
            if(item.getNode() == currentNode)
            {
                DirectoryItem itemCopy = new DirectoryItem(item);
                            
                UiApplication app = UiApplication.getUiApplication();
                app.pushModalScreen(new ItemScreen(item, _sqlManager, false));      
                
                // Check whether the item was changed by the user
                if(!itemCopy.equals(item))                
                {
                    _treeField.setCookie(currentNode, item.getName());  // Item was edited, update the cookie for the current node                   
                }
                break;            
            }
        }       
    }
       
    /**
     * @see Screen#keyChar(char, int, int)      
     */
    protected boolean keyChar(char key, int status, int time)
    {
         // Intercept the ENTER key. 
        if (key == Characters.ENTER)
        {
            int currentNode = _treeField.getCurrentNode();
        
            if(_treeField.getCookie(currentNode) instanceof String)
            {
                displayItem(currentNode);  
            }
            return true;          
        }   
        
        return super.keyChar(key, status, time);
    }
    
    
    /**
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    protected boolean invokeAction(int action)
    {        
        if(action == ACTION_INVOKE) // Trackball click
        {
            int currentNode = _treeField.getCurrentNode();
    
            if(_treeField.getCookie(currentNode) instanceof String)
            {
                displayItem(currentNode);  
            }                
            return true; // We've consumed the event        
        }    
        return  super.invokeAction(action);
    }  
    
    /**
     * @see Screen#onClose()
     */
    public boolean onClose()
    {      
        _sqlManager.closeDB();
        
        return super.onClose();
    }
    
    /**
     * @see TreeFieldCallback#drawTreeItem(TreeField, Graphics, int, int, int, int)      
     */
    public void drawTreeItem(TreeField treeField, Graphics graphics, int node, int y, int width, int indent) 
    {
        if( treeField == _treeField ) 
        {        
            Object cookie = _treeField.getCookie(node);           
                    
            if(cookie instanceof String) 
            {
                String text = (String)cookie;
                graphics.drawText(text, indent, y, Graphics.ELLIPSIS, width);
            }
            
            if(cookie instanceof Category) 
            {
                Category category = (Category)cookie;
                String text = category.getName();
                graphics.drawText(text, indent, y, Graphics.ELLIPSIS, width);
            }
        }        
    }
    
    
    /**
     * @see MainScreen#makeMenu(Menu, int) 
     */
    protected void makeMenu(Menu menu, int context)
    {       
        // Get the cookie for the current node
        int currentNode = _treeField.getCurrentNode();      
        
        if(currentNode >= 0)
        {
            Object cookie = _treeField.getCookie(currentNode);
            
            if(cookie instanceof Category)
            {
                // Currently highlighted node is a category node, allow user to add
                // an item to or delete the category.
                Category category = (Category)cookie;        
                menu.add(new AddItem(category.getId(), category.getName(), currentNode));  
                MenuItem separator = MenuItem.separator(0);
                menu.add(separator);
                menu.add(new DeleteCategory(category));
                
                // Add menu item to add a new category
                menu.add(new AddCategory());                     
            }
            else if(cookie instanceof String)
            {
                // Currently highlighted node is an item node, allow user to add
                // an item to the parent category or delete the highlighted item.
                int parentNode = _treeField.getParent(currentNode);
                Object parentCookie = _treeField.getCookie(parentNode);
                Category parentCategory = (Category)parentCookie;   
                menu.add(new OpenItem());     
                menu.add(new AddItem(parentCategory.getId(), parentCategory.getName(), parentNode));              
                menu.add(new DeleteItem(currentNode));
                
                // Add menu item to add a new category
                MenuItem addCategory = new AddCategory();
                addCategory.setOrdinal(0x10000); // Inserts separator 
                menu.add(addCategory);                                     
            }            
        } 
        else
        {
            // Tree field is empty, start by allowing the addition of a new category
            menu.add(new AddCategory());        
        }        
        
        super.makeMenu(menu, context);
    }    
    
    
    /**
     * A MenuItem class to add a new directory item to an existing category 
     */
    private final class AddItem extends MenuItem
    {        
        private int _categoryId;
        private String _categoryName;
        private int _categoryNode;
        
        /**
         * Constructs a MenuItem object to add a new directory item to an existing category 
         * @param categoryId The ID for the category to which the directory item will be added to
         * @param categoryName The name of the category to which the directory item will be added to
         * @param categoryNode The currently highlighted node, corresponding to the category to which the directory item will be added to
         */
        private AddItem(int categoryId, String categoryName, int categoryNode)        
        {
            super("", 0, 0);
            _categoryId = categoryId;
            _categoryName  = categoryName;
            _categoryNode = categoryNode;            
        }        
        
        /**
         * Runs when the menu item is invoked by an end user.  Pushes a modal
         * screen which displays a new, blank directory item.
         */
        public void run()
        {       
            SQLiteDemo app = (SQLiteDemo)UiApplication.getUiApplication();  
            DirectoryItem item = new DirectoryItem(_categoryId);
            DirectoryItem itemCopy = new DirectoryItem(item);                                             
            app.pushModalScreen(new ItemScreen(item, _sqlManager, true));  
            
            if(!itemCopy.equals(item))                
            {               
                // Item was saved
                int itemNode = _treeField.addChildNode(_categoryNode, item.getName());                
                item.setNode(itemNode);
                _directoryItems.addElement(item);
            }          
        }
        
        /**
         * @see Object#toString()         
         */
        public String toString()
        {            
            return "Add Item to " + _categoryName;
        }
    }
    
    /**
     * A MenuItem class to open a directory item 
     */
    private final class OpenItem extends MenuItem
    {
        private OpenItem()
        {
            super("Open Item", 0, 0);            
        }
        
        public void run()
        {
            int currentNode = _treeField.getCurrentNode();
    
            if(_treeField.getCookie(currentNode) instanceof String)
            {
                displayItem(currentNode);  
            }          
        }               
    }  
    
        
    /**
     * A MenuItem class to delete a directory item 
     */
    private final class DeleteItem extends MenuItem
    {        
        private int _currentNode;        
        
        /**
         * Constructs a MenuItem object to delete a directory item 
         * @param currentNode The currently highlighted node, corresponding to the item which should be deleted         
         */
        private DeleteItem(int currentNode)        
        {
            super("", 0, 0);
            _currentNode = currentNode;            
        }        
        
        /**
         * Runs when the menu item is invoked by an end user.  
         */
        public void run()
        {
            DirectoryItem item;
            
            // Loop through the DirectoryItems vector until the object is found which
            // corresponds to the currently highlighted node.
            for(int i = _directoryItems.size() -1; i >= 0; --i)
            {
                item = (DirectoryItem)_directoryItems.elementAt(i);
                
                if(item.getNode() == _currentNode)
                {
                    // Delete the item from the DirectoryItems vector and the tree field
                    _directoryItems.removeElementAt(i);
                    _treeField.deleteSubtree(_currentNode);
                  
                  // Delete the item from the database
                  _sqlManager.deleteItem(item.getId());                  
                }
            }                      
        }        
        
        /**
         * @see Object#toString() 
         */
        public String toString()
        {            
            return "Delete Item"; 
        }
    }
    
    
    /**
     * A Dialog subclass which allows an end user to add a new category to
     * the application.
     */
    private final static class AddCategoryDialog extends Dialog
    {
        EditField _editField; 
        
        /**
         * Default Constructor
         */
        public AddCategoryDialog()
        {
            super(Dialog.D_OK_CANCEL, "Add Category", Dialog.OK, null, Dialog.GLOBAL_STATUS);
            _editField = new EditField("Name: ", "");
            add(_editField);
        }
        
        /**
         * Returns the text entered by the user
         * @return The text entered by the user
         */
        String getText()
        {
            return _editField.getText();
        }       
    } 
    
    /**
     * A MenuItem class to add a new category to the application and the database.
     */
    private final class AddCategory extends MenuItem
    {      
        /**
         * Default constructor
         */
        private AddCategory()
        {
            super("Add Category", 0, 0);            
        }
        
        /**
         * Runs when the menu item is invoked by the end user.  Inserts a new
         * entry into the Category database table and adds a new Category object
         * to the respective vector.
         */
        public void run()
        {       
            AddCategoryDialog dialog = new AddCategoryDialog();

            if (dialog.doModal() == Dialog.OK)
            {
                String name = dialog.getText();                
                         
                // Add a new category to the database           
                Category category = _sqlManager.addCategory(name);                   
                       
                if(category != null)
                {                     
                    int categoryNode = _treeField.addChildNode(0, category);                      
                    _treeField.setCurrentNode(categoryNode);              
                    category.setNode(categoryNode); 
                }                                                                                                       
            }                     
        }      
    }   
    
    /**
     * A MenuItem class to delete a category from the application and from the database
     */
    private final class DeleteCategory extends MenuItem
    {
        private Category _category;
        
        /**
         * Constructs a new DeleteCategory object to delete a category from the
         * application and from the database.
         * @param category The Category object to be deleted
         */
        private DeleteCategory(Category category)
        {
            super("Delete Category", 0, 0);   
            _category = category;        
        }        
        
        /**
         * Runs when this menu item is invoked by an end user.
         */
        public void run()
        {       
            // Remove category from the tree field                        
            _treeField.deleteSubtree(_category.getNode());
            
            int id = _category.getId();
            
            // Delete the category from the database
            _sqlManager.deleteCategory(id);
            
            DirectoryItem item;
            
            // Loop through the DirectoryItems vector and delete all
            // objects having a category ID corresponding to the
            // highlighted category.
            for(int j = _directoryItems.size() - 1; j >= 0; --j)
            {
                item = (DirectoryItem)_directoryItems.elementAt(j);
                if(item.getCategoryId() == id)
                {
                    _directoryItems.removeElementAt(j);
                }
            }                  
        }
        
        /**
         * @see Object#toString() 
         */
        public String toString()
        {
            return "Delete " + _category.getName();
        }      
    }  
}
