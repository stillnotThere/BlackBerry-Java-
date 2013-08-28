/*
 * SQLManager.java
 *
 * Copyright © 1998-2010 Research In Motion Ltd.
 * 
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

package com.rim.samples.device.sqlitedemo;

import java.util.*;
import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DataTypeException;
import net.rim.device.api.database.Row;
import net.rim.device.api.database.Statement;
import net.rim.device.api.util.*; 


/**
 * A class to handle SQLite database logic
 */
public class SQLManager
{
    private Database _db;
    
    
    /**
     * Constructs a new SQLManager object
     * @param db <description>
     */
    public SQLManager(Database db)
    {
        _db = db;
    }
    
    
    /**
     * Adds a new category to the Category database table
     * @param name The name of the Category to be added
     * @return A new Category object
     */
    Category addCategory(String name)
    {        
        Category category = null;
        try
        {
            // INSERT a row into the Category table for the new category
            Statement statement = _db.createStatement("INSERT INTO Category VALUES(null, ?)");                     
            statement.prepare();            
            statement.bind(1, name);
            statement.execute(); 
            statement.close(); 
            
            // Query the database for the auto-generated ID of the category just added
            // and create a new Category object.
            statement = _db.createStatement("SELECT category_id FROM Category WHERE category_name = ?"); 
            statement.prepare();
            statement.bind(1, name);                                  
            Cursor cursor = statement.getCursor();                        
            if(cursor.next())
            {
                Row row = cursor.getRow();
                int id = row.getInteger(0);             
                category = new Category(id, name);                                
            }                 
            cursor.close();
            statement.close();
        }
        catch(DatabaseException dbe)
        {
            SQLiteDemo.errorDialog(dbe.toString());
        }    
        catch(DataTypeException dte)
        {
            SQLiteDemo.errorDialog(dte.toString());
        }
        
        return category;
    }
    
    
    /**
     * Adds an item to the Items table in the database
     * @param name The name of the business represented by the directory item to be added
     * @param location The street address of the business represented by the directory item to be added
     * @param phone The phone number for the business represented by the directory item to be added     
     * @param categoryID The category to which the directory item to be added belongs
     * @return The id of the new directory item
     */
    int addItem(String name, String location, String phone, int categoryID)
    {    
        long id = -1;
        
        try
        {
            // Insert a new record in the DirectoryItems table          
            Statement statement = _db.createStatement("INSERT INTO DirectoryItems VALUES(null, ?, ?, ?, ?)"); 
            statement.prepare();                        
            statement.bind(1, categoryID);
            statement.bind(2, name);
            statement.bind(3, location);
            statement.bind(4, phone);                    
            statement.execute();  
            statement.close();
        
            // Retrieve the auto-generated ID of the item just added
            id = _db.lastInsertedRowID();            
        }
        catch(DatabaseException dbe)
        {
            SQLiteDemo.errorDialog(dbe.toString());   
        }         
        
        return (int)id;    
    }
    
    
    /**
     * Updates an existing record in the DirectoryItems table
     * @param id The id of the item to update
     * @param name The text with which to update the name field
     * @param location The text with which to update the location field
     * @param phone The text with which to update the phone field
     */
    void updateItem(int id, String name, String location, String phone)
    {
        try
        {          
            // Update the record in the DirectoryItems table for the given id
            Statement statement = _db.createStatement("UPDATE DirectoryItems SET item_name = ?, location = ?, phone = ? WHERE id = ?"); 
            statement.prepare();                
            statement.bind(1, name);
            statement.bind(2, location);
            statement.bind(3, phone);
            statement.bind(4, id);                    
            statement.execute();                                           
            statement.close();
        }
        catch(DatabaseException dbe)
        {
            SQLiteDemo.errorDialog(dbe.toString());   
        }                
    }
    
    
    /**
     * Deletes a category from the Category table and all corresponding records
     * in the DirectoryItems table.
     * @param id The id of the category to delete
     */
    void deleteCategory(int id)
    {
        try
        {
            // Delete the record in the Category database table
            // corresponding to the highlighted category.
            Statement statement = _db.createStatement("DELETE FROM Category WHERE category_id = ?"); 
            statement.prepare();            
            statement.bind(1, id);
            statement.execute();  
            statement.close();
            
            // Delete all items in the DirectoryItems database
            // table belonging to the highlighted category.            
            statement = _db.createStatement("DELETE FROM DirectoryItems WHERE category_id = ?"); 
            statement.prepare();
            statement.bind(1, id);            
            statement.execute();  
            statement.close();
        }
        catch(DatabaseException dbe)
        {
            SQLiteDemo.errorDialog(dbe.toString());
        }        
    }    

    
    /**
     * Deletes a directory item record from the database
     * @param id The id of the directory item to delete
     */
    void deleteItem(int id)
    {   
        try
        {      
            // Delete the record in the DirectoryItems table for the given id
            Statement statement = _db.createStatement("DELETE FROM DirectoryItems WHERE id = ?");                         
            statement.prepare();            
            statement.bind(1, id);
            statement.execute();  
            statement.close();                
        }
        catch(DatabaseException dbe)
        {
            SQLiteDemo.errorDialog(dbe.toString());
        }        
    }
    
    
    /**
     * Retrieves all records in the Category database table and returns a hash table 
     * of Category objects.
     * @return A hash table of Category objects, one for each record in the Category table
     */
    IntHashtable getCategories()
    {        
        IntHashtable categories = new IntHashtable();
        try
        {
            // Read in all records from the Category table
            Statement statement = _db.createStatement("SELECT * FROM Category"); 
            statement.prepare();
            Cursor cursor = statement.getCursor();              
            
            Row row;
            int id;
            String name;
            Category category;                     
            
            // Iterate through the result set.  For each row, create a new
            // Category object and add it to the hash table. 
            while(cursor.next())
            {
                row = cursor.getRow();
                id = row.getInteger(0);
                name = row.getString(1);                
                category = new Category(id, name);
                categories.put(id, category);                
            }
            statement.close();
            cursor.close();
        }
        catch(DatabaseException dbe)
        {
            SQLiteDemo.errorDialog(dbe.toString());
        }
        catch(DataTypeException dte)
        {
            SQLiteDemo.errorDialog(dte.toString());
        }
        
        return categories;
    }
    
    
    /**
     * Retrieves all records in the DirectoryItems database table and returns
     * a vector of DirectoryItem objects.
     * @return A vector of DirectoryItem objects, one for each record in the DirectoryItem table
     */
    Vector getItems()
    {
        Vector directoryItems = new Vector();
        
        try
        {          
            // Read in all records from the DirectoryItems table
            Statement statement = _db.createStatement("SELECT * FROM DirectoryItems"); 
            statement.prepare();
            Cursor cursor = statement.getCursor();
            
            // Iterate through the the result set.  For each row, add a
            // new DirectoryItem object to the vector.
            while(cursor.next())
            {                    
                Row row = cursor.getRow();
                
                int id = row.getInteger(0);
                int categoryId = row.getInteger(1);
                String name = row.getString(2);
                String location = row.getString(3);
                String phone = row.getString(4);                                                                 
                
                DirectoryItem item = new DirectoryItem(id, name, location, phone, categoryId);
                directoryItems.addElement(item);                                  
            }
            statement.close();
            cursor.close();
        }
        catch(DatabaseException dbe)
        {
            SQLiteDemo.errorDialog(dbe.toString());
        } 
        catch(DataTypeException dte)
        {
            SQLiteDemo.errorDialog(dte.toString());
        }  
        
        return directoryItems;          
    }  
    
    
    /**
     * Closes the database
     */
    void closeDB()
    {        
        try
        {            
            _db.close();
        }
        catch(DatabaseException dbe)
        {
            SQLiteDemo.errorDialog(dbe.toString());
        }
    }  
}
