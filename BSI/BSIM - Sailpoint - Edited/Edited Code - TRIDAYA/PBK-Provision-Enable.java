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
				if (AccountRequest.Operation.Enable.equals(account.getOperation())) {
					// DELETE Operation 

					System.out.println("Account Request Operation = Enable user on table_users");
					PreparedStatement statement = connection.prepareStatement("UPDATE table_users SET isactive = 1 WHERE username = ?");
					statement.setString(1, (String) account.getNativeIdentity());
					System.out.println("Result Update SQL Role : " + statement);
					System.out.println("Preparing to Disable USER on table_users : " + statementStatus);
					statement.executeUpdate();
					
					PreparedStatement statementLog = connection.prepareStatement("INSERT INTO tbl_log (LogDate,Computer,Activity,Username) VALUES (?,?,?,?)");
					statementLog.setString(1, dateFormatter.format(now));
					statementLog.setString(2, "sailpoint");
					statementLog.setString(3, "Enable user");
					statementLog.setString(4, (String) account.getNativeIdentity());
                    System.out.println("Insert LOG to SQL : " + statementLog);
                    statementLog.executeUpdate();

					
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