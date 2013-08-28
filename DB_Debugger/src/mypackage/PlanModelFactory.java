package mypackage;

import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.database.DatabaseIOException;
import net.rim.device.api.database.DatabasePathException;
import net.rim.device.api.database.Statement;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.ControlledAccessException;

public class PlanModelFactory 
{

	URI uri;
	Database db;
	Statement statement;
	Cursor cursor;
	
	public final String path = "file:///store/home/user/acropolis_mobile_plan.db";
	public final String DB_NAME = "acropolis_mobile_plan";
	public final String Create_plan = "CREATE TABLE \'"+ DB_NAME + "\' (" +
			"\'billing_date\' text," +
			"\'minutes\' text," +
			"\'message\' text," +
			"\'data\' text," +
			"\'roam_quota\' text," +
			"\'roam_min\' text," +
			"\'roam_msg\' text," +
			"\'roam_data\' text" +
			")";
	
	public final String Insert_data = "INSERT INTO "+ DB_NAME + " VALUES (" +
			"\'yyyyMMDD\'," +
			"\'0\'," +
			"\'0\'," +
			"\'0\'," +
			"\'false\'," +
			"\'0\'," +
			"\'0\'," +
			"\'0\'" +
			")";
	
	public PlanModelFactory()
	{
		CreateURI();
		CreateDatabase();
		CreateTable();
		InsertData();
		new Logger().LogMessage("dummy plan added in DB");
		CloseDatabase();
	}

	public void CreateURI()
	{
		try {
			uri = URI.create(path);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
	}
	
	public void CreateDatabase()
	{
		try {
			db = DatabaseFactory.openOrCreate(uri);
		} catch (ControlledAccessException e) {
			e.printStackTrace();
		} catch (DatabaseIOException e) {
			e.printStackTrace();
		} catch (DatabasePathException e) {
			e.printStackTrace();
		}
	}
	
	public void CloseDatabase()
	{
		try {
			db.close();
		} catch (DatabaseIOException e) {
			e.printStackTrace();
		}
	}
	
	public void CreateTable()
	{
		try {
			db.beginTransaction();
			statement = db.createStatement(Create_plan);
			statement.prepare();
			statement.execute();
			statement.close();
			db.commitTransaction();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	public void InsertData()
	{
		try{
			db.beginTransaction();
			statement = db.createStatement(Insert_data);
			statement.prepare();
			statement.execute();
			statement.close();
			db.commitTransaction();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	
}

