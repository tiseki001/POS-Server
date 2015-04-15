<#--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jsTree/jquery.jstree.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
<#-- some labels are not unescaped in the JSON object so we have to do this manuely -->
function unescapeHtmlText(text) {
    return jQuery('<div />').html(text).text()
}

jQuery(document).ready(createTree());

/*creating the JSON Data*/
var rawdata = [
      <@fillTree assocList = Report/>

      <#macro fillTree assocList>
          <#if (assocList?has_report)>
            <#list assocList as assoc>
                <#assign report  = delegator.findOne("Report",{"reportId":assoc.reportId},true)/>
                <#if locale != "en">
                  <#assign report = Static["org.ofbiz.report.report.ReportWorker"].findAlternateLocaleReport(delegator, report, locale)/>
                </#if>
                {
                "data": {"title" : unescapeHtmlText("${report.reportName!assoc.reportId}"), "attr": {"href": "javascript:void(0);", "onClick" : "callDocument('${assoc.reportId}');"}},
                <#assign assocChilds  = delegator.findByAnd("ReportAssoc",Static["org.ofbiz.base.util.UtilMisc"].toMap("reportId",assoc.reportId, Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNum"))/>
                    "attr": {"id" : "${assoc.reportIdTo}", "reportId" : "${assoc.reportId}"}
                    ,"children": [
                        <@fillTree assocList = assocChilds/>
                    ]
                },
                }
            </#list>
          </#if>
        </#macro>
     ];

 <#-------------------------------------------------------------------------------------define Requests-->
    var treeSelected = false;
    var listDocument =  '<@ofbizUrl>/views/ShowDocument</@ofbizUrl>';

 <#-------------------------------------------------------------------------------------create Tree-->
  function createTree() {
    jQuery(function () {
        jQuery("#tree").jstree({
            "plugins" : [ "themes", "json_data", "ui", "crrm"],
            "json_data" : {
                "data" : rawdata,
                "progressive_render" : false
            },
        });
    });
  }

<#-------------------------------------------------------------------------------------callDocument function-->
    function callDocument(reportId) {
        var tabitem='${tabButtonItem?if_exists}';
        if (tabitem=="navigateReport")
            listDocument = '<@ofbizUrl>/views/ListDocument</@ofbizUrl>';
        if (tabitem=="LookupReportTree")
            listDocument = '<@ofbizUrl>/views/ListReportTree</@ofbizUrl>';
        if (tabitem=="LookupDetailReportTree")
            listDocument = '<@ofbizUrl>/views/ViewReportDetail</@ofbizUrl>';

        //jQuerry Ajax Request
        jQuery.ajax({
            url: listDocument,
            type: 'POST',
            data: {"reportId" : reportId},
            error: function(msg) {
                showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.ErrorLoadingReport} : " + msg);
            },
            success: function(msg) {
                jQuery('#Document').html(msg);
            }
        });
     }
 <#------------------------------------------------------pagination function -->
    function nextPrevDocumentList(url){
        url= '<@ofbizUrl>'+url+'</@ofbizUrl>';
         jQuery.ajax({
            url: url,
            type: 'POST',
            error: function(msg) {
                showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.ErrorLoadingReport} : " + msg);
            },
            success: function(msg) {
                jQuery('#Document').html(msg);
            }
        });
    }

</script>

<style>
<#if tabButtonItem?has_report>
    <#if tabButtonItem=="LookupReportTree"||tabButtonItem=="LookupDetailReportTree">
        body{background:none;}
        .left-border{float:left;width:25%;}
        .reportarea{margin: 0 0 0 0.5em;padding:0 0 0 0.5em;}
        .leftonly{float:none;min-height:25em;}
    </#if>
</#if>
</style>

<div id="tree"></div>
