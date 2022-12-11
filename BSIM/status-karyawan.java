import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String valueInactiveOrganik = "";

String empStatus = link.getAttribute("status");
if (empStatus!=null) {
  if (empStatus.equals("TRUE")) {
      valueInactiveOrganik = "Karyawan Aktif";
  } else {
      valueInactiveOrganik = "Karyawan Tidak Aktif";
  } 
}
return valueInactiveOrganik;