/*
 * MapFieldDemoScreen.java
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

import java.io.IOException;
import java.util.Vector;
import javax.microedition.location.Coordinates;
import net.rim.device.api.io.LineReader;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Trackball;
import java.io.EOFException;

/**
 * Handles the display of MapFieldDemo's UI elements. 
 */
public class MapFieldDemoScreen extends MainScreen 
{    
    // For display
    private DemoMapField _map;
    private LabelField _addressBar1;
    private LabelField _addressBar2;    
    
    // For campuses
    private Vector _campusCoordinates;          // <Coordinates>
    private Vector _campusMenuItems;            // <SiteDisplayMenuItem>
    private Vector _campuses;                   // <String>
    private String _currentCampus;              
    private String _currentMenuCampus;
    
    // For sites
    private Vector _sites;                      // <Site>
    private Vector _siteNames;                  // <String>
    private MapFieldDemoSite _currentSite;                  
    
    private boolean _checkedTrackballSupport = false;  
     
    
    /**
     * Sets up screen elements and initializes the map.
     */
    public MapFieldDemoScreen() 
    {               
        super(Manager.NO_VERTICAL_SCROLL);
        Coordinates defaultLocation;        
        
        // Reads data from file.
        LineReader lineReader = new LineReader(getClass().getResourceAsStream("/MapsData.txt"));        
        
        // Maps instance for screen.
        _map = new DemoMapField();
        
        storeAllSites("/MapsData.txt");
        storeAllCampuses("/MapsData.txt");

        // Look for the default site.
        try 
        {
            for (;;) 
            {
                if (MapFieldDemoTokenizer.getString(lineReader.readLine()).equals("Default Campus:")) 
                {
                    _currentCampus = MapFieldDemoTokenizer.getString(lineReader.readLine());
                    
                    // Find campus location.
                    for (;;) 
                    {
                        if (MapFieldDemoTokenizer.getString(lineReader.readLine()).equals("Campus Display Name:")) 
                        {
                            if (MapFieldDemoTokenizer.getString(lineReader.readLine()).equals(_currentCampus)) 
                            {
                                if (MapFieldDemoTokenizer.getString(lineReader.readLine()).equals("Default Campus Location:")) 
                                {
                                    defaultLocation = MapFieldDemoTokenizer.getCoordinates(lineReader.readLine());
                                    break;
                                }
                            }
                        }
                    }
                    
                    _map.moveTo(defaultLocation);
                    break;
                }
            }
            
            displayTitle(_currentCampus);
            add(_map);
                        
            // Create label fields to display address of highlighted site.            
            _addressBar1 = new LabelField("", LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH);
            _addressBar2 = new LabelField("", LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH);       
        
            // Add the address label fields to the screen's status section.
            VerticalFieldManager statusVFM = new VerticalFieldManager();
            statusVFM.add(_addressBar1);
            statusVFM.add(_addressBar2);                  
            setStatus(statusVFM);                         
            
            createMenuItems();            
            
        } 
        catch (IOException e) 
        {
            MapFieldDemo.errorDialog(e.toString());
        }
    }
    
    
    /**
     * Finds all sites in file and stores them.
     * 
     * @param MapData Location of file containing site information.
     */
    private void storeAllSites(String MapData) 
    {
        LineReader lineReader = new LineReader(getClass().getResourceAsStream(MapData));
        byte[] line;
        
        // For sites
        _siteNames = new Vector();
        _sites = new Vector();
        String currentSiteName;
        
        for (;;) 
        {
            try 
            {
                line = lineReader.readLine();

                if (MapFieldDemoTokenizer.getString(line).equals("Site Name:")) 
                {
                    // Initialize site variables.
                    currentSiteName = MapFieldDemoTokenizer.getString(lineReader.readLine());
                    _siteNames.addElement(currentSiteName);
                    _currentSite = new MapFieldDemoSite(MapData, currentSiteName, _map);
                    _map.addSite(_currentSite);
                    _sites.addElement(_currentSite);
                    
                    // Set campus.
                    _currentCampus = _currentSite.getCampus();
                }
            } 
            catch(EOFException eof)
            {
                // We've reached the end of the file.
                break;
            }
            catch(IOException ioe)
            {
                MapFieldDemo.errorDialog("Error reading data from file: " + ioe.toString());
                break;
            }
        }
    }
    
    
    /**
     * Finds all campuses in file and stores them
     * 
     * @param MapData Location of file containing site information.
     */
    private void storeAllCampuses(String MapData) 
    {
        LineReader lineReader = new LineReader(getClass().getResourceAsStream(MapData));
        byte[] line;
        
        // For campuses
        String campusName;
        _campuses = new Vector();
        _campusCoordinates = new Vector();
        
        for (;;) 
        {
            try 
            {
                line = lineReader.readLine();
                
                if (MapFieldDemoTokenizer.getString(line).equals("Campus Display Name:")) 
                {
                    campusName = MapFieldDemoTokenizer.getString(lineReader.readLine());
                    
                    // Make sure that there are no duplicate campuses in vector.
                    if(!_campuses.contains(campusName)) 
                    { 
                        _campuses.addElement(campusName);
                            
                        // Adds the campus' location.
                        if (MapFieldDemoTokenizer.getString(lineReader.readLine()).equals("Default Campus Location:"))
                        {
                            _campusCoordinates.addElement(MapFieldDemoTokenizer.getCoordinates(lineReader.readLine()));
                        }
                    }
                }
            }
            catch(EOFException eof)
            {
                // We've reached the end of the file.
                break;
            }
            catch(IOException ioe)
            {
                MapFieldDemo.errorDialog("Error reading data from file: " + ioe.toString());
                break;
            }                 
        } 
    } 
    
    
    /**
     * Creates menu items according to the campuses present.
     */
    private void createMenuItems() 
    {
        int defaultPriority = 1000, order = 1000;
        _campusMenuItems = new Vector();
        
        for (int count = 0; count < _campuses.size(); count++) 
        {
            _currentMenuCampus = (String)_campuses.elementAt(count);
            String menuDisplay = "Go to " + _currentMenuCampus;
            
            _campusMenuItems.addElement(new MapFieldDemoMenuItem(menuDisplay, _currentMenuCampus, order++, defaultPriority) 
            {
                
                // If the map displays a current campus, that campus' menu item will set isValid to false.
                protected boolean isValid() 
                {
                    return !_currentMenuCampus.equals(_currentCampus);
                }
                
                public void run() 
                {                  
                    _currentCampus = this.getCampus();
                    
                    // Moves the map to the current campus.
                    _map.moveTo((Coordinates)_campusCoordinates.elementAt(_campuses.indexOf(_currentCampus)));
                    displayTitle(_currentCampus);
                }
            });
        }   
    }
    
    
    /**
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu, int)
     */
    protected void makeMenu(Menu menu, int instance) 
    {
        for (int count = 0; count < _campusMenuItems.size(); count++) 
        {
            // Resets the current campus.
            _currentMenuCampus = ((MapFieldDemoMenuItem)(_campusMenuItems.elementAt(count))).getCampus();
            
            if (((MapFieldDemoMenuItem)(_campusMenuItems.elementAt(count))).isValid())
            {
                menu.add((MapFieldDemoMenuItem)_campusMenuItems.elementAt(count));
            }
        }
        
        super.makeMenu(menu, instance);
    }
    
    
    /**
     * Displays the title according to the current campus.
     * 
     * @param campus Name of the campus.
     */
    private void displayTitle(String campus) 
    {
        setTitle("Welcome to " + campus);
    }
    
    
    /**
     * Displays information at the bottom of the screen when a site is hovered
     * over.
     */
    private void displayText() 
    {
        MapFieldDemoSite highlightedSite = _map.getHighlightedSite();
        
        if (highlightedSite != null && highlightedSite.isHighlighted()) 
        {
            // Displays site name if it's not a stand-alone site.
            if ( !highlightedSite.isStandAloneSite() )
            {
                _addressBar1.setText(highlightedSite.getSiteName() + ": " + highlightedSite.getStreetNumber() + " " + highlightedSite.getStreetName());
            }
            else
            {
                _addressBar1.setText(highlightedSite.getStreetNumber() + " " + highlightedSite.getStreetName());
            }
            
           _addressBar2.setText(highlightedSite.getCity() + ", " + highlightedSite.getProvince() + ", " + highlightedSite.getCountry());
        }
        else 
        {
            // Displays the following if no site is currently highlighted.
            _addressBar1.setText("No highlighted site...");
            _addressBar2.setText("");
        }
    }
    
    
    /**
     * @see net.rim.device.api.ui.Screen#paint(Graphics)
     */
    protected void paint(Graphics g) 
    {
        super.paint(g);
        
        displayText();
    }
        
    
    /**
     * @see net.rim.device.api.ui.Screen#keyDown(int, int)
     */
    protected boolean keyDown(int keycode, int time) 
    {
        StringBuffer sb = new StringBuffer();
        
        // Retrieve the characters mapped to the keycode for the current keyboard layout
        Keypad.getKeyChars(keycode, sb);        
        
        // Zoom in
        if(sb.toString().indexOf('i') != -1) 
        { 
            _map.setZoom(Math.max(_map.getZoom() - 1, _map.getMinZoom()));
            return true;
        }
        // Zoom out
        else if(sb.toString().indexOf('o') != -1) 
        { 
            _map.setZoom(Math.min(_map.getZoom() + 1, _map.getMaxZoom()));
            return true;
        }        

        return super.keyDown(keycode, time);
    } 
           
    
    /**
     * @see Screen#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(TouchEvent message)
    {        
        boolean isConsumed = false;
        
        TouchGesture touchGesture = message.getGesture();           
        if (touchGesture != null)
        {                
            // If the user has performed a swipe gesture we will move the
            // map accordingly.
            if (touchGesture.getEvent() == TouchGesture.SWIPE)
            {      
                // Retrieve the swipe magnitude so we know how
                // far to move the map.
                int magnitude = touchGesture.getSwipeMagnitude();
                
                // Move the map in the direction of the swipe.
                switch(touchGesture.getSwipeDirection())
                {
                    case TouchGesture.SWIPE_NORTH:
                        _map.move(0, - magnitude);
                        break;
                    case TouchGesture.SWIPE_SOUTH:
                        _map.move(0, magnitude);
                        break;
                    case TouchGesture.SWIPE_EAST:
                        _map.move(- magnitude, 0);
                        break;
                    case TouchGesture.SWIPE_WEST:
                        _map.move(magnitude, 0);
                        break;                            
                } 
                isConsumed = true; // We've consumed the touch event.              
            }
        }     
        return isConsumed;       
    }
    
    
    /**
     * @see net.rim.device.api.ui.Screen#navigationMovement(int, int, int, int)
     */
    protected boolean navigationMovement(int dx, int dy, int status, int time) 
    {
        if (!_checkedTrackballSupport) 
        {
            _checkedTrackballSupport = true;
            
            // Allows smoother panning on the map.
            if (Trackball.isSupported()) 
            {
                // Adjust the filter.
                getScreen().setTrackballFilter(Trackball.FILTER_NO_TIME_WINDOW | Trackball.FILTER_ACCELERATION);
            }
        }        
        
        // Delegates this task to the map instance.
        return _map.navigationMovement(dx, dy, status, time);        
    }   
}
