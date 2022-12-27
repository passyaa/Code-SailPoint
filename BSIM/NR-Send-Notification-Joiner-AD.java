import sailpoint.api.*;
import sailpoint.object.*;
import sailpoint.tools.*;
import java.util.*;
import java.lang.*;
import java.text.*;
import sailpoint.object.Application;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningPlan.PermissionRequest;
import sailpoint.object.ProvisioningResult;
import sailpoint.object.Schema;

public Object getAttributeRequestValue(AccountRequest acctReq, String attribute) {
    if ( acctReq != null ) {
        AttributeRequest attrReq = acctReq.getAttributeRequest(attribute);
        if ( attrReq != null ) {
            return attrReq.getValue();
        }
    }
    return null;
}

ProvisioningPlan plan = new ProvisioningPlan();

if ( plan != null ) {
    System.out.println("AfterProvisioning Rule - incoming plan:\n" + plan.toXml());

    if ((result != void) &amp;&amp; (null != result)) {
        System.out.println("result:\n" + result.toXml());
    }   

	List accounts = plan.getAccountRequests("Active Directory");
    System.out.println("accounts = " + accounts);

	//  Get all Account Requests out of the plan 
	if ( accounts != null ) {
        for ( AccountRequest account : accounts ) {	
            try {

                // Send Notification Email Create
                // Point this to the "To" email address
                String userPN = getAttributeRequestValue(account, "userPrincipalName");
                String emailTo = "test2@gws.banksinarmas.com";
                String nomerInduk = account.getNativeIdentity();
                String namaLengkap = getAttributeRequestValue(account, "givenName");
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

            } catch (SQLException e) {
                System.out.println("Error: " + e);
			} 
        } 
    }
}

return;