import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String fullName = link.getAttribute("nm_peg");
String firstname = "";

if (fullName.split(" ").length == 1) {
    firstname = fullName;
    System.out.println(firstname);
} else {
    String surName = fullName.split(" ")[fullName.split(" ").length-1];
    firstname = fullName.substring(0, fullName.length() - surName.length());
    System.out.println(firstname);
}

return firstname;


