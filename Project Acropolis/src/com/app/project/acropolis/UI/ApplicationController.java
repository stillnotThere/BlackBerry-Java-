package com.app.project.acropolis.UI;

/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 */
public class ApplicationController extends Thread 
{
	public boolean Application_Stop = false;
	
	/**
	 *	Application_Controller allows the "current executing application" to
	 *	wait() or notifyAll()
	 *	IMPORTANT --->>> ApplicatiionController has to
	 *					 be called as separate instances for "Start" or "Stop"  
	 * @param haltapp
	 */
	public ApplicationController(boolean haltapp)
	{
		Application_Stop = haltapp;
	}
	
	public void run()
	{
		if(Application_Stop)
		{
			try {
				LocationApplication.getApplication().wait();				//stop the application
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else
		{
			LocationApplication.getApplication().notifyAll();				//start the application
		}
	}
	
}
