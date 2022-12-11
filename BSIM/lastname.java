import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String fullName = link.getAttribute("nm_peg");
String lastname = "";

if (fullName.split(" ").length == 1) {
    lastname = "";
    System.out.println(lastname);
} else {
    lastname = fullName.split(" ")[fullName.split(" ").length-1];
    System.out.println(lastname);
}

return lastname;