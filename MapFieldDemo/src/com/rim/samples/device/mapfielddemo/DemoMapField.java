/*
 * DemoMapField.java
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
 
import java.util.Vector;
import javax.microedition.location.Coordinates;
import net.rim.device.api.lbs.MapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.system.Bitmap;


/**
 * Displays a map and all the sites added to it.
 */
public class DemoMapField extends MapField 
{    
    // Vector of sites
    private Vector _allSites = new Vector();
    private MapFieldDemoSite _highlightedSite;          
    
    // For cursor
    private Bitmap _cursor = Bitmap.getBitmapResource("bullseye.png");
    
    // For preferred height
    private LabelField _sampleLabel;    
    
    // Instructive text
    private int _textHeight;  
    private boolean _turnOffText = false;        
    
    /**
     * Initializes map.
     */
    public DemoMapField() 
    {
    	this.isStyle(MapField.FIELD_VCENTER|MapField.FIELD_HCENTER);
        // Sample label is only used to determine the instructive text height
        // and is declared null right after use.
        _sampleLabel = new LabelField();
        _textHeight = _sampleLabel.getPreferredHeight();        
        _sampleLabel = null;                 
    }
    
    /**
     * Runs through all the sites and sets their color.
     */
    private void determineSiteColors() 
    {
        Vector highlightCandidates = new Vector();          // Stores the sites that the cursor touches.
        XYPoint convertedHighlightArea[] = new XYPoint[4];
        
        // Cursor coordinates , obtained by dividing preferred width and height by 2.
        int cursorX = getPreferredWidth() >> 1;     
        int cursorY = getPreferredHeight() >> 1;    
        
        // See comments below.
        int above, below, right, left;
        
        for (int count = 0; count < _allSites.size(); count++) 
        {
            Coordinates[] highlightableArea = ((MapFieldDemoSite)(_allSites.elementAt(count))).getHighlightableArea();
            
            /* The following algorithm dictates that for a site to be deemed
             highlightable, the cursor must be at least over one point, under
             one point, to the left of one point and to the right of one point */
               
            above = below = right = left = 0;
            for (int side = 0; side < 4; side++) 
            {
                convertedHighlightArea[side] = new XYPoint();
                convertWorldToField(highlightableArea[side], convertedHighlightArea[side]);
                
                if (convertedHighlightArea[side].x > cursorX)
                {
                    right++;
                }
                    
                if (convertedHighlightArea[side].x < cursorX)
                {
                    left++;
                }
                    
                if (convertedHighlightArea[side].y > cursorY)
                {
                    above++;
                }
                    
                if (convertedHighlightArea[side].y < cursorY)
                {
                    below++;
                }
            }
            
            // If this condition passes, the site is being touched by the cursor.
            if (right >= 1 && left >= 1 && above >= 1 && below >= 1)
            {
                highlightCandidates.addElement((MapFieldDemoSite)_allSites.elementAt(count));
            }
            else
            {
                ((MapFieldDemoSite)_allSites.elementAt(count)).setHighlight(false);
            }
        }

        if (highlightCandidates.size() > 0) 
        {
            // Highlights the first highlightable site and disregards the rest.
            ((MapFieldDemoSite)(highlightCandidates.elementAt(0))).setHighlight(true);
            _highlightedSite = ((MapFieldDemoSite)(highlightCandidates.elementAt(0)));

            if (highlightCandidates.size() > 1) 
            {
                for (int count = 1; count < highlightCandidates.size(); count++) 
                {
                    ((MapFieldDemoSite)(highlightCandidates.elementAt(count))).setHighlight(false);
                }
            }
        }
    }
    
    /**
     * @see net.rim.device.api.lbs.MapField#paint(Graphics)
     */
    protected void paint(Graphics g) 
    { 
        // Smooths out all the polygons.
        g.setDrawingStyle(Graphics.DRAWSTYLE_AAPOLYGONS, true);
        super.paint(g); 

        // Runs through all sites and determines color.
        determineSiteColors();
        
        // Paints all the sites on the map.  
        for (int count = 0; count < _allSites.size(); count++) 
        {
            MapFieldDemoSite currentSite = (MapFieldDemoSite)_allSites.elementAt(count);
            currentSite.drawSite(g);
        }      
        
        // Places the cursor permanently at the center of the map.
        // Logical right shift ">> 1" is equivalent to division by 2.
        g.drawBitmap(getWidth() >> 1, getHeight() >> 1, getWidth(), getHeight(), _cursor, 0, 0);
        
        // Displays instructive text until turned off.
        if (!_turnOffText) 
        {
            g.setColor(Color.SLATEGRAY);            
            g.drawText("Use 'I' to zoom in", 1, _textHeight + 2);            
            g.drawText("Use 'O' to zoom out", 1, (_textHeight * 2) + 4);            
        }
    }
    
    /**
     * @see net.rim.device.api.lbs.MapField#setZoom(int)
     */
    public void setZoom(int zoom) 
    {
        _turnOffText = true;     
        super.setZoom(zoom);
    }
    
    /**
     * @see net.rim.device.api.ui.Field#navigationMovement(int, int, int, int)
     */
    protected boolean navigationMovement(int dx, int dy, int status, int time) 
    {
        // The map is shifted in relation to the current zoom level.
        int zoom = getZoom();
        int latitude = getLatitude() - ((dy << 3) << zoom);     // << 3 is equivalent to multiplication by 8.
        int longitude = getLongitude() + ((dx << 3) << zoom);                  
        
        moveTo(latitude, longitude);       
       
        return true;        
    }

    /**
     * Adds a site to the map.
     * 
     * @param site Site to be added.
     */
    void addSite(MapFieldDemoSite site) 
    {
        _allSites.addElement(site);
    }    
    
    /**
     * Returns all the highlighted sites.  Ideally there would only be one
     * highlighted site, but differing zoom levels and proximity of sites
     * may result in multiple highlighted sites.
     * 
     * @return Highlighted sites.
     */
    MapFieldDemoSite getHighlightedSite() 
    {
        return _highlightedSite;
    }
}
