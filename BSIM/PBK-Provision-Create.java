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
					// CREATE Operation	OFFICER_TAB		
					System.out.println("Account Request Operation = Create OFFICER_TAB");

					PreparedStatement statement = connection.prepareStatement("INSERT INTO OFFICER_TAB (OFFICER_ID,WFU_ID,BRANCH,PASSWORD,FIRST_NAME,EMAIL_ADDR,DATE_CREATED,CREATED_BY,additional_branch,NoAccount) VALUES (?,?,?,?,?,?,?,?,?,?)");
					statement.setString(1, (String) account.getNativeIdentity());
					statement.setString(3, getAttributeRequestValue(account, "BRANCH"));
					statement.setString(4, getAttributeRequestValue(account, "PASSWORD"));
					statement.setString(5, getAttributeRequestValue(account, "FIRST_NAME"));
					statement.setString(6, getAttributeRequestValue(account, "EMAIL_ADDR"));
					statement.setString(7, getAttributeRequestValue(account, "DATE_CREATED"));
					statement.setString(8, getAttributeRequestValue(account, "CREATED_BY"));
					statement.setString(9, getAttributeRequestValue(account, "additional_branch"));
					statement.setString(10, getAttributeRequestValue(account, "NoAccount"));
					
					// Grab the role from the request. If it's a single role, it'll be a string, add it to 
					// the statement, other wise if it's a List, convert to CSV and add it to the statement
					AttributeRequest attrReq = account.getAttributeRequest("WFU_ID");
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
					System.out.println("Preparing to execute OFFICER_TAB : " + statement);
					statement.executeUpdate();

					// CREATE Operation	OFFICER_STATUS_TAB
					System.out.println("Account Request Operation = Create OFFICER_STATUS_TAB");

					PreparedStatement statementStatus = connection.prepareStatement("INSERT INTO OFFICER_STATUS_TAB (OFFICER_ID,TRIAL_COUNT,STATUS,IN_USED) VALUES (?,?,?,?)");
					statementStatus.setString(1, (String) account.getNativeIdentity());
					statementStatus.setString(2, getAttributeRequestValue(account, "TRIAL_COUNT"));
					statementStatus.setString(3, getAttributeRequestValue(account, "STATUS"));
					statementStatus.setString(4, getAttributeRequestValue(account, "IN_USED"));
					
					System.out.println("Preparing to execute OFFICER_STATUS_TAB : " + statementStatus);
					statementStatus.executeUpdate();
						
					// Sucessful Create, so mark result as COMMITTED
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