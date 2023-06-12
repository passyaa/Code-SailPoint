import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;
  
System.out.println("START RULE SEPARATE FIRSTNAME");

String fullName = link.getAttribute("nm_peg");
String firstname = "";

if (fullName!=null) {
    int firstIndex = fullName.indexOf(" ");
    //System.out.println("index = " + firstIndex);

    if (firstIndex == -1) {
	String firstName = fullName;
	//System.out.println("Only a single name: " + firstName);
    } else {
	String firstName = fullName.substring(0, firstIndex);
	//System.out.println("firstName: " + firstName);
    }

}

return firstName.toLowerCase();
