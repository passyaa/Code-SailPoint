import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import sailpoint.api.SailPointContext;
import sailpoint.connector.JDBCConnector;
import sailpoint.object.Application;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningPlan.PermissionRequest;
import sailpoint.object.ProvisioningResult;
import sailpoint.object.Schema;
import sailpoint.tools.xml.XMLObjectFactory;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import sailpoint.tools.Util;

		// oke masuk
		// Internal method for grabbing an Attribute Request Value.
		//
		
	   public Object getAttributeRequestValue(AccountRequest acctReq, String attribute) {
	    	if ( acctReq != null ) {
	    	  AttributeRequest attrReq = acctReq.getAttributeRequest(attribute);
	    	  if ( attrReq != null ) {
	    	    return attrReq.getValue();
	    	  }
	    	}
	    	return null;
	    }

//
// JDBC Provisioning Rule Body
//
// We will handle these cases right now: 
// 
// Account Request Create
// Account Request Modify
// Account Request Delete
// Account Lock/Unlock
// Account Enable/Disable
//
	
Date now = new Date();

System.out.println("\n\n\n\n\n");
System.out.println("****************************************");
System.out.println("Entering Provisioning Rule for Bug Tracking");
System.out.println(" Current Time =  " + now.toString());
System.out.println("****************************************");


//
// The ProvisioningResult is the return object for this type of rule. We'll create it here and then populate it later
//

ProvisioningResult result = new ProvisioningResult();


//
// Check if the plan is null or not, if not null, process it... 
//

if ( plan != null ) {

	System.out.println("*** \n The Provisioning Plan being passed in = \n***\n" + plan.toXml() + "\n****************************************");

	List accounts = plan.getAccountRequests();
	
	// 
	//  Get all Account Requests out of the plan
	// 

	if ( ( accounts != null ) && ( accounts.size() > 0 ) ) {
	
		//
		// If the plan contains one or more account requests, we'll iterate through them
		//
		
		for ( AccountRequest account : accounts ) {
			
			try {
			
				// 
				// All of the account operations will reside in a try block in case we have any errors, we can mark the provisioningresult as "Failed" if we have an issue.
				//

				if (AccountRequest.Operation.Create.equals(account.getOperation())) {

					//
					// CREATE Operation
					// 				
				
					System.out.println("Account Request Operation = Create");

					PreparedStatement statement = connection.prepareStatement("insert into users (id,firstname,lastname,capability,status,locked,username) values (?,?,?,?,?,?,?)");
					statement.setString(1, (String) account.getNativeIdentity());
					statement.setString(2, getAttributeRequestValue(account, "firstname"));
					statement.setString(3, getAttributeRequestValue(account, "lastname"));
					statement.setString(5, getAttributeRequestValue(account, "status"));
					statement.setString(6, getAttributeRequestValue(account, "locked"));
					statement.setString(7, getAttributeRequestValue(account, "username"));
					 //
					 // Grab the role from the request. If it's a single role, it'll be a string, add it to 
					 // the statement, other wise if it's a List, convert to CSV and add it to the statement
					 //
					 AttributeRequest attrReq = account.getAttributeRequest("capability");
					 if (attrReq != null) {
								if (attrReq.getValue() instanceof String) {
									statement.setString(4, (String) attrReq.getValue());
								} else if (attrReq.getValue() instanceof List) {
									String listOfRoles = Util.listToCsv((List) attrReq.getValue());
									statement.setString(4, listOfRoles);
								}
					 } else {
                                                               statement.setString(4,"");
                                         }

					System.out.println("Preparing to execute: " + statement);
					statement.executeUpdate();
						
					//
					// Sucessful Create, so mark result as COMMITTED
					//
					
					result.setStatus(ProvisioningResult.STATUS_COMMITTED);
					

				} else if (AccountRequest.Operation.Modify.equals(account.getOperation())) {
				
					//
					// MODIFY Operation
					// 
				
					//
					// We have a modify, this one is trickier, as we can have "Add" and "Remove" 
					// operations and each can be a single string value or a list
					//

					System.out.println("Account Request Operation = Modify");

					//
					// Determine what the current roles are first... 
					//

					Statement curr_stmt = connection.createStatement();
					ResultSet rs = curr_stmt .executeQuery("select * from users where id = '" + account.getNativeIdentity() + "'");

					//
					//  Check result set. Should only be one row since id is a unique key for the table
					//
					List current_roles = null;
					String roles = "";

					while (rs.next()) {
					   roles = roles + "," + rs.getString("capability");
					} 
					current_roles = Util.csvToList(roles,true);
					
					if (current_roles == null) {
						System.out.println("We have a null current_roles list... change it to an empty list for subsequent processing.");
						current_roles = new ArrayList();
					}

					System.out.println("Current Roles for User = " + Util.listToCsv(current_roles));

					//
					// Get all Attribute Requests and pull out just the role ones. 
					//
										
					List remove_roles = new ArrayList();
					List add_roles = new ArrayList(); 
					//
					// Get all attribute requests and then we will filter for those related to the roles column
					//
					List mod_attr_requests = account.getAttributeRequests();
							
					if (mod_attr_requests != null) {
						for (AttributeRequest req : mod_attr_requests ) {
								if (req.getName().equals("capability")) {
								   if (ProvisioningPlan.Operation.Remove.equals(req.getOperation())) {
										// Process Removes First
										if (req.getValue() instanceof String) {
												  remove_roles = Util.csvToList(req.getValue());
										} else if (req.getValue() instanceof List) {
												  remove_roles = req.getValue();                
										}
									} else if (ProvisioningPlan.Operation.Add.equals(req.getOperation())) {
										// Process Adds Second
										if (req.getValue() instanceof String) {
												   add_roles = Util.csvToList(req.getValue());
										} else if (req.getValue() instanceof List) {
												  add_roles = req.getValue();                     
										}
									} 

							   } 
						}
					}
					//
					//  We now have a calculated list of the roles we are adding, the roles we are removing, and the current roles for the user.
					//
					System.out.println("Add Roles = " + Util.listToCsv(add_roles));
					System.out.println("Remove Roles = " + Util.listToCsv(remove_roles));


					//
					// If we have roles to remove, remove them
					//
					if (!remove_roles.isEmpty()) {
						System.out.println("About to remove roles: " + remove_roles.toString() + "from the current_roles = " + current_roles.toString());
						current_roles.removeAll(remove_roles);
					}
					//
					// If we have roles to add, check if they are there and add them as we iterate through
					//
					
					if (!add_roles.isEmpty()) {
						System.out.println("About to add roles: " + add_roles.toString() + " to the current_roles = " + current_roles.toString());
						for (Object item: add_roles) {
							 if (!current_roles.contains(item)) {
									   current_roles.add(item);
							 }
						}
					}
					
					//
					//  Print out the list of roles being provisioned after processing "add" and "remove" operations
					//
					System.out.println("Updating the roles for:" + (String) account.getNativeIdentity() + " Current Roles after adding/removing = " + Util.listToCsv(current_roles)); 

					//
					// Process update SQL operation
					//

					PreparedStatement statement = connection.prepareStatement("update users set capability = ? where id = ?");
					statement.setString(2, (String) account.getNativeIdentity());
					statement.setString(1,Util.listToCsv(current_roles)); 
					statement.executeUpdate();

					// Add these in the future.
					// statement.setString ( 2,
					// getAttributeRequestValue(account,"first") );
					// statement.setString ( 3,
					// getAttributeRequestValue(account,"last") );
					// statement.setString ( 4,
					// getAttributeRequestValue(account,"capability") );
					// statement.setString ( 5,
					// getAttributeRequestValue(account,"status") );

					result.setStatus(ProvisioningResult.STATUS_COMMITTED);

				} else if (AccountRequest.Operation.Delete.equals(account.getOperation())) {
				
					//
					// DELETE Operation
					// 


					System.out.println("Account Request Operation = Delete");
					PreparedStatement statement = connection.prepareStatement("delete from users where id = ?");
					statement.setString(1, (String) account.getNativeIdentity());
					statement.executeUpdate();

					result.setStatus(ProvisioningResult.STATUS_COMMITTED);

				} else if (AccountRequest.Operation.Disable.equals(account.getOperation())) {

System.out.println("Account Request Operation = Disable");
	PreparedStatement statement = connection.prepareStatement("update users set status = 'I' where id = ?");
					statement.setString(1, (String) account.getNativeIdentity());
					statement.executeUpdate();
					result.setStatus(ProvisioningResult.STATUS_COMMITTED);

				} else if (AccountRequest.Operation.Enable.equals(account.getOperation())) {

System.out.println("Account Request Operation = Enable");
	PreparedStatement statement = connection.prepareStatement("update users set status = 'A' where id = ?");
					statement.setString(1, (String) account.getNativeIdentity());
					statement.executeUpdate();
					result.setStatus(ProvisioningResult.STATUS_COMMITTED);


				} else if (AccountRequest.Operation.Lock.equals(account.getOperation())) {

System.out.println("Account Request Operation = Lock");
	PreparedStatement statement = connection.prepareStatement("update users set locked = 'Y' where id = ?");
					statement.setString(1, (String) account.getNativeIdentity());
					statement.executeUpdate();
					result.setStatus(ProvisioningResult.STATUS_COMMITTED);


				} else if (AccountRequest.Operation.Unlock.equals(account.getOperation())) {

System.out.println("Account Request Operation = Unlock");
	PreparedStatement statement = connection.prepareStatement("update users set locked = 'N' where id = ?");
					statement.setString(1, (String) account.getNativeIdentity());
					statement.executeUpdate();
					result.setStatus(ProvisioningResult.STATUS_COMMITTED);

				} else {

					// Unknown operation!
					System.out.println("Unknown operation ["
							+ account.getOperation() + "]!");
				}

			} catch (SQLException e) {
                        System.out.println("Error: " + e);
                        result.setStatus(ProvisioningResult.STATUS_FAILED);
                        result.addError(e);
			} 
		}    // account request loop
	}     // if account requests exist
}   // if plan not null


System.out.println("****************************************");
System.out.println("****************************************");
System.out.println("Exiting Provisioning Rule for Bug Tracking. \n  Result=  \n" + result.toXml(false));
System.out.println("****************************************");
System.out.println("****************************************");
System.out.println("\n\n\n\n\n");

plan.setResult(result);

return result;