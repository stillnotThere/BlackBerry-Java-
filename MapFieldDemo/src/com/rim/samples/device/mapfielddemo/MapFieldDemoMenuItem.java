/*
 * MapFieldDemoMenuItem.java
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

package com.rim.samples.device.mapfielddemo;

import net.rim.device.api.ui.MenuItem;

/**
 * MapFieldDemoMenuItem defines the type of menu items used in MapFieldDemo.
 */
public abstract class MapFieldDemoMenuItem extends MenuItem 
{
    private String _campus;
    
    /**
     * Creates a new menu item.
     * 
     * @param text      Text to be displayed representing the menu item.
     * @param campus    The campus that this menu item refers to.
     * @param ordinal   Determines the order of the menu item, with lower values 
     *                  appearing higher.
     * @param priority  Determines the priority of a menu item, with lower values 
     *                  representing a higher priority.
     */
    public MapFieldDemoMenuItem(String text, String campus, int ordinal, int priority) 
    {
        super(text, ordinal, priority);
        
        _campus = campus;       
    }
    
    /**
     * Determines whether the particular object of this class should be included
     * in the menu.
     * <p>Every sub-class must provide its own implementation.
     * 
     * @return True if the menu item should be included in the menu and 
     * false if otherwise.
     */
    protected abstract boolean isValid();
    
    /**
     * Returns the campus that this menu item refers to.
     * @return The campus referred to.
     */
    public String getCampus() 
    {
        return _campus;
    }
}
