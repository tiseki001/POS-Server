<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jsTree/jquery.jstree.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
function unescapeHtmlText(text) {
    return jQuery('<div />').html(text).text()
}

jQuery(document).ready(createTree());

var rawdata = [

	<#assign toReportList  = Static["org.ofbiz.report.UtilReport"].toReportList(reportList)/>

      <@fillTree assocList = toReportList/>
      
      <#macro fillTree assocList>
            {
            "data": {"title" : unescapeHtmlText("报表"), "attr": {"href": "javascript:void(0);"}},
				"attr": {"id":"reportIdNo1"}
				,"children": [
					  <#if (assocList?has_content)>
			             <#list assocList as assoc>
			                <#assign report  = delegator.findByPrimaryKey("Report",Static["org.ofbiz.base.util.UtilMisc"].toMap("reportId",assoc))/>
			                {
			                "data": {"title" : unescapeHtmlText("${report.reportName}"), "attr": {"href": "javascript:void(0);", "onClick" : "callDocument('${report.reportType}');"}},
							"attr": {"id":"${assoc}"}
							<#if assoc_has_next>
			                },
			                <#else>
			                }
			                </#if>
			            </#list>
			         </#if>
                ]
            }
        </#macro>
     ];

  var editDocumentTreeUrl = '<@ofbizUrl>/views/EditDocumentTree</@ofbizUrl>';
  var listDocument =  '<@ofbizUrl>/views/ListDocument</@ofbizUrl>';
  var editDocumentUrl = '<@ofbizUrl>/views/EditDocument</@ofbizUrl>';
  var deleteDocumentUrl = '<@ofbizUrl>removeDocumentFromTree</@ofbizUrl>';

  function createTree() {
    jQuery(function () {
        jQuery("#tree").jstree({
            "plugins" : [ "themes", "json_data", "ui", "contextmenu", "crrm"],
            "json_data" : {
                "data" : rawdata,
                "progressive_render" : false
            },
            'contextmenu': {
                'items': {
                    'ccp' : false,
                    'create' : false,
                    'rename' : false,
                    'remove' : false,
                    <#if hasPermission>
                    	'create1' : {
                        'label' : "添加",
                        'action' : function(obj) {
                            callCreateDocumentTree(obj.attr('id'));
                        }
                    },
                    </#if>
                    <#if hasPermission>
                    'rename' : {
                        'label' : "编辑",
                        'action' : function(obj) {
                        	if(obj.attr('id') !="reportIdNo1"){
                        		callRenameDocumentTree(obj.attr('id'));
                        	}
                        }
                    },
                    </#if>
                    <#if hasPermission>
                    'delete1' : {
                        'label' : "删除",
                        'action' : function(obj) {
                        	if(obj.attr('id') !="reportIdNo1"){
                            	callDeleteDocument(obj.attr('id'), obj.attr('id'));
                            }
                        }
                    },
                    </#if>
                }
            }
        });
    });
  }

    function callDocument(reportType) {
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
            data: {"reportId" : reportType},
            error: function(msg) {
                showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.ErrorLoadingReport} : " + msg);
            },
            success: function(msg) {
                jQuery('#Document').html(msg);
            }
        });
     }
     
      function callCreateDocumentTree(reportId) {
        jQuery.ajax({
            url: editDocumentTreeUrl,
            type: 'POST',
            data: {reportId: reportId},
            error: function(msg) {
                showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.ErrorLoadingReport} : " + msg);
            },
            success: function(msg) {
                jQuery('#Document').html(msg);
            }
        });
    }
<#-------------------------------------------------------------------------------------删除-->
    function callDeleteDocument(reportId) {
        jQuery.ajax({
            url: deleteDocumentUrl,
            type: 'POST',
            data: {reportId : reportId},
            error: function(msg) {
                showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.ErrorLoadingReport} : " + msg);
            },
            success: function(msg) {
                location.reload();
            }
        });
    }
 <#-------------------------------------------------------------------------------------编辑-->
    function callRenameDocumentTree(reportId) {
        jQuery.ajax({
            url: editDocumentTreeUrl,
            type: 'POST',
            data: {reportId: reportId,rname:reportId},
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
    <#if tabButtonItem=="LookupReportTree"||tabButtonItem=="LookupDetailReportTree">
        body{background:none;}
        .left-border{float:left;width:25%;}
        .reportarea{margin: 0 0 0 0.5em;padding:0 0 0 0.5em;}
        .leftonly{float:none;min-height:25em;}
	</#if>
</style>

<div id="tree"></div>


