package codetester.packg;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;

public class PromptApplicationPermissions {

	public PromptApplicationPermissions()
	{
	
		ApplicationPermissionsManager manager = ApplicationPermissionsManager.getInstance();
		
		int LocationPermission = manager.getPermission(ApplicationPermissions.PERMISSION_LOCATION_DATA);
		int MailPermission = manager.getPermission(ApplicationPermissions.PERMISSION_EMAIL);
		
		if (LocationPermission != ApplicationPermissions.VALUE_ALLOW) 
		{
		    ApplicationPermissions locationpermissions = new ApplicationPermissions();
		    locationpermissions.addPermission(ApplicationPermissions.PERMISSION_LOCATION_DATA);
		    manager.invokePermissionsRequest(locationpermissions);
		}
		if(MailPermission != ApplicationPermissions.VALUE_ALLOW)
		{
		    ApplicationPermissions mailpermissions = new ApplicationPermissions();
		    mailpermissions.addPermission(ApplicationPermissions.PERMISSION_EMAIL);
		    manager.invokePermissionsRequest(mailpermissions);
		}

	}
	
	
}
