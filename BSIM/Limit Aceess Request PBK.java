import sailpoint.api.SailPointContext;
import sailpoint.api.IdentityService;  
import sailpoint.object.*;
import sailpoint.tools.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

System.out.println("******************");
System.out.println("GENERIC SOD START");
System.out.println("******************");

Boolean violate = false;
PolicyViolation v = null;

String APP_NAME = "PBK";
String ENT_NAME = "WFU_ID";

Application app = context.getObjectByName(Application.class, APP_NAME);

if( app != null ) {
    IdentityService ids = new IdentityService(context);
    if( ids != null ) {
        List<Link> listLink = ids.getLinks(identity,app);
        if ( listLink != null ) {
            for(Link l : listLink) {
                Attributes atts = l.getEntitlementAttributes();
                if( atts != null ) {
                    Object roles = atts.get(ENT_NAME);
                    if( roles != null ) {
                        System.out.println("ENT : ==> " + roles);
                        System.out.println("ENT TO STRING : ==> "  + roles.getClass().toString());
                        if(roles != null && roles instanceof ArrayList && (((ArrayList) roles).size() > 1)) {
                            System.out.println("VIOLATE IS TRUE");
                            violate = true;
                        } else {
                            System.out.println("VIOLATE IS FALSE");
                            violate = false;
                        }

                    }

                }

            }
            if (violate) {
                System.out.println("WE CREATE NEW SOD POLICY VIOLATION");
                v = new PolicyViolation();
                v.setActive(true);
                v.setIdentity(identity);
                v.setPolicy(policy);
                v.setConstraint(constraint);
                v.setDescription("SINGLE PROFILE SOD FOR : " + APP_NAME);
                v.setStatus(sailpoint.object.PolicyViolation.Status.Open);
            }
        }
    }
}

System.out.println("******************");
System.out.println("GENERIC SOD STOP");
System.out.println("******************");

return v;