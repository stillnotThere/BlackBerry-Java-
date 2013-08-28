package codetester.packg;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.system.USBPortListener;
import net.rim.device.api.ui.UiApplication;

/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class TesterApp extends UiApplication
{
    /**
     * Entry point for application
     * @param args Command line arguments (not used)
     */ 
    public static void main(String[] args)
    {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        TesterApp theApp = new TesterApp();       
        theApp.enterEventDispatcher();
    }
    

    /**
     * Creates a new MyApp object
     */
    public TesterApp()
    {
    	Application.getApplication().addSystemListener(new USBStateListener());
//    	Application.getApplication().addIOPortListener((IOPortListener)new ConnectionAlive());
        // Push a screen onto the UI stack for rendering.
        pushScreen(new TesterScreen());
    }    
    
    public class ConnectionAlive implements USBPortListener
    {

		public void connected() {
			new Logger().LogMessage("Connected  " + String.valueOf(this.getClass()));
		}

		public void dataReceived(int length) {
			new Logger().LogMessage("Received data length::"+length);
		}

		public void dataSent() {
			
		}

		public void disconnected() {
			new Logger().LogMessage("Disconnected  " + String.valueOf(this.getClass()));
		}

		public void patternReceived(byte[] pattern) {
			
		}

		public void receiveError(int error) {
			
		}

		public void connectionRequested() {
			new Logger().LogMessage("Connection Requested  "+ String.valueOf(this.getClass()));
		}

		public void dataNotSent() {
			
		}

		public int getChannel() {
			return 0;
		}
    	 
    }
    
    
    private class USBStateListener implements SystemListener2 {

    	boolean USBActive = false;
    	boolean connectionRequested = false;
    	boolean waitInactive = false;
    	boolean mediaActive = false;
    	
		public void usbConnectionStateChange(int state)
		{
//			new Logger().LogMessage("USB State::" + state);
			switch(state)
			{
				case 1:{
					new Logger().LogMessage("Wire connected");
					USBActive = true;
				};
				case 2:{
					new Logger().LogMessage("USB_Enumerated");
					mediaActive = true;
				};
				case 8:{
					new Logger().LogMessage("USB_NOT_Enumerated");
					mediaActive = true;
				};
				case 18:{
					new Logger().LogMessage("Media choice");
				};
				case 4:{
					new Logger().LogMessage("Wire disconnected");
					USBActive = false;
					mediaActive = false;
				};
			}
			if(mediaActive)
			{
				synchronized(Application.getEventLock())
				{
					try {
						new Logger().LogMessage("going to sleep");
						Thread.sleep(10*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			Application.getApplication().notifyAll();
		}

		public void batteryGood() {
			// TODO Auto-generated method stub
			
		}

		public void batteryLow() {
			// TODO Auto-generated method stub
			
		}

		public void batteryStatusChange(int status) {
			// TODO Auto-generated method stub
			
		}

		public void powerOff() {
			// TODO Auto-generated method stub
			
		}

		public void powerUp() {
			// TODO Auto-generated method stub
			
		}

		public void backlightStateChange(boolean on) {
			// TODO Auto-generated method stub
			
		}

		public void cradleMismatch(boolean mismatch) {
			// TODO Auto-generated method stub
			
		}

		public void fastReset() {
			// TODO Auto-generated method stub
			
		}

		public void powerOffRequested(int reason) {
			// TODO Auto-generated method stub
			
		}
			
	}
		
}
