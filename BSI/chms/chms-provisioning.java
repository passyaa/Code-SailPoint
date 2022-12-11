import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.lang.*;
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

// Internal method for grabbing an Attribute Request Value.
public Object getAttributeRequestValue(AccountRequest acctReq, String attribute) {
    if ( acctReq != null ) {
        AttributeRequest attrReq = acctReq.getAttributeRequest(attribute);
        if ( attrReq != null ) {
        return attrReq.getValue();
        }
    }
    return null;
}

// JDBC Provisioning Rule Body
// Handle these cases right now: 
// Account Request Create
// Account Request Modify
// Account Request Delete
// Account Lock/Unlock
// Account Enable/Disable

Date now = new Date();

System.out.println("\n\n\n\n\n");
System.out.println("****************************************");
System.out.println("Entering Provisioning Rule PBK Application");
System.out.println(" Current Time =  " + now.toString());
System.out.println("****************************************");


// The ProvisioningResult is the return object for this type of rule. We'll create it here and then populate it later
ProvisioningResult result = new ProvisioningResult();

// Check if the plan is null or not, if not null, process it...
if ( plan != null ) {
	System.out.println("*** \n The Provisioning Plan being passed in = \n***\n" + plan.toXml() + "\n****************************************");

	List accounts = plan.getAccountRequests();

	//  Get all Account Requests out of the plan 
	if ( ( accounts != null ) && ( accounts.size() > 0 ) ) {

		// If the plan contains one or more account requests, we'll iterate through them
		for ( AccountRequest account : accounts ) {	
			try { 
				// All of the account operations will reside in a try block in case we have any errors, we can mark the provisioningresult as "Failed" if we have an issue.
				if (AccountRequest.Operation.Create.equals(account.getOperation())) {
					// CREATE TASK SYSTEM USER		
					System.out.println("Account Request Operation = CREATE TASK SYSTEM USER");

					PreparedStatement statement = connection.prepareStatement("INSERT INTO [SYSTEM_USER] (USER_ID,USER_GROUP_ID,BRANCH_CODE,USER_NAME,USER_LOGIN,USER_PASSWORD,USER_ISLOGON,USER_ISLOGON_DATE,USER_STATUS,USER_CREATED_BY,USER_CREATED_DATE,USER_CREATED_TIME,ID_UNIT,ID_DIVISION,ID_REGION,EMAIL,BRANCH_ID,NOTIF_EVENT,SOURCE_BANK,LOGIN_TYPE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					statement.setString(1, (String) account.getNativeIdentity());
					statement.setString(3, getAttributeRequestValue(account, "BRANCH_CODE"));
					statement.setString(4, getAttributeRequestValue(account, "USER_NAME"));
					statement.setString(5, getAttributeRequestValue(account, "USER_LOGIN"));
					statement.setString(6, getAttributeRequestValue(account, "USER_PASSWORD"));
					statement.setString(7, getAttributeRequestValue(account, "USER_ISLOGON"));
					statement.setString(8, getAttributeRequestValue(account, "USER_ISLOGON_DATE"));
					statement.setString(9, getAttributeRequestValue(account, "USER_STATUS"));
					statement.setString(10, getAttributeRequestValue(account, "USER_CREATED_BY"));
					statement.setString(11, getAttributeRequestValue(account, "USER_CREATED_DATE"));
					statement.setString(12, getAttributeRequestValue(account, "USER_CREATED_TIME"));
					statement.setString(13, getAttributeRequestValue(account, "ID_UNIT"));
					statement.setString(14, getAttributeRequestValue(account, "ID_DIVISION"));
					statement.setString(15, getAttributeRequestValue(account, "ID_REGION"));
					statement.setString(16, getAttributeRequestValue(account, "EMAIL"));
					statement.setString(17, getAttributeRequestValue(account, "BRANCH_ID"));
					statement.setString(18, getAttributeRequestValue(account, "NOTIF_EVENT"));
					statement.setString(19, getAttributeRequestValue(account, "SOURCE_BANK"));
					statement.setString(20, getAttributeRequestValue(account, "LOGIN_TYPE"));
					
					// Grab the role from the request. If it's a single role, it'll be a string, add it to 
					// the statement, other wise if it's a List, convert to CSV and add it to the statement
					AttributeRequest attrReq = account.getAttributeRequest("USER_GROUP_ID");
					if (attrReq != null) {
						if (attrReq.getValue() instanceof String) {
							statement.setString(2, (String) attrReq.getValue());
						} else if (attrReq.getValue() instanceof List) {
							String listOfRoles = Util.listToCsv((List) attrReq.getValue());
							statement.setString(2, listOfRoles);
						}
					} else {
                        statement.setString(2,"");
                    }
					System.out.println("PREPARING TO EXECUTE TO CREATE TASK SYSTEM USER : " + statement);
					statement.executeUpdate();
						
					// Sucessful Create, so mark result as COMMITTED
					result.setStatus(ProvisioningResult.STATUS_COMMITTED);
					
				} else if (AccountRequest.Operation.Modify.equals(account.getOperation())) {
					// MODIFY Operation
					// We have a modify, this one is trickier, as we can have "Add" and "Remove" 
					// operations and each can be a single string value or a list

					// Determine what the current roles are first...  
					System.out.println("Account Request Operation = Modify : " + account.getOperation());
					Statement curr_stmt = connection.createStatement();
					ResultSet rs = curr_stmt .executeQuery("SELECT OFFICER_TAB.OFFICER_ID,WFU_ID,BRANCH,FIRST_NAME,EMAIL_ADDR,DATE_CREATED,CREATED_BY,LAST_PASSWORD_CHANGE,COMPANY_CODE,additional_branch,NoAccount,WFU_ID_OLD,SUB_BRANCH,TRIAL_COUNT,STATUS,IN_USED,LAST_ACTIVITY FROM OFFICER_TAB LEFT JOIN OFFICER_STATUS_TAB ON OFFICER_TAB.OFFICER_ID = OFFICER_STATUS_TAB.OFFICER_ID WHERE OFFICER_TAB.OFFICER_ID = '" + account.getNativeIdentity() + "'");

					//  Check result set. Should only be one row since login is a unique key for the table
					List current_roles = null;
					String roles = "";
          			String branch = "";

					while (rs.next()) {
						roles = roles + "," + rs.getString("WFU_ID");
          				branch = rs.getString("BRANCH");
            			System.out.println("roles : " + rs.getString("WFU_ID"));
						System.out.println("branch : " + rs.getString("BRANCH"));
					} 
					current_roles = Util.csvToList(roles,true);
					System.out.println("current_roles : " + current_roles);
          
					if (current_roles == null) {
						System.out.println("We have a null current_roles list... change it to an empty list for subsequent processing.");
						current_roles = new ArrayList();
					}
					System.out.prinutln("Current Roles for User = " + Util.listToCsv(current_roles));

					// Get all Attribute Requests and pull out just the role ones. 										
					List remove_roles = new ArrayList();
					List add_roles = new ArrayList(); 

					// Get all attribute requests and then we will filter for those related to the roles column
					List mod_attr_requests = account.getAttributeRequests();
					System.out.println("mod_attr_requests = " + mod_attr_requests);
          
					if (mod_attr_requests != null) {
						for (AttributeRequest req : mod_attr_requests ) {
								if (req.getName().equals("WFU_ID")) {
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

					//  We now have a calculated list of the roles we are adding, the roles we are removing, and the current roles for the user.
					System.out.println("Add Roles = " + Util.listToCsv(add_roles));
					System.out.println("Remove Roles = " + Util.listToCsv(remove_roles));

					// If we have roles to remove, remove them
					if (!remove_roles.isEmpty()) {
						System.out.println("About to remove roles: " + remove_roles.toString() + " from the current_roles = " + current_roles.toString());
						current_roles.removeAll(remove_roles);
					}

					// If we have roles to add, check if they are there and add them as we iterate through
					if (!add_roles.isEmpty()) {
						System.out.println("About to add roles: " + add_roles.toString() + " to the current_roles = " + current_roles.toString());
						for (Object item: add_roles) {
							 if (!current_roles.contains(item)) {
									   current_roles.add(item);
							 }
						}
					}
					
					//  Print out the list of roles being provisioned after processing "add" and "remove" operations
					System.out.println("Updating the roles for : " + (String) account.getNativeIdentity() + " Current Roles after adding/removing = " + Util.listToCsv(current_roles)); 

					// Process update SQL operation
					System.out.println("Preparing to Update user from OFFICER_TAB WFU_ID  : " + Util.listToCsv(current_roles));
					PreparedStatement statement = connection.prepareStatement("UPDATE OFFICER_TAB SET WFU_ID = ? WHERE OFFICER_ID = ?");
					statement.setString(1, Util.listToCsv(current_roles));
					statement.setString(2, (String) account.getNativeIdentity());
					System.out.println("Preparing to UPDATE user from OFFICER_TAB WFU_ID : " + statement);
					statement.executeUpdate();
          
          			String branchFromSailpoint = getAttributeRequestValue(account, "BRANCH");
					System.out.println("branchFromSailpoint : " + branchFromSailpoint);
					
					if (branchFromSailpoint != null) {
						System.out.println("Preparing to Update user from OFFICER_TAB branchFromSailpoint  : " + getAttributeRequestValue(account, "BRANCH"));
						PreparedStatement statementBranch = connection.prepareStatement("UPDATE OFFICER_TAB SET BRANCH = ? WHERE OFFICER_ID = ?");
						statementBranch.setString(1, getAttributeRequestValue(account, "BRANCH"));
						statementBranch.setString(2, (String) account.getNativeIdentity());
						System.out.println("Preparing to UPDATE user from OFFICER_TAB BRANCH : " + statementBranch);
						statementBranch.executeUpdate();
					}	else {
						System.out.println("Preparing to Update user from OFFICER_TAB branchFromDB  : " + branch);
						PreparedStatement statementBranch = connection.prepareStatement("UPDATE OFFICER_TAB SET BRANCH = ? WHERE OFFICER_ID = ?");
						statementBranch.setString(1, branch);
						statementBranch.setString(2, (String) account.getNativeIdentity());
						System.out.println("Preparing to UPDATE user from OFFICER_TAB BRANCH : " + statementBranch);
						statementBranch.executeUpdate();
					}

					result.setStatus(ProvisioningResult.STATUS_COMMITTED);

				} else if (AccountRequest.Operation.Delete.equals(account.getOperation())) {
					// DELETE Operation 
					System.out.println("Account Request Operation = Delete OFFICER_STATUS_TAB");
					PreparedStatement statementStatus = connection.prepareStatement("delete from OFFICER_STATUS_TAB WHERE OFFICER_ID = ?");
					statementStatus.setString(1, (String) account.getNativeIdentity());
					System.out.println("Preparing to DELETE user from OFFICER_STATUS_TAB : " + statementStatus);
					statementStatus.executeUpdate();
          
          			System.out.println("Account Request Operation = Delete OFFICER_TAB");
					PreparedStatement statement = connection.prepareStatement("delete from OFFICER_TAB WHERE OFFICER_ID = ?");
					statement.setString(1, (String) account.getNativeIdentity());
					System.out.println("Preparing to DELETE user from OFFICER_TAB : " + statement);
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
System.out.println("Exiting Provisioning Rule for PBK. \n  Result=  \n" + result.toXml(false));
System.out.println("****************************************");
System.out.println("****************************************");
System.out.println("\n\n\n\n\n");

plan.setResult(result);
		
return result;