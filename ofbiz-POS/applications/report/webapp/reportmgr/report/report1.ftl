<#if reportId?has_content>
<iframe id="reportFrame" width="100%" height="800" scrolling="auto" src="/WebReport/ReportServer?reportlet=${reportId}.cpt&userLoginId=${userLoginId}"></iframe>
</#if>