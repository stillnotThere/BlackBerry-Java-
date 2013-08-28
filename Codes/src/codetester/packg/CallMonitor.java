package codetester.packg;
import net.rim.blackberry.api.phone.AbstractPhoneListener;
import net.rim.blackberry.api.phone.Phone;
import net.rim.blackberry.api.phone.PhoneCall;


public class CallMonitor 
{

	public boolean Incoming = false;
	public boolean Outgoing = false;
	
	public PhoneCall call=null;
	public Phone phone=null;
	public int callID=0;
	
	public int IN_minutes = 0;
	public int OUT_minutes = 0;
	
	public CallMonitor()
	{
		new Logger().LogMessage(">>CallMonitor<<");
		Phone.addPhoneListener((AbstractPhoneListener)new CallAbstractListner());
	}
	
	public class CallAbstractListner extends AbstractPhoneListener
	{

		public void callAnswered(int arg0) {
			new Logger().LogMessage("call answered :"+arg0);
		}

		public void callInitiated(int arg0) 
		{
			Outgoing = true;
			new Logger().LogMessage("Outgoing call");
		}
		
		public void callIncoming(int arg0) 
		{
			Incoming = true;
			new Logger().LogMessage("Incoming call");
		}
		
		public void callConnected(int arg0) {
			new Logger().LogMessage("Call connected!!");
			if(Outgoing)
			{
				call = Phone.getCall(arg0);
				new Logger().LogMessage("Answered Outgoing call");
			}
			else if(Incoming)
			{
				call = Phone.getCall(arg0);
				new Logger().LogMessage("Answered Incoming call");
			}
		}

		public void callDisconnected(int arg0) {
			int out = 0;
			int in = 0;
			if(Outgoing)
			{
				out = call.getElapsedTime();
				new Logger().LogMessage("out minutes:"+out);
				OUT_minutes = OUT_minutes + out;
				
			}
			else if(Incoming)
			{
				in = call.getElapsedTime();
				new Logger().LogMessage("in minutes:"+in);
				IN_minutes = IN_minutes + in;
			}
			
		}
		
		public void callEndedByUser(int arg0) {
			// TODO Auto-generated method stub
			new Logger().LogMessage("Call ended by user");
		}
		
		public void callAdded(int arg0) {
			
		}

		public void callConferenceCallEstablished(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void callDirectConnectConnected(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void callDirectConnectDisconnected(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void callFailed(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		public void callHeld(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void callRemoved(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void callResumed(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void callWaiting(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void conferenceCallDisconnected(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public int getOutgoingDuration()
	{
		return OUT_minutes;
	}
	
	public int getIncomingDuration()
	{
		return IN_minutes;
	}
	
}
