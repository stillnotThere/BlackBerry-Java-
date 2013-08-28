package mypackage;

import net.rim.device.api.lbs.picker.AbstractLocationPicker;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class MyScreen extends MainScreen
{
    /**
     * Creates a new MyScreen object
     */
    public MyScreen()
    {        
        // Set the displayed title of the screen       
        setTitle("MyTitle");
        
        Field field = null;
        field.setChangeListener(new AbstractLocationPicker(null, null){
        	
        
        });
        
        
    }
}
