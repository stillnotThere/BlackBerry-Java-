package com.app.project.acropolis.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.DataTypeException;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.database.DatabaseIOException;
import net.rim.device.api.database.DatabasePathException;
import net.rim.device.api.database.Row;
import net.rim.device.api.database.Statement;
import net.rim.device.api.io.IDNAException;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.ControlledAccessException;

public class ModelFactory {

	public final String DB_NAME = "acropolis.db";
	public final String SCHEMA = "activity_acropolis";
	
	public Database db;
	public String path = "file:///SDCard/Acropolis/database/acropolis.db";
	public URI db_URI = null;
	
	public String update_query = "update activity_acropolis set ";
//	public String update_query = "update activity_acropolis set sent=\'4\'";
	public String select_query = "select ";
	
	public ModelFactory()
	{
		new DBLogger().LogMessage(">>-ModelFactory-<<");
	}
	
	/**
	 * Update db with specified values
	 * @param column
	 * @param data
	 */
	public void UpdateData(String column,String data)
	{
		OpenDB();
		try{
			db.beginTransaction();
			Statement st_update = db.createStatement(update_query + column + " = \'" + data + "\'");
//			Statement st_update = db.createStatement(update_query);
			st_update.prepare();
			st_update.execute();
			st_update.close();
			db.commitTransaction();
		} catch (DatabaseException e) {
			e.printStackTrace();
			new DBLogger().LogMessage("DatabaseException:"+e.getClass()+"::"+e.getMessage());
		}
		new DBLogger().LogMessage("DB updated");
		CloseDB();
	}
	
	public String SelectData(String column)
	{
		OpenDB();
		String collected = "";
		int colIndex = 0;
		try{
			if(column.equalsIgnoreCase("phone_number"))
				colIndex = 0;
			if(column.equalsIgnoreCase("roaming"))
				colIndex = 3;
			if(column.equalsIgnoreCase("fix_ack"))
				colIndex = 4;
			if(column.equalsIgnoreCase("lat"))
				colIndex = 5;
			if(column.equalsIgnoreCase("lng"))
				colIndex = 6;
			if(column.equalsIgnoreCase("acc"))
				colIndex = 7;
			if(column.equalsIgnoreCase("incoming"))
				colIndex = 8;
			if(column.equalsIgnoreCase("outgoing"))
				colIndex = 9;
			if(column.equalsIgnoreCase("received"))
				colIndex = 10;
			if(column.equalsIgnoreCase("sent"))
				colIndex = 11;
			if(column.equalsIgnoreCase("downloaded"))
				colIndex = 12;
			if(column.equalsIgnoreCase("uploaded"))
				colIndex = 13;
			
			db.beginTransaction();
			Statement st_select = db.createStatement(select_query + column +" from activity_acropolis");
			st_select.prepare();
//			st_select.execute();
			Cursor cursor = st_select.getCursor();
			while(cursor.next())
			{
				collected = cursor.getRow().getString(colIndex);
			}
			st_select.close();
			cursor.close();
			db.commitTransaction();
		} catch (DatabaseException e) {
			e.printStackTrace();
			new DBLogger().LogMessage("DatabaseException:"+e.getClass()+"::"+e.getMessage());
		} catch(DataTypeException e) {
			e.printStackTrace();
			new DBLogger().LogMessage("DataTypeException:"+e.getClass()+"::"+e.getMessage());
		}
		new DBLogger().LogMessage("selected data" + collected);
		CloseDB();
		return collected;
	}
	
	public void OpenDB()
	{
		try {
			db_URI = URI.create(path);
			db = DatabaseFactory.open(db_URI);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (ControlledAccessException e) {
			e.printStackTrace();
		} catch (DatabaseIOException e) {
			e.printStackTrace();
		} catch (DatabasePathException e) {
			e.printStackTrace();
		}
	}

	public void CloseDB()
	{
		try {
			db.close();
		} catch (DatabaseIOException e) {
			e.printStackTrace();
		}
	}
	
}
