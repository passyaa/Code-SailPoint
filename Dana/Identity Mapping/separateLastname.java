import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

System.out.println("START RULE SEPARATE LASTNAME");  

String fullName = link.getAttribute("nm_peg");
String lastName = "";

if (fullName!=null) {
    int firstIndex = fullName.indexOf(" ");
    //System.out.println("index = " + firstIndex);

    if (firstIndex == -1) {
	lastName  = lastName;
	//System.out.println("Only a single name for lastName : " + lastName);
    } else {
	lastName  = fullName.substring(firstIndex + 1);
	//System.out.println("lastName : " + lastName);
    }

}


return lastName.toLowerCase();
