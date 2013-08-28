package com.app.project.acropolis.model;

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
import net.rim.device.api.database.Statement;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.IDNAException;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.EventLogger;

public class ModelFactory 
{
	final long GUID = 0x2ba36897214e5561L;
	final String AppName = "Project Acropolis";
	
	/*DB Location/Path & Name*/
	public String DBName = "acropolis.db";
	public String DBPath = "/store/ProjectAcropolis/Database/";
	public URI db_uri;
	public Database db;

	public FileConnection db_fileconn;
	public OutputStream db_fileout = null;
	public InputStream db_filein = null;
	
	public Statement statement;
	
	/*DB Schema*/
	public final String SCHEMA = "ACROPOLIS_LAST_COORDINATES";
	public final String COORD_TABLE = "CREATE TABLE 'ACROPOLIS_LAST_COORDINATES' (" +
			"'PHONE_NUMBER' TEXT NOT NULL," +				//FORMATTED 	+1 (647)-464-8458
			"'FIX_DATE' TEXT NOT NULL," +					//DATE 			mm/dd/yyyy
			"'FIX_TIME' TEXT NOT NULL," +					//TIME 			hh:mm **24hrs
			"'ROAMING' TEXT NOT NULL," +					//ROAMING 		true/false
			"'FIX_ACK' TEXT NOT NULL," +					//ACK.			true/false
			"'LAT' TEXT NOT NULL," +						//LATITUDE		xx.xxxxxx or 67.43125**iff FALSE
			"'LNG' TEXT NOT NULL," +						//LONGITUDE		xx.xxxxxx or -45.123456**iff FALSE
			"'ACC' TEXT NOT NULL" +						//ACCURACY		xx.xxx or 1234.1234**iff FALSE
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
			db_uri = URI.create("file://" + DBPath + DBName);
			db = DatabaseFactory.open(db_uri);
			db.close();									//Open and close the empty file created
			
			MoveDBtoFileSystem();
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch(DatabaseException e) {
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
	 * Inserts data in DB
	 */
	public void InsertData( String phno, String date, String time, String roam, 
			String ack, String lat, String lng, String acc )
	{
		try{
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
	 * @return All[] contains all the data within the table(considering there is only one row)
	 */
	public String[] SelectAll()
	{
		String[] All = new String[40];		//All [Row] [Column]
		Cursor cursor;
		
		try{
			statement = db.createStatement(SELECT_ALL_QUERY);
			statement.prepare();
			statement.execute();
			cursor = statement.getCursor();
			cursor.first();									//point @ first row
			while( cursor.next() )
			{
				All[0] = cursor.getRow().getString(0);
				All[1] = cursor.getRow().getString(1);
				All[2] = cursor.getRow().getString(2);
				All[3] = cursor.getRow().getString(3);
				All[4] = cursor.getRow().getString(4);
				All[5] = cursor.getRow().getString(5);
				All[6] = cursor.getRow().getString(6);
				All[7] = cursor.getRow().getString(7);
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
	
	/**
	 * Checks whether file/database is not empty
	 * @return
	 */
	public boolean CheckEmpty()
	{
		boolean db_Empty = false;
		
		try{
			db_fileconn = (FileConnection)Connector.open("file://" + DBPath + DBName);
			if(db_fileconn.fileSize() > 0)
				db_Empty = false;
			else
				db_Empty = true;
			db_fileconn.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return db_Empty;
	}
	
	/**
	 * Copies database contents onto file-system
	 * @return "true" is successful or "false" on else
	 */
	public boolean MoveDBtoFileSystem()
	{
		boolean copy_success = false;
		try{
			db_fileconn = (FileConnection)Connector.open("file://" + DBPath + DBName);
			// file:///store/ProjectAcropolis/Database/acropolis.db
			//moving acropolis.db to file-system
			if(db_fileconn.exists() && db_fileconn.fileSize() == 0)
			{
				db_filein = getClass().getResourceAsStream( "/" + DBName );
				db_fileout = db_fileconn.openOutputStream();
				byte[] data = new byte[1000];
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
	
}