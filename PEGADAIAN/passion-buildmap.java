import sailpoint.connector.JDBCConnector;
import sailpoint.connector.Connector;
import java.util.HashMap;

System.out.println("result : " + result);

HashMap map = JDBCConnector.buildMapFromResultSet(result);
System.out.println("map : " + map);
System.out.println("schema.getObjectType() : " + schema.getObjectType());
System.out.println("Connector : " + Connector);
System.out.println("Connector.TYPE_ACCOUNT : " + Connector.TYPE_ACCOUNT);

String active = map.get("STATUS");
System.out.println("active : " + active);

if (active.equals("5")) {
    map.put("IIQDisabled", false);
    System.out.println("Active : " + "5");
} else {
    map.put("IIQDisabled", true);
    System.out.println("Disabled : " + "8/9");
}

return map;