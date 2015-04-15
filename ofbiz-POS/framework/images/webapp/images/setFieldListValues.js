/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
// *** getDependentDropdownValues allows to dynamically populate a dependent drop-down on change on its parent drop-down, doesn't require any fixed naming convention 
// request      = request calling the service which retrieve the info from the DB, ex: getAssociatedStateList
// paramKey     = parameter value used in the called service 
// paramField   = parent drop-down field Id (mainId)
// targetField  = dependent drop-down field Id (dependentId)
// responseName = result returned by the service (using a standard json response, ie chaining json request)
// keyName      = keyName of the dependent drop-down  
// descName     = name of the dependent drop-down description
// selected     = optional name of a selected option
// callback     = optional javascript function called at end
// hide         = optional boolean argument, if true the dependent drop-down field (targetField) will be hidden when no options are available else only disabled. False by default.
// hideTitle    = optional boolean argument (hide must be set to true), if true the title of the dependent drop-down field (targetField) will be hidden when no options are available else only disabled. False by default.
// inputField   = optional name of an input field    
// 				  this is to handle a specific case where an input field is needed instead of a drop-down when no values are returned by the request
// 				  this will be maybe extended later to use an auto-completed drop-down or a lookup, instead of straight drop-down currently, when there are too much values to populate
// 				  this is e.g. currently used in the Product Price Rules screen

//*** calls any service already mounted as an event
//由串号得出销售，用户信息
function getFieldsValue(responseName){
    jQuery.ajax({
        type: 'POST',
        url: 'getSaleOrderAndPersonView',
        data: [{
            name: 'sequenceId',
            value: jQuery('#2_lookupId_sequenceId').val()
        }],
        async: false,
        cache: false,
        success: function(result){
    		list = result[responseName];
	        if (list) {
	            jQuery.each(list, function(key, value){
	                if (typeof value == 'string') {
	                    values = value.split(': ');
	                    if (values[0] == 'productId') {
	                    	jQuery('#1_lookupId_productId').val(values[1])
						} else if (values[0] == 'storeId'){
							jQuery('#3_lookupId_productStoreId').val(values[1])
						} else if (values[0] == 'postingDate'){
							jQuery('#productDate_i18n').val(values[1])
						} else if (values[0] == 'rebatePrice'){
							jQuery('#price').val(values[1])
						} else if (values[0] == 'name'){
							jQuery('#customerName').val(values[1])
						} else if (values[0] == 'phoneMobile'){
							jQuery('#customerPhone').val(values[1])
						} else if (values[0] == 'address1'){
							jQuery('#customerAddr').val(values[1])
						}
	                } 
	            })
	        };
        }
    });
}
function setCostTotalValue(){
	var cost1 = parseInt(jQuery('#cost1').val());
	var cost2 = parseInt(jQuery('#cost2').val());
	var  patrn=/^\d*$/; 
	var total = null;
	if (patrn.test(cost1)) {  
		total = cost1;
	}
	if (patrn.test(cost2)) {  
		total = cost2;
	}
	if (patrn.test(cost1) && patrn.test(cost2)) {  
		total = cost1 + cost2;
	}
	jQuery('#costTotal').val(total);
}
function setStatusValue(){
	var auditorId = jQuery('#auditorId').val();
	if (auditorId) {  
		jQuery('#status').val("2");
		setAcceptFieldDisabled();
	}
}
function setStatusValue3(){
	var confirmor = jQuery('#confirmor').val();
	var confirmResult = jQuery('#confirmResult').val();
	if (confirmor && confirmResult) {  
		jQuery('#status').val("1");
		setQuoteFieldDisabled();
	}
}


function setAcceptFieldDisabled(){
	var status = jQuery('#status').val();
	//alert(status);
	// 审核 or 确认
	if (status == '1' || status == '2') {
		//基本信息
		jQuery("#0_lookupId_accepteStoreId").attr('disabled', 'disabled');
		jQuery("#status").attr('disabled', 'disabled');
		jQuery("#accepteDate_i18n").attr('disabled', 'disabled');
		jQuery("#auditorDate_i18n").attr('disabled', 'disabled');
		jQuery("#demo").attr('disabled', 'disabled');
		jQuery("#touchPartyId").attr('disabled', 'disabled');
		jQuery("#auditorId").attr('disabled', 'disabled');
		
		//商品信息
		jQuery("#1_lookupId_productId").attr('disabled', 'disabled');
		jQuery("#2_lookupId_sequenceId").attr('disabled', 'disabled');
		jQuery("#EditAccept_productSN").attr('disabled', 'disabled');
		jQuery("#3_lookupId_productStoreId").attr('disabled', 'disabled');
		jQuery("#productDate_i18n").attr('disabled', 'disabled');
		jQuery("#acceptedFacility").attr('disabled', 'disabled');
		jQuery("#price").attr('disabled', 'disabled');
		jQuery("#customerName").attr('disabled', 'disabled');
		jQuery("#customerPhone").attr('disabled', 'disabled');
		jQuery("#customerAddr").attr('disabled', 'disabled');
		jQuery("#attachment").attr('disabled', 'disabled');
		jQuery("#inspectUnit").attr('disabled', 'disabled');
		jQuery("#inspectNumber").attr('disabled', 'disabled');
		//受理信息
		jQuery("#EditAccept_initSAppearance").attr('disabled', 'disabled');
		jQuery("#EditAccept_faultDesc").attr('disabled', 'disabled');
		jQuery("#EditAccept_accepteDemo").attr('disabled', 'disabled');
		jQuery("#initSStatus").attr('disabled', 'disabled');
		jQuery("#faultCategory").attr('disabled', 'disabled');
	}
	// 已清 or 作废
	if (status == '3' || status == '4') {
		//基本信息
		jQuery("#0_lookupId_accepteStoreId").attr('disabled', 'disabled');
		jQuery("#status").attr('disabled', 'disabled');
		jQuery("#accepteDate_i18n").attr('disabled', 'disabled');
		jQuery("#auditorDate_i18n").attr('disabled', 'disabled');
		jQuery("#demo").attr('disabled', 'disabled');
		jQuery("#touchPartyId").attr('disabled', 'disabled');
		jQuery("#auditorId").attr('disabled', 'disabled');
		//商品信息
		jQuery("#1_lookupId_productId").attr('disabled', 'disabled');
		jQuery("#2_lookupId_sequenceId").attr('disabled', 'disabled');
		jQuery("#EditAccept_productSN").attr('disabled', 'disabled');
		jQuery("#3_lookupId_productStoreId").attr('disabled', 'disabled');
		jQuery("#productDate_i18n").attr('disabled', 'disabled');
		jQuery("#acceptedFacility").attr('disabled', 'disabled');
		jQuery("#price").attr('disabled', 'disabled');
		jQuery("#customerName").attr('disabled', 'disabled');
		jQuery("#customerPhone").attr('disabled', 'disabled');
		jQuery("#customerAddr").attr('disabled', 'disabled');
		jQuery("#attachment").attr('disabled', 'disabled');
		jQuery("#inspectUnit").attr('disabled', 'disabled');
		jQuery("#inspectNumber").attr('disabled', 'disabled');
		//受理信息
		jQuery("#EditAccept_initSAppearance").attr('disabled', 'disabled');
		jQuery("#EditAccept_faultDesc").attr('disabled', 'disabled');
		jQuery("#EditAccept_accepteDemo").attr('disabled', 'disabled');
		jQuery("#initSStatus").attr('disabled', 'disabled');
		jQuery("#faultCategory").attr('disabled', 'disabled');
		//维护信息
		jQuery("#4_lookupId_EditAccept_NewProductId").attr('disabled', 'disabled');
		jQuery("#5_lookupId_EditAccept_NewSequenceId").attr('disabled', 'disabled');
		jQuery("#EditAccept_NewSN").attr('disabled', 'disabled');
		jQuery("#EditAccept_maintainMethod").attr('disabled', 'disabled');
		jQuery("#EditAccept_account").attr('disabled', 'disabled');
		jQuery("#qualityResult").attr('disabled', 'disabled');
	}
}
function setQuoteFieldDisabled(){
	var status = jQuery('#status').val();
	//alert(status);
	// 已清 or 作废 or 确认
	if (status == '3' || status == '4' || status == '1') {
		//基本信息
		jQuery("#0_lookupId_accepteStoreId").attr('disabled', 'disabled');
		jQuery("#status").attr('disabled', 'disabled');
		jQuery("#accepteDate_i18n").attr('disabled', 'disabled');
		jQuery("#auditorDate_i18n").attr('disabled', 'disabled');
		jQuery("#EditQuote_demo").attr('disabled', 'disabled');
		jQuery("#touchPartyId").attr('disabled', 'disabled');
		jQuery("#auditorId").attr('disabled', 'disabled');
		
		//报价信息
		jQuery("#quoteAccount").attr('disabled', 'disabled');
		jQuery("#cost1").attr('disabled', 'disabled');
		jQuery("#cost2").attr('disabled', 'disabled');
		jQuery("#costTotal").attr('disabled', 'disabled');
		jQuery("#quoteDemo").attr('disabled', 'disabled');
		jQuery("#quoteDate_i18n").attr('disabled', 'disabled');
		jQuery("#offerer").attr('disabled', 'disabled');
		jQuery("#department").attr('disabled', 'disabled');
		//报价确认
		jQuery("#confirmResult").attr('disabled', 'disabled');
		jQuery("#confirmDate_i18n").attr('disabled', 'disabled');
		jQuery("#confirmor").attr('disabled', 'disabled');
		jQuery("#confirmDemo").attr('disabled', 'disabled');
	}
}
function setAcceptParty(targetField, paramField){
	target = '#' + targetField;
	//var storeId = jQuery('#' + paramField).val();
	if (storeId && storeId != undefined) {
	    optionList = '';
	    jQuery.ajax({
	        url: 'getPartyByStoreId',
	        data: [{
	            name: 'storeId',
	            value: jQuery('#' + paramField).val()
	        }], // get requested value from parent drop-down field
	        async: false,
	        type: 'POST',
	        success: function(result){
	            list = result['partyList'];
	            // Create and show dependent select options            
	            if (list) {
	                jQuery.each(list, function(key, value){
	                    if (typeof value == 'string') {
	                        values = value.split(': ');
	                        if (values[1].indexOf(selected) >= 0 && selected.length > 0) {
	                            optionList += "<option selected='selected' value = " + values[1] + " >" + values[0] + "</option>";
	                        } else {
	                            optionList += "<option value = " + values[1] + " >" + values[0] + "</option>";
	                        }
	                    }
	                })
	            };
	        },
	        complete: function(){
	            jQuery(target).html(optionList).click().change();
	        }
	    });
	}
}

window.onload = function(){	
	setAcceptFieldDisabled();
	setQuoteFieldDisabled();
	//setOptBusiness('getOperatorList', 'productSalesPolicyId', 'changeId', 'targetId', 'operatorList');
}

function setOptBusiness(request, paramKey, paramField, targetField, responseName, productStoreId){
	target = '#' + targetField;
	//alert(productStoreId+":"+jQuery('#0_lookupId_storeId').val());
	valueStr = jQuery('#' + paramField).val() + "-" + jQuery('#0_lookupId_storeId').val();
	optionList = '';
    jQuery.ajax({
        url: request,
        data: [{
        	name: paramKey,
            value: valueStr
        }], // get requested value from parent drop-down field
        async: false,
        type: 'POST',
        success: function(result){
            list = result[responseName];
            // Create and show dependent select options            
            if (list) {
                jQuery.each(list, function(key, value){
                    if (typeof value == 'string') {
                    	values = value.split(': ');
                    	optionList += "<option value = " + values[1] + " >" + values[0] + "</option>";
                    }
                })
            };
        },
        complete: function(){
        	jQuery(target).html(optionList).click().change();
        }
    });
}

//受理信息 
function setAcceptValues () {
	var jsonStr = "{\"flag\":\"accept\",\"docId\":\"" + jQuery("#docId").val() +
	"\",\"accepteStoreId\":\"" + jQuery("#0_lookupId_accepteStoreId").val() + 
	"\",\"status\":\"" + jQuery("#status").val() + 
	"\",\"touchPartyId\":\"" + jQuery("#touchPartyId").val() + 
	"\",\"auditorId\":\"" + jQuery("#auditorId").val() +
	"\",\"accepteDate\":\"" + jQuery("#accepteDate_i18n").val() + 
	"\",\"auditorDate\":\"" + jQuery("#auditorDate_i18n").val() + 
	"\",\"productDate\":\"" + jQuery("#productDate_i18n").val() + 
	"\",\"demo\":\"" + jQuery("#demo").val() + 
	"\",\"productId\":\"" + jQuery("#1_lookupId_productId").val() + 
	"\",\"sequenceId\":\"" + jQuery("#2_lookupId_sequenceId").val() +
	"\",\"productSN\":\"" + jQuery("#EditAccept_productSN").val() +
	"\",\"productStoreId\":\"" + jQuery("#3_lookupId_productStoreId").val() +
	"\",\"acceptedFacility\":\"" + jQuery("#acceptedFacility").val() + 
	"\",\"price\":\"" + jQuery("#price").val() + 
	"\",\"customerName\":\"" + jQuery("#customerName").val() + 
	"\",\"customerPhone\":\"" + jQuery("#customerPhone").val() + 
	"\",\"customerAddr\":\"" + jQuery("#customerAddr").val() + 
	"\",\"attachment\":\"" + jQuery("#attachment").val() + 
	"\",\"inspectUnit\":\"" + jQuery("#inspectUnit").val() + 
	"\",\"inspectNumber\":\"" + jQuery("#inspectNumber").val() + 
	"\",\"initSAppearance\":\"" + jQuery("#EditAccept_initSAppearance").val() + 
	"\",\"faultDesc\":\"" + jQuery("#EditAccept_faultDesc").val() + 
	"\",\"accepteDemo\":\"" + jQuery("#EditAccept_accepteDemo").val() + 
	"\",\"initSStatus\":\"" + jQuery("#initSStatus").val() + 
	"\",\"faultCategory\":\"" + jQuery("#faultCategory").val() + 
	"\",\"NewProductId\":\"" + jQuery("#4_lookupId_EditAccept_NewProductId").val() + 
	"\",\"NewSequenceId\":\"" + jQuery("#5_lookupId_EditAccept_NewSequenceId").val() + 
	"\",\"NewSN\":\"" + jQuery("#EditAccept_NewSN").val() + 
	"\",\"maintainMethod\":\"" + jQuery("#EditAccept_maintainMethod").val() + 
	"\",\"account\":\"" + jQuery("#EditAccept_account").val() + 
	"\",\"qualityResult\":\"" + jQuery("#qualityResult").val() + 
	"\"}";
	jQuery("#jsonStr").val(jsonStr);
}


//报价信息 
function setQuoteValues () {
	var jsonStr = "{\"flag\":\"quote\",\"offerer\":\"" + jQuery("#offerer").val() +
	"\",\"docId\":\"" + jQuery("#docId").val() + 
	"\",\"accepteStoreId\":\"" + jQuery("#0_lookupId_accepteStoreId").val() + 
	"\",\"status\":\"" + jQuery("#status").val() + 
	"\",\"touchPartyId\":\"" + jQuery("#touchPartyId").val() + 
	"\",\"auditorId\":\"" + jQuery("#auditorId").val() + 
	"\",\"demo\":\"" + jQuery("#demo").val() + 
	"\",\"department\":\"" + jQuery("#department").val() + 
	"\",\"cost1\":\"" + jQuery("#cost1").val() + 
	"\",\"cost2\":\"" + jQuery("#cost2").val() + 
	"\",\"costTotal\":\"" + jQuery("#costTotal").val() +
	"\",\"quoteAccount\":\"" + jQuery("#quoteAccount").val() +
	"\",\"quoteDemo\":\"" + jQuery("#quoteDemo").val() + 
	"\",\"confirmor\":\"" + jQuery("#confirmor").val() + 
	"\",\"confirmResult\":\"" + jQuery("#confirmResult").val() +
	"\",\"confirmDemo\":\"" + jQuery("#confirmDemo").val() +
	"\",\"quoteDate\":\"" + jQuery("#quoteDate_i18n").val() + 
	"\",\"confirmDate\":\"" + jQuery("#confirmDate_i18n").val() + 
	"\"}";
	jQuery("#jsonStr").val(jsonStr);
}


//现金提成信息 
function setCommissionValues () {
	var jsonStr = "{\"flag\":\"accept\",\"id\":\"" + jQuery("#id").val() +
	"\",\"startDate\":\"" + jQuery("#startDate").val() + 
	"\",\"endDate\":\"" + jQuery("#endDate").val() + 
	"\",\"storeId\":\"" + jQuery("#storeId").val() + 
	"\",\"type\":\"" + jQuery("#type").val() +
	"\",\"role\":\"" + jQuery("#role").val() + 
	"\",\"base\":\"" + jQuery("#base").val() + 
	"\",\"qualify\":\"" + jQuery("#qualify").val() + 
	"\",\"award\":\"" + jQuery("#award").val() + 
	"\"}";
	jQuery("#jsonStr").val(jsonStr);
}
//现金奖励信息 
function setAwardValues () {
	var jsonStr = "{\"flag\":\"award\",\"id\":\"" + jQuery("#id").val() +
	"\",\"startDate\":\"" + jQuery("#startDate").val() + 
	"\",\"endDate\":\"" + jQuery("#endDate").val() + 
	"\",\"storeId\":\"" + jQuery("#storeId").val() + 
	"\",\"productId\":\"" + jQuery("#productId").val() + 
	"\",\"role\":\"" + jQuery("#role").val() + 
	"\",\"salesModel\":\"" + jQuery("#salesModel").val() + 
	"\",\"qualify\":\"" + jQuery("#qualify").val() + 
	"\",\"award\":\"" + jQuery("#award").val() + 
	"\"}";
	jQuery("#jsonStr").val(jsonStr);
}
//积分基数信息 
function setBaseValues () {
	var jsonStr = "{\"flag\":\"base\",\"id\":\"" + jQuery("#id").val() +
	"\",\"startDate\":\"" + jQuery("#startDate").val() + 
	"\",\"endDate\":\"" + jQuery("#endDate").val() + 
	"\",\"storeId\":\"" + jQuery("#storeId").val() + 
	"\",\"role\":\"" + jQuery("#role").val() + 
	"\",\"type\":\"" + jQuery("#type").val() + 
	"\",\"base\":\"" + jQuery("#base").val() + 
	"\"}";
	jQuery("#jsonStr").val(jsonStr);
}
//积分倍率信息 
function setRateValues () {
	var jsonStr = "{\"flag\":\"rate\",\"id\":\"" + jQuery("#id").val() +
	"\",\"startDate\":\"" + jQuery("#startDate").val() + 
	"\",\"endDate\":\"" + jQuery("#endDate").val() + 
	"\",\"storeId\":\"" + jQuery("#storeId").val() + 
	"\",\"productId\":\"" + jQuery("#productId").val() + 
	"\",\"rate\":\"" + jQuery("#rate").val() + 
	"\"}";
	jQuery("#jsonStr").val(jsonStr);
}


//由productId，获取product信息
function getValue(responseName){
    alert{};
}


