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
                if (AccountRequest.Operation.Modify.equals(account.getOperation())) {
                    // MODIFY Operation
                    // We have a modify, this one is trickier, as we can have "Add" and "Remove" 
                    // operations and each can be a single string value or a list

                    // Determine what the current roles are first...  
                    System.out.println("Account Request Operation = Modify : " + account.getOperation());
                    Statement curr_stmt = connection.createStatement();
                    ResultSet rs = curr_stmt .executeQuery("SELECT OFFICER_TAB.OFFICER_ID,WFU_ID,BRANCH,FIRST_NAME,EMAIL_ADDR,DATE_CREATED,CREATED_BY,LAST_PASSWORD_CHANGE,COMPANY_CODE,additional_branch,NoAccount,WFU_ID_OLD,SUB_BRANCH,TRIAL_COUNT,STATUS,IN_USED,LAST_ACTIVITY FROM OFFICER_TAB LEFT JOIN OFFICER_STATUS_TAB ON OFFICER_TAB.OFFICER_ID = OFFICER_STATUS_TAB.OFFICER_ID WHERE OFFICER_TAB.OFFICER_ID = '" + account.getNativeIdentity() + "'");

                    //  Check result set. Should only be one row since login is a unique key for the table
                    String roles = "";
                    String branch = "";

                    while (rs.next()) {
                        roles = rs.getString("WFU_ID");
                        branch = rs.getString("BRANCH");
                        System.out.println("roles : " + roles);
                        System.out.println("branch : " + branch);
                    }

                    List currentRoles = Util.csvToList(roles,true);
                    System.out.println("currentRoles : " + currentRoles);
        
                    if (currentRoles == null) {
                        System.out.println("We have a null currentRoles list... change it to an empty list for subsequent processing.");
                        currentRoles = new ArrayList();
                    }

                    // Get all Attribute Requests and pull out just the role ones. 										
                    List removeRoles = new ArrayList();
                    List addRoles = new ArrayList(); 

                    // Get all attribute requests and then we will filter for those related to the roles column
                    List mod_attr_requests = account.getAttributeRequests();

                    if (mod_attr_requests != null) {
                        for (AttributeRequest req : mod_attr_requests ) {
                                if (req.getName().equals("WFU_ID")) {
                                    if (ProvisioningPlan.Operation.Remove.equals(req.getOperation())) {
                                        // Process Removes First
                                        if (req.getValue() instanceof String) {
                                            removeRoles = Util.csvToList(req.getValue());
                                            System.out.println("removeRoles instanceof String = " + removeRoles);
                                        } else if (req.getValue() instanceof List) {
                                            removeRoles = req.getValue();
                                            System.out.println("removeRoles instanceof List = " + removeRoles);                
                                        }
                                    } else if (ProvisioningPlan.Operation.Add.equals(req.getOperation())) {
                                        // Process Adds Second
                                        if (req.getValue() instanceof String) {
                                            addRoles = Util.csvToList(req.getValue());
                                            System.out.println("addRoles instanceof String = " + addRoles);
                                        } else if (req.getValue() instanceof List) {
                                            addRoles = req.getValue();
                                            System.out.println("addRoles instanceof List = " + addRoles);                 
                                        }
                                    } 

                            } 
                        }
                    }

                    //  We now have a calculated list of the roles we are adding, the roles we are removing, and the current roles for the user.
                    System.out.println("Add Roles = " + Util.listToCsv(addRoles));
                    System.out.println("Remove Roles = " + Util.listToCsv(removeRoles));

                    // If we have roles to remove, remove them
                    if (!removeRoles.isEmpty()) {
                        System.out.println("About to remove roles: " + removeRoles.toString() + " from the currentRoles = " + currentRoles.toString());
                        currentRoles.removeAll(removeRoles);
                    }

                    // If we have roles to add, check if they are there and add them as we iterate through
                    if (!addRoles.isEmpty()) {
                        System.out.println("About to add roles : " + addRoles.toString() + " and remove current roles first : " + currentRoles.toString() + " and become roles : " + addRoles.toString());
                        
                        currentRoles.clear();
                        System.out.println("After Clear currentRoles : " + Util.listToCsv(currentRoles));

                        for (Object item : addRoles) {
                            currentRoles.add(item);
                            System.out.println("After Add Roles : " + Util.listToCsv(currentRoles));
                        }

                    }
                    
                    //  Print out the list of roles being provisioned after processing "add" and "remove" operations
                    System.out.println("Updating the roles for : " + (String) account.getNativeIdentity() + " Current Roles after adding/removing = " + Util.listToCsv(currentRoles)); 

                    // Process update SQL operation
                    // System.out.println("Preparing to Update user from OFFICER_TAB WFU_ID 1 : " + Util.listToCsv(currentRoles));
                    // System.out.println("Preparing to Update user from OFFICER_TAB WFU_ID 2 : " + Util.listToCsv(currentRoles).getClass().getSimpleName());
                    
                    // System.out.println("Preparing to Update user from OFFICER_TAB WFU_ID 3 : " + currentRoles);    	
                    // System.out.println("Preparing to Update user from OFFICER_TAB WFU_ID 4 : " + currentRoles.getClass().getSimpleName());

                    // Process update SQL Role
                    PreparedStatement statement = connection.prepareStatement("UPDATE OFFICER_TAB SET WFU_ID = ? WHERE OFFICER_ID = ?");
                    statement.setString(1, Util.listToCsv(currentRoles));
                    statement.setString(2, (String) account.getNativeIdentity());
                    System.out.println("Result Update SQL Role : " + statement);
                    statement.executeUpdate();

                    // Process update SQL Branch
                    String branchFromSailpoint = getAttributeRequestValue(account, "BRANCH");
                    System.out.println("branchFromSailpoint : " + branchFromSailpoint);
                    
                    if (branchFromSailpoint != null) {
                        System.out.println("Preparing Update Branch Baru : " + getAttributeRequestValue(account, "BRANCH"));
                        PreparedStatement statementBranch = connection.prepareStatement("UPDATE OFFICER_TAB SET BRANCH = ? WHERE OFFICER_ID = ?");
                        statementBranch.setString(1, getAttributeRequestValue(account, "BRANCH"));
                        statementBranch.setString(2, (String) account.getNativeIdentity());
                        System.out.println("Result Update SQL Branch Baru : " + statementBranch);
                        statementBranch.executeUpdate();
                    } else {
                        System.out.println("Preparing Update Branch Lama : " + branch);
                        PreparedStatement statementBranch = connection.prepareStatement("UPDATE OFFICER_TAB SET BRANCH = ? WHERE OFFICER_ID = ?");
                        statementBranch.setString(1, branch);
                        statementBranch.setString(2, (String) account.getNativeIdentity());
                        System.out.println("Result Update SQL Branch Lama : " + statementBranch);
                        statementBranch.executeUpdate();
                    }

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
        } // account request loop

    }   // if account requests exist

}   // if plan not null



System.out.println("****************************************");
System.out.println("****************************************");
System.out.println("Exiting Provisioning Rule for PBK. \n  Result=  \n" + result.toXml(false));
System.out.println("****************************************");
System.out.println("****************************************");
System.out.println("\n\n\n\n\n");

plan.setResult(result);
    
return result;