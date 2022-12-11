import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

for(Object object: processedResponseObject) {

	Map userMap=(Map)object;
	System.out.println("*** \n After rule - userMap = \n***\n" + userMap + "\n****************************************");
	if(userMap.containsKey("accessGroup")) {
		System.out.println("*** \n After rule - TRUE = \n***\n" + "\n****************************************");

		String statusValue=((String) userMap.get("accessGroup"));
		System.out.println("*** \n After rule - statusValue = \n***\n" + statusValue + "\n****************************************");

		if(statusValue.equalsIgnoreCase("DeleteInactive:User")) {
			System.out.println("*** \n After rule - Already disabled = \n***\n" + "\n****************************************");
			userMap.put("IIQDisabled", true);	
		}
	
	}
}