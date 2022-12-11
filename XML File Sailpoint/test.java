import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import connector.common.JsonUtil;
import connector.common.Util;
import sailpoint.connector.webservices.EndPoint;
import sailpoint.connector.webservices.WebServicesClient;
import sailpoint.object.Application;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningPlan.PermissionRequest;
import sailpoint.object.ProvisioningResult;           
import sailpoint.tools.Util; 
import sailpoint.object.Attributes;
import sailpoint.object.Link;
import sailpoint.tools.GeneralException;
import sailpoint.object.Link;
import sailpoint.object.Filter;
import sailpoint.object.ManagedAttribute;
import sailpoint.object.TaskResult;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import flexjson.JSONDeserializer;
import sailpoint.connector.webservices.EndPoint;

//retriving message body
Map body = requestEndPoint.getBody();
String jsonBody = (String) body.get("jsonBody");
System.out.println("*** \n JSON Body = \n***\n" + jsonBody + "\n****************************************");

//Traversing in Json Response
JSONParser jsonParser = new JSONParser();
JSONObject jOb = (JSONObject) jsonParser.parse(inline);

//populating userRoleList values
String populating = jOb.get("accessGroupList").get(0).get("assetClassCode").toString();
logger.debug("populating is : " + populating);