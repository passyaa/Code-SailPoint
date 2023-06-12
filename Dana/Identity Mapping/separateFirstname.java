import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;
  
System.out.println("START RULE SEPARATE FIRSTNAME");

String fullName = link.getAttribute("nm_peg");
String firstname = "";
String capitalizeWord = "";

if (fullName!=null) {
	if (fullName.split(" ").length == 1) {
    firstname = fullName;
    //System.out.println(firstname);
	} else {
    String surName = fullName.split(" ")[fullName.split(" ").length-1];
    firstname = fullName.substring(0, fullName.length() - surName.length());
    //System.out.println(firstname);
	}
}
  
if (firstname!="") {
	String lowerCase = firstname.toLowerCase();
	String[] words = lowerCase.split(" ");
	
	for(String w:words) {  
		String first=w.substring(0,1);  
		String afterfirst=w.substring(1);  
		capitalizeWord+=first.toUpperCase()+afterfirst+" ";  
	}
  //System.out.println(capitalizeWord.trim());
}

return capitalizeWord.trim();