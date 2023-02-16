import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import sailpoint.api.*;
import sailpoint.object.*;
import sailpoint.tools.*;
import java.util.*;
import java.lang.*;
import java.text.*;
import java.security.SecureRandom;
import java.security.*;
import java.math.*;
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
import sailpoint.object.Identity;

System.out.println("*** \n The Provisioning Plan PEGA - After = \n***\n" + plan.toXml() + "\n****************************************");

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

// Check if the plan is null or not, if not null, process it...
if ( plan != null ) {
	//System.out.println("*** \n The Provisioning Plan being passed in = \n***\n" + plan.toXml() + "\n****************************************");

	List accounts = plan.getAccountRequests();
        //System.out.println("accounts == " + accounts);

	//  Get all Account Requests out of the plan 
	if (accounts != null) {
		// If the plan contains one or more account requests, we'll iterate through them
		for ( AccountRequest account : accounts ) {	
			try { 
				// All of the account operations will reside in a try block in case we have any errors, we can mark the provisioningresult as "Failed" if we have an issue.
				if (AccountRequest.Operation.Create.equals(account.getOperation())) {	
					System.out.println("Account Request Create");

          			// Send Notification Email Create
					// Point this to the "To" email address

					//Identity identity=context.getObject(Identity.class,identityName);
					//Identity manager=identity.getManager();
					// Identity emailManager=manager.getAttribute("email");
					// System.out.println("manager >>>> " + manager);
					// System.out.println("emailManager >>>> " + emailManager);
					

					String upn = getAttributeRequestValue(account, "userPrincipalName");
					System.out.println("upn >>>> " + upn);
					//String emailTo = upn.toLowerCase();
					String emailTo = getAttributeRequestValue(account, "emailManager");
					System.out.println("emailTo >>>> " + emailTo);
					String nomerInduk = getAttributeRequestValue(account, "sAMAccountName");
					String gn = getAttributeRequestValue(account, "givenName");
					String sn = getAttributeRequestValue(account, "sn");
					String namaLengkap = String.join(" ",gn,sn);
					String pwd = getAttributeRequestValue(account, "password");
					System.out.println("emailTo : " + emailTo);
					System.out.println("nomerInduk : " + nomerInduk);
					System.out.println("namaLengkap : " + namaLengkap);
					System.out.println("password : " + pwd);
					
					if (null == emailTo) {
						System.out.println("ERROR: could not find destination email");
						return;
					}
					// Specify the email template name in tplName
					String tplName = "Joiner Notification AD";
					EmailTemplate template = context.getObjectByName(EmailTemplate.class, tplName);
					if (null == template) {
						System.out.println("ERROR: could not find email template [ " + tplName + "]");
						return;
					}
					template = (EmailTemplate) template.deepCopy(context);
					if (null == template) {
						System.out.println("ERROR: failed to deepCopy template [ " + tplName + "]");
						return;
					}

					// Add all args needed by the template like this
					Map args = new HashMap();
					args.put("nip", nomerInduk);
					args.put("name", namaLengkap);
					args.put("username", nomerInduk);
					args.put("pwd", pwd);
          			EmailOptions ops = new EmailOptions(emailTo, args);
					context.sendEmailNotification(template, ops);
					
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

return;