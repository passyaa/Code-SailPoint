import sailpoint.connector.JDBCConnector;
import sailpoint.connector.Connector;
import java.util.HashMap;

HashMap map = JDBCConnector.buildMapFromResultSet(result);
if (schema.getObjectType().compareTo(Connector.TYPE_ACCOUNT) == 0) {
    String active = map.get("STATUS");
    if (active.equals("5")) {
        map.put("IIQDisabled", false);
        } else {
        map.put("IIQDisabled", true);
    }
    
} else if (schema.getObjectType().compareTo(Connector.TYPE_GROUP) == 0) {
    // Handle Groups here...
}
return map;