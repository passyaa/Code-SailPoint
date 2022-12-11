import sailpoint.connector.JDBCConnector;
import sailpoint.connector.Connector;
import java.util.HashMap;

System.out.println("result : " + result);

HashMap map = JDBCConnector.buildMapFromResultSet(result);
System.out.println("map : " + map);
System.out.println("schema.getObjectType() : " + schema.getObjectType());
System.out.println("Connector : " + Connector);
System.out.println("Connector.TYPE_ACCOUNT : " + Connector.TYPE_ACCOUNT);

String pbkStatus = map.get("STATUS").toString();
System.out.println("pbkStatus : " + pbkStatus);

if (pbkStatus!=null) {
    String active = map.get("STATUS").toString();
    System.out.println("STATUS ==> " + active);

    if (active.equals("5")) {
        map.put("IIQDisabled", false);
        System.out.println("Enable");
    } else {
        map.put("IIQDisabled", true);
        System.out.println("Disabled");
    }
}

return map;