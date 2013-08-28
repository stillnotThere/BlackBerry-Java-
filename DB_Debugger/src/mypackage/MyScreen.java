package mypackage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.database.Statement;
import net.rim.device.api.io.IDNAException;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class MyScreen extends MainScreen
{
	public String Path = "file:///SDCard/db/acropolis.db";
//	public String Path = "file:///store/home/user/acropolis.db";
	public URI db_URI = null;
	
	public String CreateSchema = "CREATE TABLE 'DB_DEBUG' ('TEST1' TEXT , 'TEST2' INTEGER)";
	public String InsertQuery = "INSERT INTO DB_DEBUG (TEST1,TEST2) VALUES ";
	public String SelectQuery = "SELECT (TEST1,TEST2) FROM DB_DEBUG WHERE TEST2=45";
	
    /**
     * Creates a new MyScreen object
     */
    public MyScreen()
    {        
        // Set the displayed title of the screen       
        setTitle("DB Debugger");
        new PlanModelFactory();
        StartUP_Check();
        TestDB();
    }
    
    public void TestDB()
    {
    	double latitude=43.641679;
    	double longitude=-79.627809;
    	 try{
         	URI uri = URI.create(Path);
         	
         	add(new RichTextField("IDNA representation:" + uri.toIDNAString()));
         	add(new RichTextField("IRI representation:" + uri.toIRIString()));
         	add(new RichTextField("URI Absolute:" + uri.isAbsolute()));
         	add(new RichTextField("File host:" + uri.getHost()));
         	add(new RichTextField("File authority:" + uri.getAuthority()));
         	add(new RichTextField("URI scheme:" + uri.getScheme()));
         	add(new RichTextField("URI fragment:" + uri.getFragment()));
         	add(new RichTextField("URI port:" + uri.getPort()));
         	add(new RichTextField("URI user:" + uri.getUserinfo()));
//         	new ModelFactory().UpdateData("lat", String.valueOf(latitude));
//         	new ModelFactory().UpdateData("lng", String.valueOf(longitude));
//         	new ModelFactory().SelectData("received");
//         	new ModelFactory().SelectAll();
//         	new ModelFactory().AddColumn("roam_used_mins");
//         	new ModelFactory().AddColumn("roam_used_msgs");
//         	new ModelFactory().AddColumn("roam_used_data");
         	new UsageModelFactory();
         } catch(Exception e) { 
         	e.printStackTrace();
         } 
    }
    
    /**
	 * At initial boot-up cycle
	 */
	public void StartUP_Check()
	{
		try{
			URI uri = URI.create(Path);
			
			new Logger().LogMessage("URI::"+uri.toIDNAString());
			Database db_check = DatabaseFactory.openOrCreate(uri);
			db_check.close();
			
			FileConnection fileConnection = (FileConnection) Connector.open(Path);
			if(fileConnection.exists() && fileConnection.fileSize()==0)
			{
				MoveDBFromResourceToFileSystem(fileConnection);
				if(fileConnection!=null)
				{
					fileConnection.close();
				}
			}
		} catch (IDNAException e) {
			e.printStackTrace();
			new Logger().LogMessage(e.getMessage());
		} catch(MalformedURIException e) {
			e.printStackTrace();
			new Logger().LogMessage(e.getMessage());
		} catch(DatabaseException e) {
			e.printStackTrace();
			new Logger().LogMessage(e.getMessage());
	 	} catch(IOException e) {
	 		e.printStackTrace();
	 		new Logger().LogMessage(e.getMessage());
	 	}
	}
    
	/**
	 * Move "acropolis.db" application package to File-System(SDCard/store)
	 */
	public void MoveDBFromResourceToFileSystem(FileConnection fileConnection)
	{
		try{
			OutputStream outputStream = null;
	        InputStream inputStream = null;       
	                       
	        // Open an input stream to the pre-defined encrypted database bundled
	        // within this module.
	        inputStream = getClass().getResourceAsStream("/acropolis.db"); 
	        
	        // Open an output stream to the newly created file
	        outputStream = (OutputStream)fileConnection.openOutputStream();                                       
	        
	        // Read data from the input stream and write the data to the
	        // output stream.            
	        byte[] data = new byte[2000];
	        int length = 0;
	        while (-1 != (length = inputStream.read(data)))
	        {
	            outputStream.write(data, 0, length);                
	        }  
	        if(outputStream != null)
	        {
	            outputStream.close();
	        } 
	        if(inputStream != null)
	        {
	            inputStream.close();
	        }
	        new Logger().LogMessage("DB moved");
	        
		} catch(IOException e) {
			e.printStackTrace();
			new Logger().LogMessage("IOException:"+e.getClass()+"::"+e.getMessage());
		}
	}
    
}
