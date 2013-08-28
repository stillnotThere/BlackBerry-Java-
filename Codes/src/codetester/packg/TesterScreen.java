package codetester.packg;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.Memory;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.FontManager;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.GridFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class TesterScreen extends MainScreen
{
	
	final int Charges_Rows = 1;
	final int Charges_Columns = 2;
	final int Rows = 3;
	final int Columns = 2;
	
	String waitText = "waiting..";
	String RoamingString = "Roaming";
	String RoamingResultString = "No";
	String FixString = "Fix acquired";
	String LatitudeString = "Latitude";
	String LongitudeString = "Longitude";
	String LatitudeResultString = "0";
	String LongitudeResultString = "0";
	String IncomingString = "Incoming (Mins)";
	String IncomingResultString = "";
	String OutgoingString = "Outgoing (Mins)";
	String OutgoingResultString = "";
	String TotalMinString = "Total (Mins)";
	String TotalResultMinString = "";
	String ReceivedString = "Received";
	String ReceivedResultString = "";
	String SentString = "Sent Messages";
	String SentResultString = "";
	String TotalMsgString = "Total Messages";
	String TotalResultMsgString = "";
	String DownloadString = "Downloaded (KB)";
	String DownloadResultString = "";
	String UploadString = "Uploaded (KB)";
	String UploadResultString = "";
	String TotalDataString = "Total (KB)";
	String TotalResultDataString = "";
	String TotalLocalCharges = "Total Monitored Cost";
	String TotalResultLocalCharges = "";
	String TotalRoamingCharges = "Total Roaming Cost";
	String TotalResultRoamingCharges = "$0.0";
	String RoamingChargesString = "Roaming Charges(Approx.)";
	String RoamingMinutesChargesString = "Minutes \'$2.00/min\':-- $";
	String RoamingMessageChargesString = "Messages \'$0.6/msg\':-- $";
	String RoamingDataChargesString = "Data \'$5.00/MB\':-- $";
	String LocalChargesString = "Local Charges (Approx.)";
	String LocalMinutesChargesString = "Minutes \'$0.10/min\' (\u221E Incoming) $";
	String LocalMessageChargesString = "Messages \'$0.10/msg\':-- $";
	String LocalDataChargesString = "Data \'$0.06/MB\':-- $";
	
	GridFieldManager LocalChargesGrid = new GridFieldManager(Charges_Rows,Charges_Columns,GridFieldManager.USE_ALL_WIDTH);
	GridFieldManager RoamingChargesGrid = new GridFieldManager(Charges_Rows,Charges_Columns,GridFieldManager.USE_ALL_WIDTH);
	GridFieldManager LocationGrid = new GridFieldManager(Rows,Columns,GridFieldManager.USE_ALL_WIDTH);
    GridFieldManager VoiceGrid = new GridFieldManager(Rows,Columns,GridFieldManager.USE_ALL_WIDTH);
    GridFieldManager MessageGrid = new GridFieldManager(Rows,Columns,GridFieldManager.USE_ALL_WIDTH);
    GridFieldManager DataGrid = new GridFieldManager(Rows,Columns, GridFieldManager.USE_ALL_WIDTH);
	
	RichTextField phonenumberText = new RichTextField("Phone Number : " + Phone.getDevicePhoneNumber(true) );
	RichTextField roamText = new RichTextField(RoamingString,Field.FOCUSABLE|Field.FIELD_LEFT);
	RichTextField roamResultText = new RichTextField("",Field.FOCUSABLE);
//	RichTextField FixAckText = new RichTextField(FixString,Field.NON_FOCUSABLE|Field.FIELD_LEFT); 
	
	RichTextField LatitudeText = new RichTextField(LatitudeString,Field.FOCUSABLE);
	RichTextField LatitudeResultText = new RichTextField(LatitudeResultString,Field.FOCUSABLE);
	RichTextField LongitudeText = new RichTextField(LongitudeString,Field.FOCUSABLE);
	RichTextField LongitudeResultText = new RichTextField(LongitudeResultString,Field.FOCUSABLE);
	
	RichTextField UsageDetails = new RichTextField("Monitored Usage",Field.FIELD_HCENTER|Field.FIELD_VCENTER);
	
	RichTextField MinutesMonitor = new RichTextField("Minutes usage");
	RichTextField IncomingUsage = new RichTextField(IncomingString);
	RichTextField IncomingResultUsage = new RichTextField("",Field.FOCUSABLE);
	RichTextField OutgoingUsage = new RichTextField(OutgoingString);
	RichTextField OutgoingResultUsage = new RichTextField("",Field.FOCUSABLE);
	RichTextField TotalMinsUsage = new RichTextField(TotalMinString,Field.FIELD_RIGHT);
	RichTextField TotalResultMinsUsage = new RichTextField("",Field.FOCUSABLE|Field.FIELD_LEFT);
	
	RichTextField MessagingMonitor = new RichTextField("Messaging usage");
	RichTextField ReceivedMsgUsage = new RichTextField(ReceivedString);
	RichTextField ReceivedResultMsgUsage = new RichTextField("",Field.FOCUSABLE);
	RichTextField SentMsgUsage = new RichTextField(SentString);
	RichTextField SentResultMsgUsage = new RichTextField("",Field.FOCUSABLE);
	RichTextField TotalMsgUsage = new RichTextField(TotalMsgString,Field.FOCUSABLE|Field.FIELD_RIGHT);
	RichTextField TotalResultMsgUsage = new RichTextField("",Field.FOCUSABLE|Field.FIELD_LEFT);

	RichTextField DataMonitor = new RichTextField("Data usage");
	RichTextField DownloadUsage = new RichTextField(DownloadString);
	RichTextField DownloadResultUsage = new RichTextField(DownloadString,Field.FOCUSABLE);
	RichTextField UploadUsage = new RichTextField(UploadString);
	RichTextField UploadResultUsage = new RichTextField(UploadString,Field.FOCUSABLE);
	RichTextField TotalDataUsage = new RichTextField(TotalDataString,Field.FIELD_RIGHT);
	RichTextField TotalResultDataUsage = new RichTextField(TotalResultDataString,Field.FOCUSABLE|Field.FIELD_LEFT);
	
	RichTextField TotalLocal = new RichTextField(TotalLocalCharges,Field.FIELD_HCENTER);
	RichTextField TotalResultLocal = new RichTextField(TotalResultLocalCharges,Field.FIELD_HCENTER);
	RichTextField LocalCharges =  new RichTextField(LocalChargesString);
	RichTextField LocalMinutesCharges = new RichTextField(LocalMinutesChargesString);
	RichTextField LocalMessageCharges = new RichTextField(LocalMessageChargesString);
	RichTextField LocalDataCharges = new RichTextField(LocalDataChargesString);
	
	RichTextField TotalRoaming = new RichTextField(TotalRoamingCharges,Field.FIELD_HCENTER);
	RichTextField TotalResultRoaming = new RichTextField(TotalResultRoamingCharges,Field.FIELD_HCENTER);
	RichTextField RoamingCharges =  new RichTextField(RoamingChargesString,Field.FIELD_HCENTER);
	RichTextField RoamingMinutesCharges = new RichTextField(RoamingMinutesChargesString);
	RichTextField RoamingMessageCharges = new RichTextField(RoamingMessageChargesString);
	RichTextField RoamingDataCharges = new RichTextField(RoamingDataChargesString);
    
    RichTextField temp = new RichTextField("23424");
	
	DataMonitor data;
	String SMSPort = "";
	RichTextField wifi_connection = new RichTextField("WLAN ::");
	RichTextField data_down;
	RichTextField data_up;
	RichTextField msg_count_recv = new RichTextField("Received: 0",Field.FOCUSABLE);
	RichTextField msg_count_sent = new RichTextField("Sent: 0",Field.FOCUSABLE);
	RichTextField min_count_incoming = new RichTextField("Incoming Duration: 0",Field.FOCUSABLE);
	RichTextField min_count_outgoing = new RichTextField("Outgoing Duration: 0",Field.FOCUSABLE);
    /**
     * Creates a new MyScreen object
     */
    public TesterScreen()
    {        
        // Set the displayed title of the screen       
        setTitle("testing codes...");

        super.setBackground(BackgroundFactory.createLinearGradientBackground
        		(Color.BLANCHEDALMOND, Color.BLANCHEDALMOND, Color.AZURE, Color.YELLOWGREEN));
        
        this.getMainManager().setBackground(BackgroundFactory.createLinearGradientBackground
     			(Color.CYAN, Color.CYAN, 0x00336699, 0x00336699));
    	
        FontManager.getInstance().setApplicationFont(Font.getDefault().derive(Font.SANS_SERIF_STYLE));
        FontManager.getInstance().load(
        		this.getClass().getResourceAsStream("/Aaargh.ttf"), "Aaargh", FontManager.APPLICATION_FONT);
        
        try {
			FontFamily fontFamily = FontFamily.forName("Aaargh");
			Font appFont = fontFamily.getFont(Font.PLAIN, 28);
			FontManager.getInstance().setApplicationFont(appFont);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        
        TableLayoutManager table = new TableLayoutManager(new int[]
 			   {
 			   	TableLayoutManager.FIXED_WIDTH,
 			   	TableLayoutManager.FIXED_WIDTH,
 			   }, new int[]{
            	Display.getWidth()/2,
            	Display.getWidth()/2,
            	}
            ,3, Manager.VERTICAL_SCROLL );
 	   
        table.addAll(new Field[]
        		{
			   		IncomingUsage,
			   		IncomingResultUsage,
			   		OutgoingUsage,
			   		OutgoingResultUsage,
			   		TotalMinsUsage,
			   		TotalResultMinsUsage
        		});
        add(table);

        
        ChargesGrid();
    	add(new RichTextField("   ",Field.NON_FOCUSABLE));
//    	MonitoredField();
//    	PositionGrid();
//    	MinuteField();
//    	VoiceMinutesGrid();
//    	MessageField();
//    	MessageGrid();
//    	DataField();
//    	DataGrid();
    	
        data_down = new RichTextField("Download bytes::0");
        data_up = new RichTextField("Upload bytes::0");
        add(data_down);
        add(data_up);
        add(wifi_connection);
        
        RichTextField TotalRoaming = new RichTextField("roaming roaming");
        XYEdges roamEdges = new XYEdges(20,20,20,20);
		Border roamBorder = BorderFactory.createRoundedBorder(roamEdges, Color.DARKRED, Border.STYLE_SOLID);
		TotalRoaming.setBorder(roamBorder);
    	add(TotalRoaming);
        
        add(new RichTextField("Operator @" + RadioInfo.getNetworkName(RadioInfo.getCurrentNetworkIndex())) );
        add(new RichTextField("Current Operator ::"+RadioInfo.getCurrentNetworkName()));
        add(new RichTextField("Roaming:"+String.valueOf(Check_NON_CAN_Operator())));
        add(new RichTextField("Battery level @" + DeviceInfo.getBatteryLevel()));
        add(new RichTextField("Battery voltage :" + DeviceInfo.getBatteryVoltage()));
        add(new RichTextField("Battery temp :" + DeviceInfo.getBatteryTemperature()));
        add(new RichTextField("Manufacturer Name :" + DeviceInfo.getManufacturerName()));
//        add(new RichTextField("Device flash size :" + DeviceInfo.getTotalFlashSizeEx()));
        add(new RichTextField("Battery status :" + DeviceInfo.getBatteryStatus()));
        
        add(new RichTextField("Platform version::" + DeviceInfo.getPlatformVersion()));
        add(new RichTextField("OS Version::"+DeviceInfo.getOSVersion()));
        add(new RichTextField("Software Version::"+DeviceInfo.getSoftwareVersion()));
        
        add(new RichTextField("Total flash::" + Memory.getFlashTotal()));
        add(new RichTextField("Free flash::" + Memory.getFlashFree()));
        
        data = new DataMonitor();
        data.run();
        
        Application.getApplication().invokeLater(new Runnable() 
        { 
        	public void run()
        	{
        		synchronized(Application.getEventLock())
        		{
	        		data_down.setText("Download::"+ (data.getDownload()/(1024*1024)));
	        		data_up.setText("Upload::"+data.getUpload()/(1024*1024));
	        		if(data.getWLANState()==true)
	        			wifi_connection.setText("WLAN::"+data.getWLANState() + 
	        					"\r\nProfile::"+data.getWLANProfileName());
	        		else
	        			wifi_connection.setText("WLAN::"+data.getWLANState());
        		}
        	}
        }, 10*1000, true);
        
//        text_scan.HandleMessageConnection();
        
//        new Timer().schedule(new TimerTask()
//        {
//        	public void run()
//        	{
//		    	synchronized(Application.getApplication())
//		    	{
//		    		msg_count_recv.setText(String.valueOf("Received:"+text_scan.getRecievedMessages()));
//		    		msg_count_sent.setText(String.valueOf("Sent:"+text_scan.getSentMessages()));
//		    		min_count_incoming.setText("Incoming Duration:"+call_scan.getIncomingDuration());
//		    		min_count_outgoing.setText("Outgoing Duration:"+call_scan.getOutgoingDuration());
//		    	}
//        	}
//        }, 100, 20*1000);
    }
    
    public void ChargesGrid()
    {
    	LocalChargesGrid.setColumnProperty(0, GridFieldManager.FIXED_SIZE, Display.getWidth()/2);
    	LocalChargesGrid.setColumnProperty(1, GridFieldManager.FIXED_SIZE, Display.getWidth()/2);
    	LocalChargesGrid.add(TotalLocal);
    	TotalResultLocal.setText(TotalResultLocalCharges);
    	LocalChargesGrid.add(TotalResultLocal);
//    	Bitmap roundedBMP = Bitmap.getBitmapResource("rounded-border.png");
//    	LocalChargesGrid.setBorder(BorderFactory.createBitmapBorder(new XYEdges(3,3,3,3),roundedBMP));
    	LocalChargesGrid.setBorder(BorderFactory.createSimpleBorder(new XYEdges(3,3,3,3),Border.STYLE_TRANSPARENT));
    	this.getMainManager().add(LocalChargesGrid);
    	
    	RoamingChargesGrid.setColumnProperty(0, GridFieldManager.FIXED_SIZE, Display.getWidth()/2);
    	RoamingChargesGrid.setColumnProperty(1, GridFieldManager.FIXED_SIZE, Display.getWidth()/2);
    	RoamingChargesGrid.add(TotalRoaming);
    	TotalResultRoaming.setText(TotalResultRoamingCharges);
    	RoamingChargesGrid.add(TotalResultRoaming);
    	RoamingChargesGrid.setBorder(BorderFactory.createSimpleBorder(new XYEdges(3,3,3,3),Border.STYLE_TRANSPARENT));
    	this.getMainManager().add(RoamingChargesGrid);
    }
    
    public void MonitoredField()
    {
    	UsageDetails.setFont(Font.getDefault().derive(
    			Font.BROKEN_LINE_UNDERLINED,45,Ui.UNITS_px,Font.ANTIALIAS_DEFAULT
    			,Font.EMBOSSED_EFFECT));
    	add(UsageDetails);
    }
    
    public void MinuteField()
    {
    	MinutesMonitor.setFont(Font.getDefault().derive(
    			Font.ITALIC,MinutesMonitor.getContentHeight(),Ui.UNITS_px,Font.ANTIALIAS_DEFAULT
    			,Font.EMBOSSED_EFFECT));
    	add(MinutesMonitor);
    }
    
    public void MessageField()
    {
    	MessagingMonitor.setFont(Font.getDefault().derive(
    			Font.ITALIC,MinutesMonitor.getContentHeight(),Ui.UNITS_px,Font.ANTIALIAS_DEFAULT
    			,Font.EMBOSSED_EFFECT));
    	add(MessagingMonitor);
    }
    
    public void DataField()
    {
    	DataMonitor.setFont(Font.getDefault().derive(
    			Font.ITALIC,MinutesMonitor.getContentHeight(),Ui.UNITS_px,Font.ANTIALIAS_DEFAULT
    			,Font.EMBOSSED_EFFECT));
    	add(DataMonitor);
    }
    
    public void PositionGrid()
    {
    	LocationGrid.setColumnProperty(0,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
    	LocationGrid.setColumnProperty(1,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
    	
    	/*Row 1*/
    	LocationGrid.add(roamText);
    	roamResultText.setText(RoamingResultString);
    	LocationGrid.add(roamResultText);
    	
    	/*Row 2*/
    	LocationGrid.add(LatitudeText);
    	LatitudeResultText.setText(LatitudeResultString);
    	LocationGrid.add(LatitudeResultText);
    	
    	/*Row 3*/
    	LocationGrid.add(LongitudeText);
    	LongitudeResultText.setText(LongitudeResultString);
    	LocationGrid.add(LongitudeResultText);
    	
    	LocationGrid.setBorder(BorderFactory.createBevelBorder(new XYEdges(4,4,4,4)));
    	this.getMainManager().add(LocationGrid);
    }
    
    public void VoiceMinutesGrid()
    {
    	VoiceGrid.setColumnProperty(0,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
    	VoiceGrid.setColumnProperty(1,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
    	
    	/*Row 1*/
    	VoiceGrid.add(IncomingUsage);
    	IncomingResultUsage.setText(IncomingResultString);
    	VoiceGrid.add(IncomingResultUsage);
    	
    	/*Row 2*/
    	VoiceGrid.add(OutgoingUsage);
    	OutgoingResultUsage.setText(OutgoingResultString);
    	VoiceGrid.add(OutgoingResultUsage);
    	
    	/*Row 3*/
    	TotalMinsUsage.setFont(Font.getDefault().derive(Font.ITALIC));
    	VoiceGrid.add(TotalMinsUsage);
    	TotalResultMinsUsage.setText(TotalResultMinString);
    	TotalResultMinsUsage.setFont(Font.getDefault().derive(Font.BOLD));
    	VoiceGrid.add(TotalResultMinsUsage);
    	
    	VoiceGrid.setBorder(BorderFactory.createBevelBorder(new XYEdges(4,4,4,4)));
    	this.getMainManager().add(VoiceGrid);
    }
    
    public void MessageGrid()
    {
    	MessageGrid.setColumnProperty(0,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
    	MessageGrid.setColumnProperty(1,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
    	
    	/*Row 1*/
    	MessageGrid.add(ReceivedMsgUsage);
    	ReceivedResultMsgUsage.setText(ReceivedResultString);
    	MessageGrid.add(ReceivedResultMsgUsage);
    	
    	/*Row 2*/
    	MessageGrid.add(SentMsgUsage);
    	SentResultMsgUsage.setText(SentResultString);
    	MessageGrid.add(SentResultMsgUsage);
    	
    	/*Row 3*/
    	TotalMsgUsage.setFont(Font.getDefault().derive(Font.ITALIC));
    	MessageGrid.add(TotalMsgUsage);
    	TotalResultMsgUsage.setText(TotalResultMsgString);
    	TotalResultMsgUsage.setFont(Font.getDefault().derive(Font.BOLD));
    	MessageGrid.add(TotalResultMsgUsage);

    	MessageGrid.setBorder(BorderFactory.createBevelBorder(new XYEdges(4,4,4,4)));
    	this.getMainManager().add(MessageGrid);
    }
    
   public void DataGrid()
   {
	   DataGrid.setColumnProperty(0,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
	   DataGrid.setColumnProperty(1,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
	   
	   /*Row 1*/
	   DataGrid.add(DownloadUsage);
	   DownloadResultUsage.setText(DownloadResultString);
	   DataGrid.add(DownloadResultUsage);
	   
	   /*Row 2*/
	   DataGrid.add(UploadUsage);
	   UploadResultUsage.setText(UploadResultString);
	   DataGrid.add(UploadResultUsage);
	   
	   /*Row 3*/
	   TotalDataUsage.setFont(Font.getDefault().derive(Font.ITALIC));
	   DataGrid.add(TotalDataUsage);
	   TotalResultDataUsage.setText(TotalResultDataString);
	   TotalResultDataUsage.setFont(Font.getDefault().derive(Font.BOLD));
	   DataGrid.add(TotalResultDataUsage);
	   
	   DataGrid.setBorder(BorderFactory.createBevelBorder(new XYEdges(4,4,4,4)));
	   this.getMainManager().add(DataGrid);
   }
   
    
    public boolean Check_NON_CAN_Operator()
	{
    	boolean NON_CANOperatorCheck=false;
    	String[] CanadianOperators = {"TELUS","Bell","Rogers Wireless"};
		String CurrentNetworkName = RadioInfo.getNetworkName(RadioInfo.getCurrentNetworkIndex());
		    	
		if(CurrentNetworkName == null)
		{
			new Logger().LogMessage("no network found");
		}
		else
		{
			new Logger().LogMessage("Device registered on " + CurrentNetworkName);
			if( CurrentNetworkName.equalsIgnoreCase(CanadianOperators[0]) 
			  			|| CurrentNetworkName.equalsIgnoreCase(CanadianOperators[1])
			   			||CurrentNetworkName.equalsIgnoreCase(CanadianOperators[2]) )
				NON_CANOperatorCheck = false;				//if Current Operator is CANADIAN then **FALSE**
			else
				NON_CANOperatorCheck = true;				//if Current Operator is not CANADIAN then **TRUE** hence ROAMING
			    
		}
		return NON_CANOperatorCheck;
	 }
    
}
