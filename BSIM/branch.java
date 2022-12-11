import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String kodeKantorLama = link.getAttribute("kd_kantor_lama");
String branch = "";

if (kodeKantorLama.length() >= 3) {
    branch = kodeKantorLama.substring(0,3);
    System.out.println(branch);
} else {
    branch = kodeKantorLama;
    System.out.println(branch);
}

return branch;