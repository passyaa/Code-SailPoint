import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String namaKantor = link.getAttribute("nm_kantor");
String ouLocation = "";

if (namaKantor.equals("KPNO")) {
    ouLocation = "CN=" + identity.getDisplayName() + ",OU=KPNO,OU=Users,OU=Account,DC=banksinarmas,DC=com";
    System.out.println(ouLocation);
} else {
    ouLocation = "CN=" + identity.getDisplayName() + ",OU=Branch,OU=Users,OU=Account,DC=banksinarmas,DC=com";
    System.out.println(ouLocation);
}

return ouLocation;