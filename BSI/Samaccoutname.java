import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.Filter;

String firstname = identity.getFirstname();
String lastname = identity.getLastname();
String employeeid = identity.getAttribute("empId");
String lastDigit = employeeid.substring(employeeid.length() - 4);

if((null != firstname) && (null != lastname)) {

    String localpart= firstname + "."+ lastname;
    String genEmail= localpart;

    if (genEmail.length() <= 16 ){
        genEmail = localpart + lastDigit;
    } else if (genEmail.length() >= 16 ){
        genEmail = localpart.substring(0, 16) + lastDigit;
    }

    return genEmail;

}