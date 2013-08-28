/*
 * Category.java
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


/**
 * Class to represent a directory category
 */
public final class Category 
{
    int _id;
    String _name;
    int _node;
    

    /**
     * Constructs a Category object
     * @param id The unique identifier associated with the category
     * @param name The descriptive name of the category
     */
    public Category(int id, String name)
    {
        _id = id;
        _name = name;        
    }
    
    
    /**
     * Returns the unique identifier associated with the category
     * @return The unique identifier associated with this Category object
     */
    int getId()    
    {
        return _id;        
    }
    
    /**
     * Returns the descriptive name of the category
     * @return The descriptive name of this Category object
     */        
    String getName()
    {
        return _name;
    }
    
    /**
     * Returns the tree field node number that maps to the category
     * @return The tree field node number that maps to this Category object
     */
    int getNode()
    {
        return _node;
    }
    
    /**
     * Sets the tree field node number that maps to the category
     * @param node The tree field node number that maps to this Category object
     */
    void setNode(int node)
    {
        _node = node;        
    }
}
