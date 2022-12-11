<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE Rule PUBLIC "sailpoint.dtd" "sailpoint.dtd">
<Rule created="1664424604470" id="0afdf902835b12d48183876ff3365f67" language="beanshell" name="Update Inactive Attribute" type="PostLifecycle">
  <Description>Update inactive attribute</Description>
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
  import sailpoint.api.*;
  import sailpoint.object.*;
  import sailpoint.tools.*;
  import java.util.*;
  import java.lang.*;
  import java.text.*;

  Identity identity = context.getObjectByName(Identity.class, identityName);

  //Set Inactive Attribute
  identity.setAttribute("inactive", "true");
  context.saveObject(identity);
  context.commitTransaction();
  context.decache();
  
  return;
  </Source>
</Rule>
