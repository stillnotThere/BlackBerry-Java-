package codetester.packg;

import java.util.Vector;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.RuntimeStore;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.picker.FilePicker.Listener;

public class FirstRun {

	
	public class MyApplication extends UiApplication {
	     static final long MY_APP_DESCRIPTOR = 0x57114469668376edL; // MY_APP_DESCRIPTOR hash but it MUST be unique for each application
	     static final long MY_APP_MESSAGE_QUEUE = 0xe2e29c962e9c4214L; // MY_APP_MESSAGE_QUEUE hash but it MUST be unique for each application
	 
	     public MyApplication() {
	         // check if the application has been started at least once
	         RuntimeStore runtimeStore = RuntimeStore.getRuntimeStore();
	         if( runtimeStore.get( MY_APP_DESCRIPTOR ) == null ) {
	             // the application started the very first time - store application descriptor in memory 
	             runtimeStore.put( MY_APP_DESCRIPTOR, ApplicationDescriptor.currentApplicationDescriptor() );
	             // allocate message queue between application and the listener
	             runtimeStore.put( MY_APP_MESSAGE_QUEUE, new Vector() );
	         }
	         installListener();
	         startProcessingMessages();
	         enterEventDispatcher();
	     }
	 
	     public void installListener() {
	         MyListener myListener = new MyListener();
	         /**API.addListener( myListener );**/
	     }
	 
	     public void startProcessingMessages(){
	         new Thread( new Runnable() {
	             public void run() {
	                 RuntimeStore runtimeStore = RuntimeStore.getRuntimeStore();
	                 Vector messageQueue = (Vector) runtimeStore.get( MyApplication.MY_APP_MESSAGE_QUEUE );
	                 while( true ) {
	                     String email = null;
	                     synchronized( messageQueue ) {
	                         if( messageQueue.isEmpty() ) {
	                             try {
									messageQueue.wait();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
	                         } else {
	                             email = (String) messageQueue.elementAt( 0 );
	                             messageQueue.removeElementAt( 0 );
	                         }
	                     }
	                     if( email != null ) {
	                         Dialog.inform( "Found email '" + email + "'" );
	                     }
	                 }
	             }
	         } ).start();
	     }
	 }
	 
	 class MyListener implements Listener {
	     public void onEvent( String email ) throws Exception {
	         RuntimeStore runtimeStore = RuntimeStore.getRuntimeStore();
	         if( email.endsWith( "@acme.com" ) ) {
	             // found email, start my application to notify user
	             ApplicationDescriptor myDescr = (ApplicationDescriptor) runtimeStore.get( MyApplication.MY_APP_DESCRIPTOR );
	             if( myDescr != null ) {
	                 int processID = ApplicationManager.getApplicationManager().runApplication( myDescr );
	                 if( processID > 0 ) {
	                     Vector messageQueue = (Vector) runtimeStore.get( MyApplication.MY_APP_MESSAGE_QUEUE );
	                     synchronized( messageQueue ) {
	                         messageQueue.addElement( email );
	                         messageQueue.notifyAll();
	                     }
	                 }
	             }
	         }
	     }
	 
	     public boolean equals( Object ob ) {
	         return ob instanceof MyListener;
	     }

		public void selectionDone(String selected) {
			// TODO Auto-generated method stub
			
		}
	 }
	
}
