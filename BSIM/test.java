// Library inclusions for BeanShell
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

  //Identity identity = context.getObjectByName(Identity.class, identityName);
  

  // Point this to the "To" email address
  String emailTo = "test2@gws.banksinarmas.com"
  String account = "SP02001";
  if (null == emailTo) {
    log.error("ERROR: could not find destination email");
    return;
  }
  // Specify the email template name in tplName
  String tplName = "Joiner Notification";
  EmailTemplate template = context.getObjectByName(EmailTemplate.class, tplName);
  if (null == template) {
    log.error("ERROR: could not find email template [ " + tplName + "]");
    return;
  }
  template = (EmailTemplate) template.deepCopy(context);
  if (null == template) {
    log.error("ERROR: failed to deepCopy template [ " + tplName + "]");
    return;
  }
  Map args = new HashMap();
  // Add all args needed by the template like this
  args.put("nip", account);
  args.put("name", identity.getFullName());
  args.put("username", account);
  //args.put("password", getAttributeRequestValue(account, "PASSWORD"));
  EmailOptions ops = new EmailOptions(emailTo, args);
  EmailFileAttachment attachment = new EmailFileAttachment("Enable 2FA Instructions.pdf", EmailFileAttachment.MimeType.MIME_PDF, Files.readAllBytes(Paths.get("C:\Sailpoint\Data_HR\data_hr.csv")));
  ops.addAttachment(attachment);
  context.sendEmailNotification(template, ops);
  return;