package com.roaming.checker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.DataTypeException;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.database.DatabaseIOException;
import net.rim.device.api.database.DatabasePathException;
import net.rim.device.api.database.DatabaseSecurityOptions;
import net.rim.device.api.database.Row;
import net.rim.device.api.database.Statement;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.EventLogger;

public class ModelFactory 
{
	final long GUID = 0x2ba36897214e5561L;
	final String AppName = "RoamingChecker";
	
	/*DB Location/Path & Name*/
	public String DBName = "ACROPOLIS";
	public String DBPath = "/ProjectAcropolis/Database/";		// SDCard/ProjectAcropolis/Database/
	public String SDCardStorage = "/sdcard";
	public String eMMCStorage = "/store";
	public String SystemStorage = "/system";
	public URI db_uri;
	public Database db;

	//public FileConnection db_fileconn;
	public OutputStream db_fileout = null;
	public InputStream db_filein = null;
	
	public Statement statement;
	
	/*DB Schema*/
	public final String SCHEMA = "ACROPOLIS";
	public final String COORD_TABLE = "CREATE TABLE 'ACROPOLIS' (" +
			"'PHONE_NUMBER' TEXT," +				//FORMATTED 	+1 (647)-464-8458
			"'FIX_DATE' TEXT," +					//DATE 			mm/dd/yyyy
			"'FIX_TIME' TEXT," +					//TIME 			hh:mm **24hrs
			"'ROAMING' TEXT," +					//ROAMING 		true/false
			"'FIX_ACK' TEXT," +					//ACK.			true/false
			"'LAT' TEXT," +						//LATITUDE		xx.xxxxxx or 67.43125**iff FALSE
			"'LNG' TEXT," +						//LONGITUDE		xx.xxxxxx or -45.123456**iff FALSE
			"'ACC' TEXT" +						//ACCURACY		xx.xxx or 1234.1234**iff FALSE
			")";
	
	/*Schema queries*/
	public final String INSERT_QUERY = "INSERT INTO " + SCHEMA + " VALUES (";
	public final String UPDATE_QUERY = "UPDATE " + SCHEMA + " SET ";
	public final String SELECT_ALL_QUERY = "SELECT * FROM " + SCHEMA;
	public final String DELETE_ALL_QUERY = "DELETE FROM "+SCHEMA;
	
	//Always log for model workers
	public ModelFactory()
	{
		EventLogger.register(GUID, AppName, EventLogger.ALWAYS_LOG);
	}

	/**
	 * Creates URI
	 */
	public void CreateDB()
	{
		try{
			//		/SDCard/ProjectAcropolis/ACT
			db_uri = URI.create(SDCardStorage + DBPath + DBName);				//creates file on SDCard with new URI(Uniform Resource Identifier)
			EventLogger.logEvent(GUID, (db_uri.toString() + "::CREATED!!!").getBytes());
												//db_uri.toIDNAString()  ASCII characters are encoded to String
												//db_uri.toIRIString()	ASCII characters are encoded as %xx String
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Normal Text file is created,encrypted and stored on LocalStorage if allowed by the device
	 */
	public void InstantiateDB()
	{
		try {
			db = DatabaseFactory.openOrCreate(db_uri);//,new DatabaseSecurityOptions(false));		//creates DB from URI
			//writing over all the DB in module to MemoryCard
			//CopyDataToMemoryCard();
			EventLogger.logEvent(GUID, (DBName + "::CREATED").getBytes());
			//CreateSchema();
			//Closing db for memory card work
			CloseDB();
			FileConnection file = (FileConnection)Connector.open("file://" + SDCardStorage + DBPath + DBName );
			CopyDataToMemoryCard(file);
			//Open db for insertion of dummy data
			OpenDB();
			//InsertDummyData();
//			EncryptDB();
			
		} catch (DatabaseIOException e) {
			e.printStackTrace();
		} catch (DatabasePathException e) {
			e.printStackTrace();
		} catch( IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Encrypts DB with RIM Runtime Signature 
	 */
	public void EncryptDB()
	{
		try {
			DatabaseSecurityOptions db_security = new DatabaseSecurityOptions(CodeSigningKey.RRT_SIGNER_ID);
			DatabaseFactory.encrypt(db_uri ,db_security);
		} catch (DatabaseIOException e) {
			e.printStackTrace();
		} catch (DatabasePathException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * If encrypted & unlocked only then it will decrypt
	 */
	public void DecryptDB()
	{
		try{
			DatabaseFactory.decrypt(db_uri);
		} catch(DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	public Database getDataBase()
	{
		return db;
	}
	
	/**
	 * DB when created can be opened for working under or over
	 */
	public void OpenDB()
	{
		try {
			db = DatabaseFactory.open(db_uri);		//opens DB
			EventLogger.logEvent(GUID, (DBName + "::OPENED").getBytes());
		} catch (ControlledAccessException e) {
			e.printStackTrace();
		} catch (DatabaseIOException e) {
			e.printStackTrace();
		} catch (DatabasePathException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes open database
	 */
	public void CloseDB()
	{
		try {
			db.close();
		} catch (DatabaseIOException e) {
			e.printStackTrace();
		}
	}
	
	/*******************************************************/
	/*********				CRUD				************/
	/*****************	C - reate		********************/
	/*****************	R - ead			********************/
	/*****************	U - pdate		********************/
	/*****************	D - elete		********************/
	/*******************************************************/
	
	/**
	 * Creates COORD_TABLE
	 */
	public void CreateSchema(Database database)
	{
		try{
			db = database;
			statement = db.createStatement(COORD_TABLE);
			statement.prepare();
			statement.execute();
			statement.close();
			EventLogger.logEvent(GUID, (SCHEMA + "::CREATED").getBytes());
		} catch(DatabaseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inserts data in DB
	 */
	public void InsertData( Database database, String phno, String date, String time, String roam, 
			String ack, String lat, String lng, String acc )
	{
		try{
			db = database;
			statement = db.createStatement(INSERT_QUERY + "'" + phno + "','" + date + "','" + 
					time + "','" + roam + "','" + ack + "','" + lat + "','" + lng + "','" + acc + "')");
			statement.prepare();
			statement.execute();
			statement.close();
			EventLogger.logEvent(GUID, (DBName + "::INSERTED").getBytes());
		} catch(DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates table contents
	 * @param column
	 * @param data
	 */
	public void UpdateData(String column,String data, String where_col,String where_arg)
	{
		try{
			statement = db.createStatement(UPDATE_QUERY + column + " = " + data + " WHERE " + where_col + " = " + where_arg);
			statement.prepare();
			statement.execute();
			statement.close();
		} catch(DatabaseException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Fetches all the data from the table
	 * @return All[Row][Column]
	 */
	public String[][] SelectAll()
	{
		int row_counter=0,column_counter=0;
		int TableColumns = 0;
		String[][] All = new String[40][40];		//All [Row] [Column]
		Row row;
		Cursor cursor;
		
		try{
			statement = db.createStatement(SELECT_ALL_QUERY);
			statement.prepare();
			statement.execute();
			cursor = statement.getCursor();
			cursor.first();									//point @ first row
			row = cursor.getRow();
			TableColumns = row.getColumnNames().length;		//returns 8
			while( cursor.next() )
			{
				++row_counter;
				for(column_counter=1 ; column_counter<=TableColumns ; column_counter++)
				{
					All[row_counter][column_counter] = row.getString(column_counter);		//fetches String represented data from all the COLUMNS of the "current ROW"
				}
			}
			cursor.close();
			statement.close();
		} catch(DatabaseException e) {
			e.printStackTrace();
		} catch(DataTypeException e) {
			e.printStackTrace();
		}
		return All;
	}
	
	/**
	 * Deletes all data from the table
	 */
	public void DeleteAll()
	{
		try{
			statement = db.createStatement(DELETE_ALL_QUERY);
			statement.prepare();
			statement.execute();
			statement.close();
		} catch(DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	/*******************************************************/
	/*********			VALIDATAIONS			************/
	/*********			FILE HANDLING			************/
	/*******************************************************/
	
//	/**
//	 * Checks whether file/database is not empty
//	 * @return
//	 */
//	public boolean CheckEmpty()
//	{
//		boolean db_Empty = false;
//		
//		try{
//			db_fileconn = (FileConnection)Connector.open("file://" + DBPath + DBName);
//			if(db_fileconn.fileSize() > 0)
//				db_Empty = false;
//			else
//				db_Empty = true;
//		} catch(IOException e) {
//			e.printStackTrace();
//		}
//		
//		return db_Empty;
//	}
	
	/**
	 * Copies database contents onto memory card
	 * @return "true" is successful or "false" on else
	 */
	public boolean CopyDataToMemoryCard(FileConnection db_fileconn)
	{
		boolean copy_success = false;
		try{
			//db_fileconn.setHidden(true);		//hides file
			//writing over the file on SDCard from module's DB
			if(db_fileconn.exists() && db_fileconn.fileSize() == 0)
			{
				db_filein = getClass().getResourceAsStream( "/" + DBName );
				db_fileout = db_fileconn.openOutputStream();
				byte[] data = new byte[256];
				int length = 0;
				while (-1 != (length = db_filein.read(data)))
				{
					db_fileout.write(data, 0, length);                
				}
				//closing all connection and I/Os
				if(db_fileconn != null)
					db_fileconn.close();
				if(db_fileout != null)
					db_fileout.close();
				if(db_filein != null)
					db_filein.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return copy_success;
	}
	
	/**
	 * Puts dummy data in database
	 */
	public void InsertDummyData(Database database)
	{
		String DevicePhoneNumber = Phone.getDevicePhoneNumber(true);
		Date currentdatetime = Calendar.getInstance().getTime();
		SimpleDateFormat sdf_date = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
		
		String setDummyNumber = DevicePhoneNumber;
		String setDummyDate = sdf_date.format(currentdatetime).toString();
		String setDummyTime = sdf_time.format(currentdatetime).toString();
		String setDummyRoaming = "n/a";
		String setDummyAck = "n/a";
		String setDummyLat = "n/a";
		String setDummyLng = "n/a";
		String setDummyAcc = "n/a";

		InsertData(database,setDummyNumber, setDummyDate, setDummyTime, setDummyRoaming, setDummyAck,
				setDummyLat, setDummyLng, setDummyAcc); 
	}
}
