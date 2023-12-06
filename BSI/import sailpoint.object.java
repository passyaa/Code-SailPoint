import sailpoint.object.*;
  import sailpoint.object.Identity;
  import sailpoint.object.Application;
  import sailpoint.object.Link;
  import sailpoint.object.ProvisioningPlan;
  import sailpoint.object.ProvisioningPlan.ObjectRequest;
  import sailpoint.object.ProvisioningPlan.AccountRequest;
  import sailpoint.object.ProvisioningPlan.AttributeRequest;
  import sailpoint.object.ProvisioningProject;
  import sailpoint.object.ProvisioningResult;
  import sailpoint.api.Provisioner;
  import java.text.DateFormat;
  import java.text.SimpleDateFormat;
  import java.util.Calendar;
  import java.time.temporal.ChronoUnit;

  // Get date Now
  DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
  Date now = new Date();
  //System.out.println("dateNow : " + now);

  List identityList = context.getObjects(Identity.class);
  List identityNameList = new ArrayList();
	
  // Get All Identity
  for (Identity id: identityList) {

    String dateJoin = id.getAttribute("JoinDate");
		
    // Checking null for attribute dates
    if (dateJoin != null){
			
      // Attribute Hire Date is String and become date 
      Date joinDate = dateFormatter.parse(dateJoin);
      // System.out.println("dateJoin : " + joinDate);

      //Comparing Dates
      long diff = now.getTime() - joinDate.getTime();
      long differenceDates = diff / (24 * 60 * 60 * 1000);
      
      //Convert numbers to days
      String dayDifference = Long.toString(differenceDates);
      //System.out.println("dayDifference : " + dayDifference);

      if (dayDifference.equals("15")){

        System.out.println("fullName : " + id.getName());
        
        ProvisioningPlan plan = new ProvisioningPlan();

        plan.setIdentity(id);

        //System.out.println("id : " + id.toXml());
        
        AccountRequest accountRequest = new AccountRequest();

        accountRequest.setApplication("Active Directory");
        
        accountRequest.setNativeIdentity("CN=" + id.getAttribute("AccountDN") + ",OU=Member Users,OU=DANA,DC=dana,DC=local");

        accountRequest.setOperation(AccountRequest.Operation.Modify);

        accountRequest.add(new AttributeRequest("memberOf",ProvisioningPlan.Operation.Remove,"CN=WS1-WallpaperNewJoinner-Group,OU=Testing,OU=DANA,DC=dana,DC=local"));

        plan.add(accountRequest);

        //System.out.println("accountRequest : " + accountRequest.toXml());

        Provisioner p = new Provisioner(context);

        p.setArgument("noCreateTemplates", true);

        p.setArgument("noApplicationTemplates", true);

        p.setArgument("noFiltering", true);

        p.setArgument("requester", "spadmin");

        p.setArgument("Source", "Rule");

        p.setArgument("plan", "LCM Provisioning");

        p.setArgument("Result", "Committed");

        p.setArgument("Channel", "Active Directory");

        //System.out.println("Provisioner : " + plan.toXml());

        ProvisioningProject proj = p.compile(plan);

        p.execute(proj);

        // System.out.println("ProvisioningProject : " + proj.toXml());

      }
    }
  }