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
        <h3> ${uiLabelMap.ProductConditions} </h3>
    </div>
    <div class="screenlet-body">
    <form method='post' action='<@ofbizUrl>CreateProductPromoParty</@ofbizUrl>' id="_form_CreateProductPromoParty" name="selectAllForm">
       <table border="1" class="basic-table" id="tab">
          <tr >
          	<td colspan="5" align="center">
          		<input type="hidden" name="productPromoId" value="${productPromoId}" />
            </td>
           </tr>
          <tr class="header-row">
            <td ><b></b></td>
            <td ><b>${uiLabelMap.Left1}</b></td>
            <td ><b>${uiLabelMap.fieldName}</b></td>
            <td ><b>${uiLabelMap.Operate}</b></td>
            <td ><b>${uiLabelMap.Value1}</b></td>
            <td ><b>${uiLabelMap.Right1}</b></td>
            <td ><b>AND/OR</b></td>
            <td ><b>${uiLabelMap.CommonRemove}</b></td>
          </tr>
          <#assign rowCount = 2>
          <#list productPromoPartys as productPromoParty>
          	<tr class="header-row" id="${rowCount}">
          	  <td><input type='hidden' name="rowCount_o_${rowCount}" id="rowCount${rowCount}" value="${rowCount}"/></td>
	          <td><input type='text' name="left1_o_${rowCount}" id="left1${rowCount}" value="${productPromoParty.left1?if_exists}" size='15'/></td>
              <td><input type='text' name="fieldName_o_${rowCount}" id="fieldName${rowCount}" value="${productPromoParty.fieldName?if_exists}" size='15'/></td>
              <td><input type='text' name="operate_o_${rowCount}" id="operate${rowCount}" value="${productPromoParty.operate?if_exists}" size='15'/></td>
              <td><input type='text' name="value1_o_${rowCount}" id="value1${rowCount}" value="${productPromoParty.value1?if_exists}" size='15'/></td>
              <td><input type='text' name="right1_o_${rowCount}" id="right1${rowCount}" value="${productPromoParty.right1?if_exists}" size='15'/></td>
              <td><input type='text' name="relation_o_${rowCount}" id="relation${rowCount}" value="${productPromoParty.relation?if_exists}" size='15'/></td>
              <td><a href="#" onclick="deltr(${rowCount})" class='button'>${uiLabelMap.CommonRemove}</a></td>
         	</tr>
         	<#assign rowCount = rowCount + 1>
          </#list>
        </table>
      </form>
    </div>
    <div class="screenlet-title-bar">
        <h3> ${uiLabelMap.AddSelectCond} </h3>
    </div>
    <div class="screenlet-body">
       <table border="1" class="basic-table" >
          <tr class="header-row">
            <td >${uiLabelMap.Left1}<input id="_left1" type="text" size="7" name="left1"></input></td>
            <td >${uiLabelMap.fieldName}
            	<span class="ui-widget">
				    <select id="_fieldName" size="1" name="fieldName">
				        <option value="PARTY_ID">会员编码</option>
				        <option value="GENDER">性别</option>
				        <option value="PHONE_MOBILE">手机号码</option>
				        <option value="LAST_NAME">姓氏</option>
				        <option value="FIRST_NAME">名称</option>
				    </select>
				</span>
            </td>
            <td ><b>${uiLabelMap.Operate}</b>
            	<span class="ui-widget">
				    <select id="_operate" size="1" name="operate">
				        <option value="EQ">${uiLabelMap.EQ}</option>
				        <option value="GT">${uiLabelMap.GT}</option>
				        <option value="GTE">${uiLabelMap.GTE}</option>
				        <option value="LT">${uiLabelMap.LT}</option>
				        <option value="LTE">${uiLabelMap.LTE}</option>
				        <option value="NEQ">${uiLabelMap.NEQ}</option>
				        <option value="LIKE">like</option>
				    </select>
				</span>
            </td>
            <td ><b>${uiLabelMap.Value1}</b>
            	<span class="ui-widget" id="del">
				    <input type='text' id='value1' name='value1' size='12'/>
				</span>
            </td>
            <td ><b>${uiLabelMap.Right1}</b>
            	<input id="_righ1" type="text" size="7" name="righ1"></input>
            </td>
            <td ><b>AND/OR</b>
            	<span class="ui-widget">
				    <select id="_relation" size="1" name="relation">
				        <option value="AND">AND</option>
				        <option value="OR">OR</option>
				    </select>
				</span>
            </td>
          </tr>
          <tr>
          	<td colspan="11" align="center">
	            <input type="button" value='${uiLabelMap.CommonAdd}' id="but"/>
	            <input type="button" value='${uiLabelMap.CommonSave}' id="commit"/>
            </td>
           </tr>
        </table>
    </div>
</div>



<script type="text/javascript" src="/images/jquery/jquery-1.7.min.js"></script>
<script>
	$(document).ready(function(){
	
	
	$("#commit").click(function(){
		$("#_form_CreateProductPromoParty").submit();
	});
	
	//<tr/>居中
	$("#tab tr").attr("align","center");
	 
		//增加<tr/>
		$("#but").click(function(){
			var operateTest = $("#_operate option:selected").text();
			var operateVal = $("#_operate option:selected").val();
			var leftVal = $("#_left1").val();
			var fieldNameVal = $("#_fieldName option:selected").val();
			// var value1Val = $("#_value1 option:selected").val();
			if(typeof value1Val == 'undefined'){
				value1Val = "";
			}
			// if(fieldNameVal == "PRODUCT"){
				value1Val = $("#value1").val();
			// }
			var righ1Val = $("#_righ1").val();
			var relationVal = $("#_relation").val();
			var _len = $("#tab tr").length;
			$("#tab").append("<tr class='header-row' id="+_len+" align='center'>"
							 +"<td><input type='hidden' name='rowCount_o_"+_len+"' id='rowCount"+_len+"' value='"+_len+"' size='15'/></td>"
	                         +"<td><input type='text' name='left1_o_"+_len+"' id='left1"+_len+"' value='"+leftVal+"' size='15'/></td>"
	                         +"<td><input type='text' name='fieldName_o_"+_len+"' id='fieldName"+_len+"' value='"+fieldNameVal+"' size='15'/></td>"
	                         +"<td><input type='text' name='operate_o_"+_len+"' id='operate"+_len+"' value='"+operateVal+"' size='15'/></td>"
	                         +"<td><input type='text' name='value1_o_"+_len+"' id='value1"+_len+"' value='"+value1Val+"' size='15'/></td>"
	                         +"<td><input type='text' name='right1_o_"+_len+"' id='right1"+_len+"' value='"+righ1Val+"' size='15'/></td>"
		                     +"<td><input type='text' name='relation_o_"+_len+"' id='relation"+_len+"' value='"+relationVal+"' size='15'/></td>"
		                     +"<td><a href=\'#\' onclick=\'deltr("+_len+")\' class='button'>${uiLabelMap.CommonRemove}</a></td></tr>");            
		})   
	})

	//删除<tr/>
	var deltr =function(index)
	{
		var _len = $("#tab tr").length;
		$("tr[id='"+index+"']").remove();//删除当前行
		for(var i=index+1,j=_len;i<j;i++)
		{
			var leftVal = $("#left1"+i).val();
			var fieldNameVal = $("#fieldName"+i).val();
			var operateVal = $("#operate"+i).val();
			var value1Val = $("#value1"+i).val();
			var righ1Val = $("#right1"+i).val();
			var relationVal = $("#relation"+i).val();
			
			$("tr[id=\'"+i+"\']")
			.replaceWith("<tr class='header-row' id="+(i-1)+" align='center'>"
				 +"<td><input type='hidden' name='rowCount_o_"+(i-1)+"' id='rowCount"+(i-1)+"' value='"+(i-1)+"' size='15'/></td>"
				 +"<td><input type='text' name='left1_o_"+(i-1)+"' value='"+leftVal+"' id='left1"+(i-1)+"' size='15'/></td>"
				 +"<td><input type='text' name='fieldName_o_"+(i-1)+"' value='"+fieldNameVal+"' id='fieldName"+(i-1)+"' size='15'/></td>"
				 +"<td><input type='text' name='operate_o_"+(i-1)+"' value='"+operateVal+"' id='operate"+(i-1)+"' size='15'/></td>"
				 +"<td><input type='text' name='value1_o_"+(i-1)+"' value='"+value1Val+"' id='value1"+(i-1)+"' size='15'/></td>"
				 +"<td><input type='text' name='right1_o_"+(i-1)+"' value='"+righ1Val+"' id='right1"+(i-1)+"' size='15'/></td>"
                 +"<td><input type='text' name='relation_o_"+(i-1)+"' value='"+relationVal+"' id='relation"+(i-1)+"' size='15'/></td>"
                 +"<td><a href=\'#\' onclick=\'deltr("+(i-1)+")\'class='button'>${uiLabelMap.CommonRemove}</a></td>"
                 +"</tr>");
		}  
		if(_len==3) {
			$("#tab").append("<tr class='header-row' align='center'>"
							 +"<td><input type='hidden' name='rowCount_o_0' value='delete' size='15'/></td>"
	                         +"</tr>");   
		}   
    }
    
</script>


