// USER_ID
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String employeeID = identity.getAttribute("empId");
return employeeID;

// USER_GROUP_ID

// BRANCH_CODE
// attached another file
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String branchCode = identity.getAttribute("location_code");
return branchCode;

// USER_NAME
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String email = identity.getAttribute("email");
String userName = email.substring(0, email.indexOf("@"));
return userName;

// USER_LOGIN
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String email = identity.getAttribute("email");
String userName = email.substring(0, email.indexOf("@"));
return userName;

// USER_PASSWORD
String value = "AervBYTQB6640odXgt7760NDIQP8400MwDRA8640PvUwL8960oYMkMLayOr5H4JD80R8880OZKtT8400JaOWJ8800XAnaW9280IbuER3920WPGce26409jD9";

// USER_ISLOGON
String value = "0";

// USER_ISLOGON_DATE
String value = "null";

// USER_STATUS
// 1 = active
String value = "1";

// USER_CREATED_BY
String value = "Created by SailPoint";

// USER_CREATED_DATE
import java.text.DateFormat;
import java.text.SimpleDateFormat;

DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
Date now = new Date();
return dateFormatter.format(now);

// USER_CREATED_TIME
import java.text.DateFormat;
import java.text.SimpleDateFormat;

DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
Date now = new Date();
return dateFormatter.format(now);

// ID_UNIT
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String codeLoc = identity.getAttribute("location_code");
String result = "";

if (codeLoc != null) {
    if (codeLoc.equals("ID0010001")) {
        // Kantor Pusat
        result = "1";
    } else if (codeLoc.equals("ID0019002")) {
        // Kantor Wilayah 1 atau RO 1
        result = "2";
    } else if (codeLoc.equals("ID0010468")) {
        // Kantor Wilayah 2 atau RO 2
        result = "2";
    } else if (codeLoc.equals("ID0010865")) {
        // Kantor Wilayah 3 atau RO 3
        result = "2";
    } else if (codeLoc.equals("ID0010469")) {
        // Kantor Wilayah 4 atau RO 4
        result = "2";
    } else if (codeLoc.equals("ID0018004")) {
        // Kantor Wilayah 5 atau RO 5
        result = "2";
    } else if (codeLoc.equals("ID0010470")) {
        // Kantor Wilayah 6 atau RO 6
        result = "2";
    } else if (codeLoc.equals("ID0010909")) {
        // Kantor Wilayah 7 atau RO 7
        result = "2";
    } else if (codeLoc.equals("ID0010471")) {
        // Kantor Wilayah 8 atau RO 8
        result = "2";
    } else if (codeLoc.equals("ID0010866")) {
        // Kantor Wilayah 9 atau RO 9
        result = "2";
    } else if (codeLoc.equals("ID0010611")) {
        // Kantor Wilayah 10 atau RO 10
        result = "2";
    } else {
        // Kantor Cabang
        result = "3";
    }
}
return result;

// ID_DIVISION
String value = "0";

// ID_REGION
// attached another file

// EMAIL
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String email = identity.getAttribute("email");
return email;

// BRANCH_ID
// attached another file

// NOTIF_EVENT
String value = "1";

// SOURCE_BANK
String value = "BSI";

// LOGIN_TYPE
String value = "Email";