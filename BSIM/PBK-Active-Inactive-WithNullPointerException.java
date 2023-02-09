import sailpoint.connector.JDBCConnector;
import sailpoint.connector.Connector;
import java.util.HashMap;

HashMap map = JDBCConnector.buildMapFromResultSet(result);

try {
    String pbkStatus = map.get("STATUS").toString();
    
    if (pbkStatus != null) {
        String active = "5";

        if (pbkStatus.equals(active)) {
            map.put("IIQDisabled", false);
            //System.out.println("Enable");
        } else {
            map.put("IIQDisabled", true);
            //System.out.println("Disabled");
        }
    }
} catch(NullPointerException e) {
    System.out.print("NullPointerException Caught");
    map.put("IIQDisabled", true);
}

return map;