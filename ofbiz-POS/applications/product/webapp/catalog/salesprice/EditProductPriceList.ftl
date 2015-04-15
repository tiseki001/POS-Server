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
<div class="screenlet">
    <div class="screenlet-title-bar">
        <h3> ${uiLabelMap.Jiageqindan} </h3>
    </div>
	<#if (listSize > 0)>
        <#if productId?has_content>
          <#assign productString = "&amp;productId=" + productId>
        </#if>
        <table border="0" width="100%" cellpadding="2">
            <tr>
            <td align="right">
                <span class="label">
                <b>
                <#if (viewIndex > 0)>
                <a href="<@ofbizUrl>EditProductPriceList?productSalesPolicyId=${productSalesPolicyId?if_exists}&amp;productPriceRuleId=${productPriceRuleId?if_exists}&amp;VIEW_SIZE=${viewSize}&amp;productId=${productId?if_exists}&amp;productName=${productName?if_exists}&amp;VIEW_INDEX=${viewIndex-1}${productString?if_exists}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
                </#if>
                ${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}
                <#if (listSize > highIndex)>
                | <a href="<@ofbizUrl>EditProductPriceList?productSalesPolicyId=${productSalesPolicyId?if_exists}&amp;productPriceRuleId=${productPriceRuleId?if_exists}&amp;productId=${productId?if_exists}&amp;productName=${productName?if_exists}&amp;VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex+1}${productString?if_exists}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
                </#if>
                </b>
                </span>
            </td>
            </tr>
        </table>
    </#if>
     <br />
    <div class="screenlet-body">
    <form method='post' action='<@ofbizUrl>UpdateProductPriceList</@ofbizUrl>' name="selectAllForm">
       <table cellspacing="" border="1" class="basic-table">
       	  <tr class="header-row" >
            <td rowspan="2" colspan="5" align="center"><b>${uiLabelMap.sp}</b></td>
            <td colspan="0" align="center"><b>${uiLabelMap.yjg}</b></td>
            <td rowspan="4" align="center">
            	<b>${uiLabelMap.sdtz}</b>
            </td>
          </tr>
          <tr class="header-row" >
            <td colspan="0" align="center"><b>${uiLabelMap.tzjg}</b></td>
          </tr>
          <tr class="header-row">
            <td rowspan="2"><b>${uiLabelMap.SPBM}</b></td>
            <td rowspan="2"><b>${uiLabelMap.SPMC}</b></td>
            <td rowspan="2"><b>${uiLabelMap.ruleName}</b></td>
            <td rowspan="2"><b>${uiLabelMap.policyName}</b></td>
            <td rowspan="2"><b>${uiLabelMap.productPriceType}</b></td>
            <td ><b>${uiLabelMap.yjg}</b></td>
          </tr>
          <tr class="header-row">
          	<td ><b>${uiLabelMap.tzjg}</b></td>
          </tr>
          <#assign rowCount = 0>
          <#assign rowClass = "2">
          <#list lists as GenericValue>
          <tr class="alternate-row" id="productPrice_tableRow_${rowCount}">
          	<input type="hidden" name="productPriceRuleId_o_${rowCount}" value="${GenericValue.productPriceRuleId}" />
            <td rowspan="2"><b><input type="hidden" name="productId_o_${rowCount}" value="${GenericValue.productId}" />${GenericValue.productId}</b></td>
            <td rowspan="2"><b>${GenericValue.productName?if_exists}</b> </td>
            <td rowspan="2"><b>${GenericValue.ruleName}</b></td>
            <td rowspan="2"><b><input type="hidden" name="productSalesPolicyId_o_${rowCount}" value="${GenericValue.productSalesPolicyId}"/>${GenericValue.policyName}</b></td>
            <td rowspan="2"><b><input type="hidden" name="productPriceTypeId_o_${rowCount}" value="${GenericValue.productPriceTypeId}"/>${GenericValue.description}</b></td>
            <td ><b>${GenericValue.price?if_exists}</b></td>
            <td rowspan="2" align="center">
            	<#if GenericValue.isManual == "Y">
            		<input type="checkbox" checked id="isManual_o_${rowCount}" name="isManual_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, 'selectAllForm');highlightRow(this,'productPrice_tableRow_${rowCount}');" />
	            <#else>
	            	<input type="checkbox" id="isManual_o_${rowCount} "name="isManual_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, 'selectAllForm');highlightRow(this,'productPrice_tableRow_${rowCount}');" />
	            </#if>
            </td>
          </tr>
          <tr class="alternate-row" id="productPrice_tableRow1_${rowCount}">
          	<td ><input type="text" size="5" id="price_o_${rowCount}" name="price_o_${rowCount}" value="" /></td>
          </tr>
          <#assign rowCount = rowCount + 1>
          <#-- toggle the row color -->
            <#if rowClass == "2">
              <#assign rowClass = "1">
            <#else>
              <#assign rowClass = "2">
            </#if>
            </#list>
           <tr><td colspan="11" align="center">
            <input type="hidden" name="_rowCount" id="_rowCount" value="${rowCount}" />
            <input type="submit" value='${uiLabelMap.CommonUpdate}' onClick="return true;"/>
            </td></tr>
        </table>
        </form>
    </div>
</div>


<script type="text/javascript" src="/images/jquery/jquery-1.7.min.js"></script>
<script>
	
	function checkValue(){
		var _rowCount = $("#_rowCount").val();
		for(var i=0;i<_rowCount;i++)
		{
			if($("#isManual_o_"+i).is(':checked')){
				 var p = $("#price_o_"+i).val();
				if(!p){
					alert("${uiLabelMap.CZNULLVALUE}");
					return false;
				}
			}
		}
		return true;
	}
    
</script>

