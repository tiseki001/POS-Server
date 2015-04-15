
<form name="editReportTree" action="<#if parameters.rename?has_content><@ofbizUrl>updateDocumentTree</@ofbizUrl><#else><@ofbizUrl>addDocumentToTree</@ofbizUrl></#if>" method="post">
    <#assign reportRoleList =delegator.findByAnd("ReportRole",Static["org.ofbiz.base.util.UtilMisc"].toMap("reportId",parameters.reportId))/>
    <#assign roleList = delegator.findList("RoleType",null,null, null, null, false) />
    <#if parameters.rname?has_content>
    	<#assign report  = delegator.findByPrimaryKey("Report",Static["org.ofbiz.base.util.UtilMisc"].toMap("reportId",parameters.reportId))/>
        <div class="h3">${uiLabelMap.UpdateTheFolder}</div>
        <input type="hidden" name="reportId" value="${report.reportId}" />
        ${uiLabelMap.reportName} : <input type="text" name="reportName" value="${report.reportName}" /><br />
        ${uiLabelMap.reportType} : <input type="text" name="reportType" value="${report.reportType}" /><br />
        <#list roleList as role>
        	<#assign rowClass = "2">
        	<#if role?has_content>
        		<#list reportRoleList as reportRole>
        			<#if reportRole?has_content>
        				<#if reportRole.roleTypeId == role.roleTypeId>
        						<#assign rowClass = "1">
								<input type="checkbox" name="roleTypeId" value="${role.roleTypeId}" checked/>"${role.description}"<br />
        					<#else>
        						
        				</#if>
        			</#if>
        		</#list>
        		<#if rowClass == "2">
	              <input type="checkbox" name="roleTypeId" value="${role.roleTypeId}"/>"${role.description}"<br />
	            <#else>
	              <#assign rowClass = "2">
	            </#if>
        	</#if>
        </#list>
        <br />
        <a class="buttontext" href="javascript:document.editReportTree.submit();">${uiLabelMap.UpdateFolder}</a><a class="buttontext" href="<@ofbizUrl>navigateReport</@ofbizUrl>">${uiLabelMap.CommonCancel}</a>
    <#else>
        <div class="h3">${uiLabelMap.ReportNewFolder}</div>
        ${uiLabelMap.reportName} : <input type="text" name="reportName" /><br />
        ${uiLabelMap.reportType} : <input type="text" name="reportType" /><br />
        <#assign siz=0/>
        <#list roleList as role>
        	<#if role?has_content>
				<input type="checkbox" name="roleTypeId" value="${role.roleTypeId}" />"${role.description}"<br />
        	</#if>
        </#list>
        <br />
        <a class="buttontext" href="javascript:document.editReportTree.submit();">${uiLabelMap.CommonCreate}</a><a class="buttontext" href="<@ofbizUrl>navigateReport</@ofbizUrl>">${uiLabelMap.CommonCancel}</a>
    </#if>
</form>
<hr />
