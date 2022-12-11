import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String codeLoc = identity.getAttribute("location_code");
String result = "";

if (codeLoc != null) {
    if (codeLoc.equals("ID0010001")) {
        result = "1";
    } else {
        result = "2";
    }
}

return result;