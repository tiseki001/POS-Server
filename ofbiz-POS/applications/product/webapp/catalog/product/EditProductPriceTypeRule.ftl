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
        <h3> ${uiLabelMap.MenuRole} </h3>
    </div>
    <#if (listSize > 0)>
        <table border="0" width="100%" cellpadding="2">
            <tr>
            <td align="right">
                <span class="label">
                <b>
                <#if (viewIndex > 0)>
                <a href="<@ofbizUrl>EditProductPriceTypeRule?productPriceTypeId=${productPriceTypeId?if_exists}&amp;VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
                </#if>
                ${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}
                <#if (listSize > highIndex)>
                | <a href="<@ofbizUrl>EditProductPriceTypeRule?productPriceTypeId=${productPriceTypeId?if_exists}&amp;VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
                </#if>
                </b>
                </span>
            </td>
            </tr>
        </table>
    </#if>
     <br />
    <div class="screenlet-body">
    <form method='post' action='<@ofbizUrl>CreateProductPriceTypeRule</@ofbizUrl>' name="selectAllForm">
        <input type="hidden" name="_checkGlobalScope" value="N" />
        <input type="hidden" name="productPriceTypeId" value="${productPriceTypeId}" />
        <table cellspacing="0" class="basic-table">
              <tr class="header-row">
                <td><b>${uiLabelMap.productPriceTypeId}</b></td>
                <td><b>${uiLabelMap.roleTypeId}</b></td>
                <td><b>${uiLabelMap.parentTypeId}</b></td>
                <td><b>${uiLabelMap.description}</b></td>
                <td><b>${uiLabelMap.IsVisble}</b></td>
                <td align="right"><b>${uiLabelMap.CommonAll}<input type="checkbox" name="selectAll" value="${uiLabelMap.CommonY}" onclick="javascript:toggleAll(this, 'selectAllForm');highlightAllRows(this, 'tableRow_', 'selectAllForm');" /></b></td>
             </tr>
        <#if (listSize > 0)>
            <#assign rowCount = 0>
            <#assign rowClass = "2">
            <#list lists as GenericValue>
            <tr id="tableRow_${rowCount}" valign="middle"<#if rowClass == "1"> class="alternate-row"</#if>>
           	   <td><input type="hidden" name="productPriceTypeId_o_${rowCount}" value="${productPriceTypeId}" />${productPriceTypeId}</td>
              <td><input type="hidden" name="roleTypeId_o_${rowCount}" value="${GenericValue.roleTypeId}" />${GenericValue.roleTypeId}</td>
              <td>${GenericValue.parentTypeId?if_exists}</td>
              <td>${GenericValue.description}</td>
              <td><input type="hidden" name="isVisble_o_${rowCount}" value="${GenericValue.isVisble?if_exists}" />${GenericValue.isVisble?if_exists}</td>
              <td align="right">
              	<#if GenericValue.isVisble?if_exists == "Y">
            		<input type="checkbox" checked name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, 'selectAllForm');highlightRow(this,'tableRow_${rowCount}');" />
	            <#else>
              		<input type="checkbox" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, 'selectAllForm');highlightRow(this,'tableRow_${rowCount}');" />
	            </#if>
              </td>
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
            <input type="hidden" name="_rowCount" value="${rowCount}" id="_rowCount"/>
            <input type="submit" value='${uiLabelMap.CommonUpdate}' /></td></tr>
        </#if>
        </table>
        </form>
    </div>
</div>


