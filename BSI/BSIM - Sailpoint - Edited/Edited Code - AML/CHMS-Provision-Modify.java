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
                        System.out.println("Account Request Operation = Modify : " + getAttributeRequestValue(account, "ID_USER"));
                        Statement curr_stmt = connection.createStatement();
                        ResultSet rs = curr_stmt .executeQuery("SELECT USER_ID,USER_GROUP_ID,BRANCH_CODE,USER_NAME,USER_LOGIN,USER_PASSWORD,USER_ISLOGON,USER_STATUS,USER_CREATED_BY,USER_CREATED_DATE,USER_CREATED_TIME,ID_UNIT,ID_DIVISION,ID_REGION,EMAIL,BRANCH_ID,NOTIF_EVENT,SOURCE_BANK,LOGIN_TYPE FROM [SYSTEM_USER] WHERE USER_ID = '" + getAttributeRequestValue(account, "ID_USER") + "'");

                        //  Check result set. Should only be one row since login is a unique key for the table
                        String USER_GROUP_ID = "";

                        while (rs.next()) {
                            USER_GROUP_ID = rs.getString("USER_GROUP_ID");
                            System.out.println("USER_GROUP_ID : " + USER_GROUP_ID);
                        }

                        List currentRoles = Util.csvToList(USER_GROUP_ID,true);
                        System.out.println("current USER_GROUP_ID : " + currentRoles);
            
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
                                    if (req.getName().equals("USER_GROUP_ID")) {
                                        if (ProvisioningPlan.Operation.Remove.equals(req.getOperation())) {
                                            // Process Removes First
                                            if (req.getValue() instanceof String) {
                                                removeRoles = Util.csvToList(req.getValue());
                                                System.out.println("remove USER_GROUP_ID instanceof String = " + removeRoles);
                                            } else if (req.getValue() instanceof List) {
                                                removeRoles = req.getValue();
                                                System.out.println("remove USER_GROUP_ID instanceof List = " + removeRoles);                
                                            }
                                        } else if (ProvisioningPlan.Operation.Add.equals(req.getOperation())) {
                                            // Process Adds Second
                                            if (req.getValue() instanceof String) {
                                                addRoles = Util.csvToList(req.getValue());
                                                System.out.println("add USER_GROUP_ID instanceof String = " + addRoles);
                                            } else if (req.getValue() instanceof List) {
                                                addRoles = req.getValue();
                                                System.out.println("add USER_GROUP_ID instanceof List = " + addRoles);                 
                                            }
                                        } 

                                } 
                            }
                        }

                        // If we have roles to remove, remove them
                        if (!removeRoles.isEmpty()) {
                            System.out.println("About to remove USER_GROUP_ID: " + removeRoles.toString() + " from the current USER_GROUP_ID = " + currentRoles.toString());
                            currentRoles.removeAll(removeRoles);
                        }

                        // If we have roles to add, check if they are there and add them as we iterate through
                        if (!addRoles.isEmpty()) {
                            System.out.println("About to add USER_GROUP_ID : " + addRoles.toString() + " and remove current USER_GROUP_ID first : " + currentRoles.toString() + " and become USER_GROUP_ID : " + addRoles.toString());

                            // currentRoles.removeAll(currentRoles);
                            // System.out.println("After Remove All currentRoles : " + Util.listToCsv(currentRoles));
                            // System.out.println("currentRoles.removeAll(currentRoles) : " + currentRoles.removeAll(currentRoles));
                            
                            currentRoles.clear();
                            System.out.println("After Clear current USER_GROUP_ID : " + Util.listToCsv(currentRoles));

                            for (Object item : addRoles) {
																currentRoles.add(item);
                                System.out.println("After Add USER_GROUP_ID : " + Util.listToCsv(currentRoles));
																}

													}

                        }
                    

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