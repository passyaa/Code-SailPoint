// --------- DISABLE & ENABLE RULE WITH SWITHC CASE --------

import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

// disableAndenable
String status = "";
String statusCheck = identity.getAttribute("status");
String firstname = identity.getFirstname();
String lastname = identity.getLastname();
String employeeid = identity.getAttribute("empId");
System.out.println("*** \n statusCheck = " + firstname + " " + lastname + " " + employeeid + " \n***\n" + statusCheck + "\n****************************************");

if (statusCheck!=null) {
    switch (statusCheck) {
        case "ACTIVE":
            // ACTIVE
            status = "1";
            System.out.println("*** \n STATUS ACTIVE \n***\n" + "\n****************************************");
            return status;
            break;
        case "INACTIVE":
            // INACTIVE
            status = "0";
            System.out.println("*** \n STATUS INACTIVE \n***\n" + "\n****************************************");
            return status;
            break;
    }
}

return status;

// --------- DISABLE & ENABLE RULE WITH SWITCH CASE --------

import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

// DISABLE & ENABLE RULE WITH IF ELSE & SWITCH CASE
String status = "";
String statusCheck = identity.getAttribute("status");
String jobTitleCodeCheck = identity.getAttribute("jobtitle_code");
String firstname = identity.getFirstname();
String lastname = identity.getLastname();
String employeeid = identity.getAttribute("empId");
System.out.println("*** \n statusCheck = " + firstname + " " + lastname + " " + employeeid + " \n***\n" + statusCheck + "\n****************************************");
System.out.println("*** \n jobTitleCodeCheck = " + firstname + " " + lastname + " " + employeeid + " \n***\n" + jobTitleCodeCheck + "\n****************************************");


if (statusCheck != null) {
    if (statusCheck.equals("ACTIVE")) { 
        switch (jobTitleCodeCheck) {
            case "BOAA0001":
                // ACTIVE
                status = "1";
                return status;
                break;
            case "BOAA0012":
                // ACTIVE
                status = "1";
                return status;
                break;
            case "POAA0002":
                // ACTIVE
                status = "1";
                return status;
                break;
            case "COAA0002":
                // ACTIVE
                status = "1";
                return status;
                break;
            case "COAA0001":
                // ACTIVE
                status = "1";
                return status;
                break;
            case "BOAA0019":
                // ACTIVE
                status = "1";
                return status;
                break;
            case "MOAA0001":
                // ACTIVE
                status = "1";
                return status;
                break;
            case "BOAA0018":
                // ACTIVE
                status = "1";
                return status;
                break;
            case "POAA0001":
                // ACTIVE
                status = "1";
                return status;
                break;
            default:
                // INACTIVE
                status = "0";
                return status;
                break;
        }
    } else if (statusCheck.equals("INACTIVE")) {
        // INACTIVE
        status = "0";
    }    
}

return status;

// --------- SUB USER GROUP ID RULE --------
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

// branchid
String subUserGroupId = "";
String jobTitleCodeCheck = identity.getAttribute("jobtitle_code");
System.out.println("*** \n jobTitleCodeCheck = \n***\n" + jobTitleCodeCheck + "\n****************************************");

if (jobTitleCodeCheck!=null) {
    switch (jobTitleCodeCheck) {
        case "BOAA0001":
            // Branch Manager
            // Approval
            subUserGroupId = "9";
            return subUserGroupId;
            break;
        case "BOAA0012":
            // Branch Operations & Service Manager
            // Checker
            subUserGroupId = "8";
            return subUserGroupId;
            break;
        case "POAA0002":
            // Branch Operations Supervisor KFO
            // Approval
            subUserGroupId = "9";
            return subUserGroupId;
            break;
        case "COAA0002":
            // Branch Operations Supervisor KK
            // Approval
            subUserGroupId = "9";
            return subUserGroupId;
            break;
        case "COAA0001":
            // Cash Outlet Supervisor
            // Checker
            subUserGroupId = "8";
            return subUserGroupId;
            break;
        case "BOAA0019":
            // General Affairs Staff
            // Maker
            subUserGroupId = "7";
            return subUserGroupId;
            break;
        case "MOAA0001":
            // Micro Outlet Supervisor
            // Checker
            subUserGroupId = "8";
            return subUserGroupId;
            break;
        case "BOAA0018":
            // Operational Staff
            // Maker
            subUserGroupId = "7";
            return subUserGroupId;
            break;
        case "POAA0001":
            // Pawning Outlet Supervisor
            // Approval
            subUserGroupId = "9";
            return subUserGroupId;
            break;
    }
}

return subUserGroupId;



// --------- USER MULPOS RULE --------

import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

// userMulpos
String userMulpos = "";
String jobTitleCodeCheck = identity.getAttribute("jobtitle_code");
System.out.println("*** \n jobTitleCodeCheck = \n***\n" + jobTitleCodeCheck + "\n****************************************");

if (jobTitleCodeCheck!=null) {
    switch (jobTitleCodeCheck) {
        case "BOAA0001":
            // Branch Manager
            // Approval
            userMulpos = "0";
            return userMulpos;
            break;
        case "BOAA0012":
            // Branch Operations & Service Manager
            // Checker
            userMulpos = "0";
            return userMulpos;
            break;
        case "POAA0002":
            // Branch Operations Supervisor KFO
            // Approval
            userMulpos = "0";
            return userMulpos;
            break;
        case "COAA0002":
            // Branch Operations Supervisor KK
            // Approval
            userMulpos = "0";
            return userMulpos;
            break;
        case "COAA0001":
            // Cash Outlet Supervisor
            // Checker
            userMulpos = "0";
            return userMulpos;
            break;
        case "BOAA0019":
            // General Affairs Staff
            // Maker
            userMulpos = "0";
            return userMulpos;
            break;
        case "MOAA0001":
            // Micro Outlet Supervisor
            // Checker
            userMulpos = "0";
            return userMulpos;
            break;
        case "BOAA0018":
            // Operational Staff
            // Maker
            userMulpos = "0";
            return userMulpos;
            break;
        case "POAA0001":
            // Pawning Outlet Supervisor
            // Approval
            userMulpos = "0";
            return userMulpos;
            break;
    }
}

return userMulpos;