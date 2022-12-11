<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE Rule PUBLIC "sailpoint.dtd" "sailpoint.dtd">
<Rule created="1663697608753" id="0afdf902835b12d481835c1ae0310126" language="beanshell" modified="1664951657868" name="TRNG-provisioningRule" type="JDBCProvision">
  <Description>This rule is used by the JDBC connector to do provisioning of the data .</Description>
  <Signature returnType="ProvisioningResult">
    <Inputs>
      <Argument name="log" type="org.apache.commons.logging.Log">
        <Description>
          The log object associated with the SailPointContext.
        </Description>
      </Argument>
      <Argument name="context" type="sailpoint.api.SailPointContext">
        <Description>
          A sailpoint.api.SailPointContext object that can be used to query the database if necessary.
        </Description>
      </Argument>
      <Argument name="application">
        <Description>
                The application whose data file is being processed.
                </Description>
      </Argument>
      <Argument name="schema">
        <Description>
                The Schema currently in use.
                </Description>
      </Argument>
      <Argument name="connection">
        <Description>
                A connection object to connect to database.
                </Description>
      </Argument>
      <Argument name="plan">
        <Description>
                The ProvisioningPlan created against the JDBC application.
                </Description>
      </Argument>
    </Inputs>
    <Returns>
      <Argument name="result">
        <Description>
                A Provisioning Result object is desirable to return the status.IT can be a new object or part of  Provisioning Plan
                </Description>
      </Argument>
    </Returns>
  </Signature>
  <Source>import java.util.Date;
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
	if ( ( accounts != null ) &amp;&amp; ( accounts.size() > 0 ) ) {

		// If the plan contains one or more account requests, we'll iterate through them
		for ( AccountRequest account : accounts ) {	
			try { 
				// All of the account operations will reside in a try block in case we have any errors, we can mark the provisioningresult as "Failed" if we have an issue.
				if (AccountRequest.Operation.Create.equals(account.getOperation())) {
					// CREATE Operation	OFFICER_TAB		
					System.out.println("Account Request Operation = Create EMPLOYEE");

					PreparedStatement statement = connection.prepareStatement("INSERT INTO PEGADAIAN.TBL_USER (USER_ID,CREATE_BY,VERSI,BRANCH_CODE,END_TIME,NAME,PASSWORD,START_TIME,CREATE_DATE,STATUS_AKTIF,STATUS_OTP,EMAIL,LOGIN_FAIL_COUNT) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
					
					
					statement.setString(1, (String) account.getNativeIdentity());
                    statement.setString(2, getAttributeRequestValue(account, "CREATE_BY"));
					statement.setString(3, getAttributeRequestValue(account, "VERSI"));
                    statement.setString(4, getAttributeRequestValue(account, "BRANCH_CODE"));
                    statement.setString(5, getAttributeRequestValue(account, "END_TIME"));
					statement.setString(6, getAttributeRequestValue(account, "NAME"));
                    statement.setString(7, getAttributeRequestValue(account, "PASSWORD"));
					statement.setString(8, getAttributeRequestValue(account, "START_TIME"));
                    statement.setString(9, getAttributeRequestValue(account, "CREATE_DATE"));
                    statement.setString(10, getAttributeRequestValue(account, "STATUS_AKTIF"));
					statement.setString(11, getAttributeRequestValue(account, "STATUS_OTP"));
                    statement.setString(12, getAttributeRequestValue(account, "EMAIL"));
                    statement.setString(13, getAttributeRequestValue(account, "LOGIN_FAIL_COUNT"));
					
                    System.out.println("Preparing to execute users : " + statement);
					statement.executeUpdate();

					// Grab the role from the request. If it's a single role, it'll be a string, add it to 
					// the statement, other wise if it's a List, convert to CSV and add it to the statement
                    System.out.println("Task 1 sudah berjalan");
                    PreparedStatement statementGroup = connection.prepareStatement("INSERT INTO PEGADAIAN.TBL_USER_GROUP (USER_ID,GROUP_ID) VALUES (?,?)");

                    statementGroup.setString(1, (String) account.getNativeIdentity());

					AttributeRequest attrReq = account.getAttributeRequest("GROUP_ID_USER");
					if (attrReq != null) {
						if (attrReq.getValue() instanceof String) {
							statementGroup.setString(2, (String) attrReq.getValue());
                            System.out.println("attrReq: " + attrReq.getValue());
                            System.out.println("Preparing to execute users : " + statementGroup);
                            statementGroup.executeUpdate();
						} else if (attrReq.getValue() instanceof List) {
							String listOfRoles = Util.listToCsv((List) attrReq.getValue());
							
                            System.out.println("attrReq (as list): " + listOfRoles);
                            String[] split = listOfRoles.split(",");
                            for (int i = 0; i &lt; split.length; i++) {
                                System.out.println("attrReq (as array ) no " + i + " :"+ split[i]);
                                statementGroup.setString(2, split[i]);
                                System.out.println("attrReq: " + attrReq.getValue());
                                System.out.println("Preparing to execute users : " + statementGroup);
                                statementGroup.executeUpdate();
                            }
						}
					} else {
                        statementGroup.setString(2,"");
                        System.out.println("Preparing to execute users : " + statementGroup);
                        statementGroup.executeUpdate();
                    }
                    
					// Sucessful Create, so mark result as COMMITTED
					result.setStatus(ProvisioningResult.STATUS_COMMITTED);
					
				}  
                
                else if (AccountRequest.Operation.Modify.equals(account.getOperation())) {
					// MODIFY Operation
					// We have a modify, this one is trickier, as we can have "Add" and "Remove" 
					// operations and each can be a single string value or a list

					// Determine what the current roles are first...  
					System.out.println("Account Request Operation = Modify : " + account.getOperation());
					Statement curr_stmt = connection.createStatement();
					ResultSet rs = curr_stmt .executeQuery("SELECT PEGADAIAN.TBL_USER.USER_ID,PEGADAIAN.TBL_USER.CREATE_BY,PEGADAIAN.TBL_USER.UPDATE_BY,PEGADAIAN.TBL_USER.VERSI,BRANCH_CODE,ENABLED,END_TIME,LAST_CHANGE_PWD,LAST_LOG_IN,LOGIN_FAIL_COUNT,NAME,START_TIME,PEGADAIAN.TBL_USER.CREATE_DATE,PEGADAIAN.TBL_USER.UPDATE_DATE,LIMIT_APPROVAL,BRANCH_MOBILE_ID,STATUS_AKTIF,NPL_DATE,NPL_MIKRO,BRANCH_CHANNEL_ID,STATUS_OTP,EMAIL,NO_HP,BRANCH_MOBILE_ACTIVE,LISTAGG(GROUP_ID, ',' ) WITHIN GROUP (ORDER BY GROUP_ID) GROUP_ID_USER FROM PEGADAIAN.TBL_USER LEFT JOIN PEGADAIAN.TBL_USER_GROUP ON PEGADAIAN.TBL_USER.USER_ID = PEGADAIAN.TBL_USER_GROUP.USER_ID WHERE PEGADAIAN.TBL_USER.USER_ID = '" + account.getNativeIdentity() + "' GROUP by PEGADAIAN.TBL_USER.USER_ID,PEGADAIAN.TBL_USER.CREATE_BY,PEGADAIAN.TBL_USER.UPDATE_BY,PEGADAIAN.TBL_USER.VERSI,BRANCH_CODE,ENABLED,END_TIME,LAST_CHANGE_PWD,LAST_LOG_IN,LOGIN_FAIL_COUNT,NAME,START_TIME,PEGADAIAN.TBL_USER.CREATE_DATE,PEGADAIAN.TBL_USER.UPDATE_DATE,LIMIT_APPROVAL,BRANCH_MOBILE_ID,STATUS_AKTIF,NPL_DATE,NPL_MIKRO,BRANCH_CHANNEL_ID,STATUS_OTP,EMAIL,NO_HP,BRANCH_MOBILE_ACTIVE");

					//  Check result set. Should only be one row since login is a unique key for the table
					String groupIdUser =  "";
					String branchCode = "";
                    List currentGroup = null;
					List fromDbGroup = null;
                    

					while (rs.next()) {
						groupIdUser =rs.getString("GROUP_ID_USER");
          				branchCode = rs.getString("BRANCH_CODE");
            			System.out.println("CURENT GROUP_ID_USER : " + rs.getString("GROUP_ID_USER"));
						System.out.println("CURENT BRANCH_CODE : " + rs.getString("BRANCH_CODE"));
					}

					if (groupIdUser == null) {
						System.out.println("We have a null current_roles list... change it to an empty list for subsequent processing.");
						currentGroup = new ArrayList();
						fromDbGroup = new ArrayList();
					}
					else
					{
						String[] currentGroupString = groupIdUser.split(",");
						for (int i = 0; i &lt; currentGroupString.length; i++) {
							System.out.println("groupIdUser (as array ) no " + i + " :"+ currentGroupString[i]);
						}
						System.out.println("make new currentGroup");
                        currentGroup = new ArrayList(Arrays.asList(currentGroupString));
						fromDbGroup = new ArrayList(Arrays.asList(currentGroupString));
						
					}
					System.out.println("Current Group");
					System.out.println(currentGroup);
					// Get all Attribute Requests and pull out just the role ones. 										
					List removeGroup = new ArrayList();
					List addGroup = new ArrayList();

					// Get all attribute requests and then we will filter for those related to the roles column
					List mod_attr_requests = account.getAttributeRequests();
					System.out.println("mod_attr_requests = " + mod_attr_requests);
          
		  			
					if (mod_attr_requests != null) {
						for (AttributeRequest req : mod_attr_requests ) {
							System.out.println("mod_attr_requests value = " + req.getValue());
								if (req.getName().equals("GROUP_ID_USER")) {
								   if (ProvisioningPlan.Operation.Remove.equals(req.getOperation())) {
										// Process Removes First
										if (req.getValue() instanceof String) {
											removeGroup.add(req.getValue()) ;
										} else if (req.getValue() instanceof List) {
											removeGroup.add(Util.csvToList(req.getValue())) ;            
										}
									} else if (ProvisioningPlan.Operation.Add.equals(req.getOperation())) {
										// Process Adds Second
										if (req.getValue() instanceof String) {
											addGroup.add(req.getValue()) ;
										} else if (req.getValue() instanceof List) {
											addGroup.add(Util.csvToList(req.getValue())) ;                
										}
									} 

							   } 
						}
					}

					//  We now have a calculated list of the roles we are adding, the roles we are removing, and the current roles for the user.
					System.out.println("Add Group = " + Util.listToCsv(addGroup));
					System.out.println("Remove Group = " + Util.listToCsv(removeGroup));

                    //buat USER_GROUP_ID
					// If we have roles to remove, remove them
					if (!removeGroup.isEmpty()) {
						System.out.println("About to remove Group: " + removeGroup.toString() + " from the currentGroup = " + currentGroup.toString());
						currentGroup.removeAll(removeGroup);
					}

					// If we have roles to add, check if they are there and add them as we iterate through
					if (!addGroup.isEmpty()) {
						System.out.println("About to add Group: " + addGroup.toString() + " to the currentGroup = " + currentGroup.toString());
						for (Object item: addGroup) {
							 if (!currentGroup.contains(item)) {
									   currentGroup.add(item);
							 }
						}
					}
					
					//  Print out the list of roles being provisioned after processing "add" and "remove" operations
					System.out.println("Updating the userGroupId for : " + (String) account.getNativeIdentity() + " Current Group after adding/removing = " + Util.listToCsv(currentGroup)); 

                    // Query
					// Process update SQL operation
					// Custom script
                    // Query yang table user dulu untuk update BRANCH_CODE
                    // logic berbeda dengan default sailpoint, karena tablenya 1 to many
					// REMOVE : kalo ada yg perlu di remove remove dulu 
					if (!removeGroup.isEmpty()) { // cek ada yg perlu di remove
						System.out.println("About to query remove Group: " + removeGroup.toString() + " from the currentGroup = " + currentGroup.toString());
			
						int i = 0;
						for (Object item: removeGroup) {
							
							// di looping cek di db udah ada, kalo ada query delete
							 if (fromDbGroup.contains(item)) {
								// delete where TBL_USER_GROUP USER_ID xxx and GROUP_ID xxx perulangan sesuai list remove group
								PreparedStatement statementDeleteGroup = connection.prepareStatement("DELETE FROM PEGADAIAN.TBL_USER_GROUP WHERE USER_ID = ? AND GROUP_ID = ? ");
								statementDeleteGroup.setString(1, (String) account.getNativeIdentity());
								statementDeleteGroup.setString(2, item.toString());
								System.out.println( " DELETE GROUP ID  : " + item.toString() + " NO:" + i++);
								System.out.println("RAW  : " + "DELETE FROM PEGADAIAN.TBL_USER_GROUP WHERE USER_ID = "+(String) account.getNativeIdentity()+" AND GROUP_ID =  "+item.toString());
								System.out.println("Preparing to DELETE FROM  TBL_USER_GROUP  : " + statementDeleteGroup);
								statementDeleteGroup.executeUpdate();

							 }
						}
					}
					// ADD :  pertama cek dulu yg di add udah ada belum di data base dengan user identity tersebut, kalau belum ada insert
					if (!addGroup.isEmpty()) {
						System.out.println("About to add Group: " + addGroup.toString() + " to the currentGroup = " + currentGroup.toString());
						for (Object item: addGroup) {
							// di looping add group cek dulu add group apa sudah ada di db
							// kalau sudah ada skip
							 if (!fromDbGroup.contains(item)) {
								PreparedStatement statementAddGroup = connection.prepareStatement("INSERT INTO PEGADAIAN.TBL_USER_GROUP (USER_ID,GROUP_ID) VALUES (?,?)");
								statementAddGroup.setString(1, (String) account.getNativeIdentity());
								statementAddGroup.setString(2, item.toString());
								System.out.println("Preparing to INSERT INTO TBL_USER_GROUP  : " + statementAddGroup);
								statementAddGroup.executeUpdate();
							 }
						}
					}
					// END group opration
					
					//ini hanya contoh jika one to one 
					/*
					System.out.println("Preparing to Update TBL_USER value  BRANCH_CODE  : " + Util.listToCsv(current_roles));
					PreparedStatement statement = connection.prepareStatement("UPDATE PEGADAIAN.TBL_USER SET BRANCH_CODE = ? WHERE USER_ID = ?");
					statement.setString(1, Util.listToCsv(current_roles));
					statement.setString(2, (String) account.getNativeIdentity());
					System.out.println("Preparing to UPDATE user from OFFICER_TAB WFU_ID : " + statement);
					statement.executeUpdate();
                    */
					// 
                  
          			String branchFromSailpoint = getAttributeRequestValue(account, "BRANCH_CODE");
					System.out.println("branchFromSailpoint : " + branchFromSailpoint);
					
					if (branchFromSailpoint != null) {
						System.out.println("Preparing to Update user from TBL_USER BRANCH_CODE  : " + getAttributeRequestValue(account, "BRANCH_CODE"));
						PreparedStatement statementBranch = connection.prepareStatement("UPDATE PEGADAIAN.TBL_USER SET BRANCH_CODE = ? WHERE USER_ID = ?");
						statementBranch.setString(1, getAttributeRequestValue(account, "BRANCH_CODE"));
						statementBranch.setString(2, (String) account.getNativeIdentity());
						System.out.println("Preparing to UPDATE user from TBL_USER BRANCH_CODE : " + statementBranch);
						statementBranch.executeUpdate();
					}	else {
						System.out.println("Preparing to Update user from TBL_USER BRANCH_CODE FromDB  : " + branchCode);
						PreparedStatement statementBranch = connection.prepareStatement("UPDATE PEGADAIAN.TBL_USER SET BRANCH_CODE = ? WHERE USER_ID = ?");
						statementBranch.setString(1, branchCode);
						statementBranch.setString(2, (String) account.getNativeIdentity());
						System.out.println("Preparing to UPDATE user from TBL_USER BRANCH_CODE : " + statementBranch);
						statementBranch.executeUpdate();
					}

					result.setStatus(ProvisioningResult.STATUS_COMMITTED);
                }

				//---------
				//disable untuk field ENABLED
				else if (AccountRequest.Operation.Delete.equals(account.getOperation())) {
					//
					// DELETE Operation
					// 
					PreparedStatement statement = connection.prepareStatement("UPDATE PEGADAIAN.TBL_USER SET ENABLED = 0, STATUS_AKTIF = 0 WHERE USER_ID = ?");
					statement.setString(1, (String) account.getNativeIdentity());
					statement.executeUpdate();

					PreparedStatement statementDeleteUserGroup = connection.prepareStatement("DELETE FROM PEGADAIAN.TBL_USER_GROUP WHERE USER_ID = ?");
					statementDeleteUserGroup.setString(1, (String) account.getNativeIdentity());
					statementDeleteUserGroup.executeUpdate();

					result.setStatus(ProvisioningResult.STATUS_COMMITTED);

				} else if (AccountRequest.Operation.Disable.equals(account.getOperation())) {

					PreparedStatement statement = connection.prepareStatement("UPDATE PEGADAIAN.TBL_USER SET ENABLED = 0, STATUS_AKTIF = 0 WHERE USER_ID = ?");
					statement.setString(1, (String) account.getNativeIdentity());
					statement.executeUpdate();
					result.setStatus(ProvisioningResult.STATUS_COMMITTED);

				} else if (AccountRequest.Operation.Enable.equals(account.getOperation())) {

					PreparedStatement statement = connection.prepareStatement("UPDATE PEGADAIAN.TBL_USER SET ENABLED = 1, STATUS_AKTIF = 1 WHERE USER_ID = ?");
					statement.setString(1, (String) account.getNativeIdentity());
					statement.executeUpdate();
					result.setStatus(ProvisioningResult.STATUS_COMMITTED);

				} else if (AccountRequest.Operation.Lock.equals(account.getOperation())) {

					PreparedStatement statement = connection.prepareStatement("UPDATE PEGADAIAN.TBL_USER SET ENABLED = 0, STATUS_AKTIF = 0 WHERE USER_ID = ?");
					statement.setString(1, (String) account.getNativeIdentity());
					statement.executeUpdate();
					result.setStatus(ProvisioningResult.STATUS_COMMITTED);

				} else if (AccountRequest.Operation.Unlock.equals(account.getOperation())) {

					PreparedStatement statement = connection.prepareStatement("UPDATE PEGADAIAN.TBL_USER SET ENABLED = 1, STATUS_AKTIF = 1 WHERE USER_ID = ?");
					statement.setString(1, (String) account.getNativeIdentity());
					statement.executeUpdate();
					result.setStatus(ProvisioningResult.STATUS_COMMITTED);

				}
				//
                else  {
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
		
return result;</Source>
</Rule>
