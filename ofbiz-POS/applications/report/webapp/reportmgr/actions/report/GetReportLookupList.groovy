
context.hasPermission = security.hasEntityPermission("CATALOG", "_UPDATE", session);


String reportId = (String)parameters.get("reportId");
Map<String,Object> userLogin = parameters.get("userLogin");
String userLoginId = (String)userLogin.get("userLoginId");
String partyId = (String)userLogin.get("partyId");
if(reportId == null){
	reportId="";
}

context.userLoginId =userLoginId;
context.reportId=reportId;
