<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE Rule PUBLIC "sailpoint.dtd" "sailpoint.dtd">
<Rule created="1664331578227" id="0afdf902835b12d4818381e47b735392" language="beanshell" name="Provision Mover Account" type="PostLifecycle">
  <Description>Provision changes for mover account</Description>
  <Signature returnType="Map">
    <Inputs>
      <Argument name="log">
        <Description>
          The log object associated with the SailPointContext.
        </Description>
      </Argument>
      <Argument name="context">
        <Description>
          A sailpoint.api.SailPointContext object that can be used to query the database if necessary.
        </Description>
      </Argument>
    </Inputs>
  </Signature>
  <Source>
    // Library inclusions for BeanShell
    import sailpoint.api.Provisioner;
    import sailpoint.api.IdentityService;
    import sailpoint.api.SailPointContext;
    import sailpoint.object.Application;
    import sailpoint.object.Identity;
    import sailpoint.object.Link;
    import sailpoint.object.ProvisioningPlan;
    import sailpoint.object.ProvisioningPlan.AccountRequest;
    import sailpoint.object.ProvisioningPlan.AttributeRequest;
    import sailpoint.object.ProvisioningPlan.ObjectOperation;
    import sailpoint.object.ProvisioningPlan.Operation;
    import sailpoint.object.ProvisioningProject;
    import java.util.ArrayList;
    import java.util.HashMap;
  
    ProvisioningPlan sfPlan = new ProvisioningPlan();
    Identity identity = context.getObjectByName(Identity.class, identityName);
    sfPlan.setIdentity(identity);

    //Search native identity
    IdentityService idsvc = new IdentityService(context);
    Application app = context.getObject(Application.class, "Google Workspace");
    List links = idsvc.getLinks(identity, app);
    Link link = null;
    if ((null != links) &amp;&amp; !links.isEmpty()) {
      Iterator it = links.iterator();
      if( (null != it) &amp;&amp; (it.hasNext()) ){
        link = (Link) it.next();
      }
    }

    AccountRequest accountRequest = new AccountRequest();
    if(null != link) {
      accountRequest.setNativeIdentity(link.getNativeIdentity());
    }
    accountRequest.setApplication("Google Workspace");
    accountRequest.setOp(ObjectOperation.Modify);

    AttributeRequest organizations = new AttributeRequest();
    organizations.setOp(Operation.Set);
    organizations.setName("organizations");
    organizations.setValue("{\"title\":\"" + identity.getAttribute("jabatan") + "\",\"primary\":true, \"customType\":\"\",\"department\":\"" + identity.getAttribute("unitKerja") + "\",\"description\":\"" + identity.getAttribute("statusKaryawan") + "\",\"costCenter\":\"" + identity.getAttribute("unitOrganisasi") + "\"}");
    accountRequest.add(organizations);
    
    sfPlan.add(accountRequest);

    Provisioner provisioner = new Provisioner(context);
    provisioner.setNoLocking(true);
    ProvisioningProject project = provisioner.compile(sfPlan);
    provisioner.execute(project);
    return;
 </Source>
</Rule>
