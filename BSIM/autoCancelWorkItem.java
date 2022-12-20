Filter f=Filter.eq("type", "Approval");
QueryOptions qo=new QueryOptions();
qo.add(f);
List openWorkItems = new ArrayList();

try {
Iterator itr=context.search(WorkItem.class, qo,"name");

while(itr.hasNext()) {
	Object[] workItemProps=(Object[]) itr.next();
	String wkName=workItemProps[0];
	openWorkItems.add(wkName);
}
openWorkItems.add("0000000123");
for(String eachWorkItem:openWorkItems) {
	log.debug("EachWorkIte = "+eachWorkItem);
	WorkItem eachWorkItemObj=context.getObjectByName(WorkItem.class, eachWorkItem);
	Identity workItemOwner=eachWorkItemObj.getOwner();
	if(workItemOwner!=null) {
		if(workItemOwner.isInactive()) {
			
			eachWorkItemObj.setState(WorkItem.State.Canceled);
			eachWorkItemObj.setCompleter("spadmin");
			eachWorkItemObj.setCompletionComments("Cancelling due to Owner is no longer active");
			context.saveObject(eachWorkItemObj);
			
			IdentityRequest accRequest=context.getObjectByName(IdentityRequest.class, eachWorkItemObj.getIdentityRequestId());
			accRequest.setCompletionStatus(CompletionStatus.Incomplete);
			context.saveObject(accRequest);
			context.commitTransaction();
		}else {
			log.debug("Owet is Active nothig to do it ");
		}
	}
	
}
} catch (GeneralException e) {
	e.printStackTrace();
}