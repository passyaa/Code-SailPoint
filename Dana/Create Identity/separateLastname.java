import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

System.out.println("START RULE SEPARATE LASTNAME");  

String fullName = link.getAttribute("nm_peg");
String lastname = "";
String capitalizeWord = "";

if (fullName!=null) {
    if (fullName.split(" ").length == 1) {
        lastname = "";
        //System.out.println(lastname);
    } else {
        lastname = fullName.split(" ")[fullName.split(" ").length-1];
        //System.out.println(lastname);
    }
}
  
if (lastname!="") {
  String lowerCase = lastname.toLowerCase();
  String[] words = lowerCase.split(" ");
	 
	for(String w:words) {  
		String first=w.substring(0,1);  
		String afterfirst=w.substring(1);  
		capitalizeWord+=first.toUpperCase()+afterfirst+" ";  
	}
  //System.out.println(capitalizeWord.trim());
}

return capitalizeWord;