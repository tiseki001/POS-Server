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
        <h3> 商品列表 </h3>
    </div>
    <div class="screenlet-body">
     <form method='post' action='<@ofbizUrl>UpdateProductPromoSpecialProduct</@ofbizUrl>' name="selectAllForm">
        <input type="hidden" name="productPromoId" value="${productPromoId}" />
        <table cellspacing="0" class="basic-table">
              <tr class="header-row">
                <td><b>${uiLabelMap.CommonId}</b></td>
                <td><b>${uiLabelMap.productName}</b></td>
                <td><b>${uiLabelMap.brandName}</b></td>
                <td><b>${uiLabelMap.ProductFeature}</b></td>
                <td><b>${uiLabelMap.ProductCategory}</b></td>
                <td><b>${uiLabelMap.ProductPrice}</b></td>
             </tr>
        <#if (listSize > 0)>
            <#assign rowCount = 0>
            <#list lists as GenericValue>
            <tr id="productFeatureId_tableRow_${rowCount}"  class="alternate-row">
              <td><input type="hidden" id="productId_${rowCount}" name="productId_o_${rowCount}" value="${GenericValue.productId}" />${GenericValue.productId}</td>
              <td>${GenericValue.productName?if_exists}</td>
              <td>${GenericValue.brandName?if_exists}</td>
              <td>
          		<select id="value1_${rowCount}" size="1" name="value1">
			        <option value=""></option>
			    </select>
              </td>
              <td>
          		<select id="value2_${rowCount}" size="1" name="value2">
			        <option value=""></option>
			    </select>
              </td>
              <td><input type="text" name="productPrice_o_${rowCount}" value="${GenericValue.priceDetailText?if_exists}" size="7"/></td>
            </tr>
            <#assign rowCount = rowCount + 1>
            </#list>
            <tr><td colspan="11" align="center">
            <input type="hidden" name="_rowCount" value="${rowCount}" id="_rowCount"/>
            <input type="submit" value='${uiLabelMap.CommonUpdate}'/></td></tr>
        </#if>
        </table>
        </form>
    </div>
</div>


<script type="text/javascript" src="/images/jquery/jquery-1.7.min.js"></script>
<script>
	
	$(document).ready(function(){
		var rowCount = $("#_rowCount").val();
		for(var i=0;i<rowCount;i++)
		{
			var productId_ = $("#productId_"+i).val();
			getDependentDropdownValues('getProductCategorysByProductId', 'fieldName', 'productId_'+i, 'value1_'+i, 'productCategoryList', 'productFeatureId', 'description');
	      	getDependentDropdownValues('getProductFeaturesByProductId', 'fieldName', 'productId_'+i, 'value2_'+i, 'productFeatureList', 'productFeatureId', 'description');
			if (true) {
			  getDependentDropdownValues('getProductFeaturesByProductId', 'fieldName', 'productId_'+i, 'value2_'+i, 'productFeatureList', 'productFeatureId', 'description', '_none_');
		      getDependentDropdownValues('getProductCategorysByProductId', 'fieldName', 'productId_'+i, 'value1_'+i, 'productCategoryList', 'productFeatureId', 'description', '_none_');
		    };
		}
    });
    
</script>


