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
    <div class="screenlet-body">
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
		$("#_form_SaveSalesPolicyPackage").submit();
	});
	
	//<tr/>居中
	$("#tab tr").attr("align","center");
	 
		//增加<tr/>
		$("#but").click(function(){
			var operateTest = $("#AddSalesPolicyPackage_operate option:selected").text();
			var operateVal = $("#AddSalesPolicyPackage_operate option:selected").val();
			var leftVal = $("#AddSalesPolicyPackage_left1").val();
			var fieldNameVal = $("#AddSalesPolicyPackage_fieldName option:selected").val();
			var value1Val = $("#AddSalesPolicyPackage_value1 option:selected").val();
			var righ1Val = $("#AddSalesPolicyPackage_righ1").val();
			var relationVal = $("#AddSalesPolicyPackage_relation").val();
			var _len = $("#tab tr").length;
			$("#tab").append("<tr class='header-row' id="+_len+" align='center'>"
	                         +"<td><input type='text' name='left1_o_"+_len+"' id='left1"+_len+"' value='"+leftVal+_len+"' size='5'/></td>"
	                         +"<td><input type='text' name='fieldName_o_"+_len+"' id='fieldName"+_len+"' value='"+fieldNameVal+"' size='5'/></td>"
	                         +"<td><input type='text' name='operate_o_"+_len+"' id='operate"+_len+"' value='"+operateVal+"' size='5'/></td>"
	                         +"<td><input type='text' name='valu1_o_"+_len+"' id='value1"+_len+"' value='"+value1Val+"' size='5'/></td>"
	                         +"<td><input type='text' name='righ1_o_"+_len+"' id='righ1"+_len+"' value='"+righ1Val+"' size='5'/></td>"
		                     +"<td><input type='text' name='relation_o_"+_len+"' id='relation"+_len+"' value='"+relationVal+"' size='5'/></td>"
		                     +"<td><a href=\'#\' onclick=\'deltr("+_len+")\' class='button'>删除</a></td></tr>");            
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
			var righ1Val = $("#righ1"+i).val();
			var relationVal = $("#relation"+i).val();
			
			$("tr[id=\'"+i+"\']")
			.replaceWith("<tr class='header-row' id="+(i-1)+" align='center'>"
				 +"<td><input type='text' name='left1_o_"+(i-1)+"' value='"+leftVal+"' id='left1"+(i-1)+"' size='5'/></td>"
				 +"<td><input type='text' name='fieldName_o_"+(i-1)+"' value='"+fieldNameVal+"' id='fieldName"+(i-1)+"' size='5'/></td>"
				 +"<td><input type='text' name='operate_o_"+(i-1)+"' value='"+operateVal+"' id='operate"+(i-1)+"' size='5'/></td>"
				 +"<td><input type='text' name='valu1_o_"+(i-1)+"' value='"+value1Val+"' id='value1"+(i-1)+"' size='5'/></td>"
				 +"<td><input type='text' name='righ1_o_"+(i-1)+"' value='"+righ1Val+"' id='righ1"+(i-1)+"' size='5'/></td>"
                 +"<td><input type='text' name='relation_o_"+(i-1)+"' value='"+relationVal+"' id='relation"+(i-1)+"' size='5'/></td>"
                 +"<td><a href=\'#\' onclick=\'deltr("+(i-1)+")\'class='button'>删除</a></td>"
                 +"</tr>");
		}    
    }
</script>

