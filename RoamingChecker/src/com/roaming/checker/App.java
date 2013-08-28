package com.roaming.checker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.database.DatabaseSecurityOptions;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class App extends UiApplication 
{

	ModelFactory theModel = new ModelFactory();
	String DBPath = "/SDCard/databases/RoamingChecker/";
	String DBName = "RoamingChecker.db";
	
    /**
     * Entry point for application
     * @param args Command line arguments (not used)
     */ 
    public static void main(String[] args) throws Exception
    {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        App theApp = new App();       
        theApp.enterEventDispatcher();
    }
    

    /**
     * Creates a new MyApp object
     */
    public App() throws Exception
    {        
//    	boolean sdCardPresent = false;
//    	String root = null;
//    	Enumeration e = FileSystemRegistry.listRoots();
//    	while (e.hasMoreElements())
//    	{
//    		root = (String)e.nextElement();
//    		if(root.equalsIgnoreCase("sdcard/"))
//    		{
//    			sdCardPresent = true;
//    		}     
//    	}  
//    	if(!sdCardPresent)			//Memory card not present
//    	{
//    		UiApplication.getUiApplication().invokeLater(new Runnable()
//    		{
//    			public void run()
//    			{
//    				Dialog.alert("This application requires an SD card to be present. Exiting application...");
//    				System.exit(0);            
//    			} 
//    		});        
//    	} 
//    	else						//Memory card present and active
//    	{
//    		URI uri = URI.create(DBPath + DBName);
//    		Database db = DatabaseFactory.openOrCreate(uri,new DatabaseSecurityOptions(false));
//    		db.close();
//    		
//    		FileConnection fileconn = (FileConnection) Connector.open("file://" + DBPath + DBName);
//    		if(fileconn.exists() && fileconn.fileSize() == 0)
//    		{
//    			readAndWriteDatabaseFile(fileconn);
//    		}
//    		
//    		DatabaseFactory.encrypt(uri, new DatabaseSecurityOptions(CodeSigningKey.RRT_SIGNER_ID));
//    		
//    		db = DatabaseFactory.open(uri);
//    		
//    		theModel.CreateSchema(db);
//    		theModel.InsertDummyData(db);
//    		
////    		theModel.CreateDB();
////    		theModel.InstantiateDB();
////    		theModel.OpenDB();
//    		UiApplication.getUiApplication().invokeLater(new Runnable()
//    		{
//    			public void run()
//    			{
//    				Dialog.alert("DB created succefully");
//    			} 
//    		});        
//    		
//    	}
    	
    	// Push a screen onto the UI stack for rendering.
        pushScreen(new Screen());
    }
    
    
    public void readAndWriteDatabaseFile(FileConnection fileConnection) throws IOException
    {        
        OutputStream outputStream = null;
        InputStream inputStream = null;       
                       
        // Open an input stream to the pre-defined encrypted database bundled
        // within this module.
        inputStream = getClass().getResourceAsStream("/" + DBName); 
        
        // Open an output stream to the newly created file
        outputStream = (OutputStream)fileConnection.openOutputStream();                                       
        
        // Read data from the input stream and write the data to the
        // output stream.            
        byte[] data = new byte[256];
        int length = 0;
        while (-1 != (length = inputStream.read(data)))
        {
            outputStream.write(data, 0, length);                
        }     
        
        // Close the connections
        if(fileConnection != null)
        {
            fileConnection.close();
        }
        if(outputStream != null)
        {
            outputStream.close();
        } 
        if(inputStream != null)
        {
            inputStream.close();
        }            
    }
    
}
