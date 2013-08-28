/*
 * DirectoryItem.java
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

package com.rim.samples.device.sqlitedemo;

/**
 * Class to represent a directory item
 */
public final class DirectoryItem
{
    private String _name;
    private String _location;
    private String _phone;
    private int _categoryId;
    private int _id;   
    private int _node; 
    
    /**
     * This constructor is called when the application starts and item
     * records are read from the database.  This application maintains
     * a Vector of DirectoryItem objects, one for each item record in the
     * database. 
     * @param id The unique ID for the directory item
     * @param name The name of the business represented by the directory item
     * @param location The street address of the business represented by the directory item
     * @param phone The phone number for the business represented by the directory item
     * @param categoryId The category to which the directory item belongs
     * @param node The tree field node number that maps to this directory item
     */    
    public DirectoryItem(int id, String name, String location, String phone, int categoryId)
    {
        _id = id;
        _name = name;
        _location = location;
        _phone = phone;
        _categoryId = categoryId;                
    }   
    
    /**
     * This constructor is used to create a copy of an existing DirectoryItem
     * object for the purpose of determining whether the original object has
     * been edited in the item screen.
     * @param item The original DirectoryItem to copy
     */
    DirectoryItem(DirectoryItem item)
    {
      _name = item.getName();
      _location = item.getLocation();
      _phone = item.getPhone();              
    }
    
    /**
     * This constructor is called when a user wishes to add a new item
     * to a given category.
     * @param categoryId The id for the category to which the new item will be added
     */
    DirectoryItem(int categoryId)
    {
        _categoryId = categoryId;        
        _name = "";
        _location = "";
        _phone = "";        
    }
    
    /**
     * @see Object#equals(Object)
     */
    public boolean equals(Object obj)
    {
        if(obj == this)
        {
            return true;
        }
        
        boolean equal = false;
        
        if(obj instanceof DirectoryItem)
        {
            DirectoryItem item = (DirectoryItem)obj;
            equal =  (item.getName().equals(_name) && item.getLocation().equals(_location) && item.getPhone().equals(_phone));             
        }
        return equal;       
    }
    
    /**
     * Returns the unique ID for the directory item
     * @return The unique ID associated with this DirectoryItem object
     */
    int getId()
    {
        return _id;
    }
    
    /**
     * Returns the name of the business represented by the directory item
     * @return The name of the business represented by this DirectoryItem object
     */
    String getName()
    {
        return _name;
    }
    
    /**
     * Returns the street address of the business represented by the directory item
     * @return The street address of the business represented by this DirectoryItem object
     */
    String getLocation()
    {
        return _location;
    }
    
    /**
     * Returns the phone number for the business represented by the directory item
     * @return The phone number for the business represented by this DirectoryItem object
     */
    String getPhone()
    {
        return _phone;
    }
    
    /**
     * Returns the ID of the category to which the directory item belongs
     * @return The ID of the category to which this DirectoryItem object belongs
     */
    int getCategoryId()
    {
        return _categoryId;
    } 
    
    /**
     * Returns the tree field node number that maps to this directory item
     * @return The tree field node number that maps to this DirectoryItem object
     */
    int getNode()
    {
        return _node;
    }  
    
    /**
     * Sets the name of the business represented by the directory item
     * @param name The text to set as the name of the business represented by this DirectoryItem object
     */
    void setName(String name)
    {
        _name = name;
    }
    
    /**
     * Sets the street address of the business represented by this directory item 
     * @param location The text to set as the street address of the business represented by this DirectoryItem object
     */
    void setLocation(String location)
    {
        _location = location;
    }
    
    /**
     * Sets the phone number for the business represented by this directory item object
     * @param phone The text to set as the phone number for the business represented by this DirectoryItem object
     */
    void setPhone(String phone)
    {
        _phone = phone;
    }    
    
    /**
     * Sets the tree field node number that maps to this directory item
     * @param node The tree field node number that maps to this DirectoryItem object
     */
    void setNode(int node)
    {
        _node = node;
    }    
    
    /**
     * Sets the unique ID for the directory item as generated by the database
     * @param id The unique ID for the directory item as generated by the database
     */
    void setId(int id)
    {
        _id = id;
    }
}    
