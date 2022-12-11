import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String status = "";

String empStatus = link.getAttribute("status");
if (empStatus!=null) {
  if (empStatus.equals("TRUE")) {
      status = "FALSE";
  } else {
      status = "TRUE";
  } 
}
return status;