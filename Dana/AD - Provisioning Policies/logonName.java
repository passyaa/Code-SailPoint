import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;


String fullName = link.getAttribute("nm_peg");
String lastname = "";

if (fullName!=null) {
    lastname = fullName.split(" ")[fullName.split(" ").length-1];
    char firstChar = fullName.charAt(0);
    char secondChars = fullName.charAt(1);
    //System.out.println("Logon Name : " + lastname + "." + firstChar);
}


return;