package org.ofbiz.query;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelViewEntity;
import org.ofbiz.entity.model.ModelViewEntity.ModelMemberEntity;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

public class QueryWorker {
	public static BasePosObject select(DispatchContext dctx,Map<String,Object> view){
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        
        String name = delegator.getDelegatorName();
        BasePosObject pObject = new BasePosObject();
        EntityCondition mainCond = null;
        List<GenericValue> resultList=null;
        String strResult="";
        
        
        String viewName="";
        String where="";
        String startstr="";
        String numberstr="";
        
        List<String> fieldsToSelect = null;
        List<String> orderBy  = null;
        int start = 0;
        int number = 0;
        EntityListIterator eli = null; 
        try {
        Set<String> fieldsToSelectSet  = null;
        if (UtilValidate.isNotEmpty(view)) {
        	
        	viewName = view.get("ViewName").toString();
        	where = view.get("Where")==null?"":view.get("Where").toString();
        	startstr = view.get("Start")==null?"":view.get("Start").toString();
        	numberstr = view.get("Number")==null?"":view.get("Number").toString();
        	orderBy = view.get("OrderBy")==null?null:(List<String>)view.get("OrderBy");
        	fieldsToSelect = view.get("Fields")==null?null:(List<String>)view.get("Fields");
        	//int a = fieldsList.size();
        }
        //String a1 = EntityCondition.makeConditionDate("fromDate", "thruDate").toString();
        if(!startstr.equals("")){
        	start = Integer.parseInt(startstr);
        }
        if(!numberstr.equals("")){
        	number = Integer.parseInt(numberstr);
        }else{
        	number = 1000;
        }
        EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, -1, true);
        ModelEntity model = delegator.getModelEntity(viewName);
        List<EntityCondition> andExprs = FastList.newInstance();
        ModelViewEntity entityModel = null;
        Class c = model.getClass();
        if(c.getName().equals( ModelViewEntity.class.getName())){
        	entityModel = (ModelViewEntity)model;
        	Map<String, ModelMemberEntity> memberEntitys = entityModel.getMemberModelMemberEntities();
            ModelMemberEntity memberEntity = null;
            ModelEntity entity;
            //TimeStamp.NTP_DATE_FORMAT
            for (String member :  memberEntitys.keySet()) {
            	memberEntity = memberEntitys.get(member);
            	entity = delegator.getModelEntity(memberEntity.getEntityName());
            	for (String pk :  entity.getPkFieldNames()){
            		if(pk.toUpperCase().equals("FROMDATE")){
            			andExprs.add(EntityCondition.makeConditionDate(memberEntity.getEntityAlias()+".FROM_DATE", memberEntity.getEntityAlias()+".THRU_DATE"));
            		}
            			
            	}
            	
            }
        }else{
        	for (String pk :  model.getPkFieldNames()){
        		if(pk.toUpperCase().equals("FROMDATE")){
        			andExprs.add(EntityCondition.makeConditionDate("FROM_DATE", "THRU_DATE"));
        		}
        			
        	}
        	
        }
        
        
        if (UtilValidate.isNotEmpty(where))
        	andExprs.add(EntityCondition.makeConditionWhere(where));
        
        if (andExprs.size() > 0){ 
        	mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        	mainCond = EntityCondition.makeConditionWhere(mainCond.toString());
        
        }
        if(fieldsToSelect!=null&&fieldsToSelect.size()>0)
        	
        	fieldsToSelectSet = new HashSet(fieldsToSelect);
        	
        	TransactionUtil.begin();
        	eli = delegator.find(viewName, mainCond,null, fieldsToSelectSet, orderBy, findOpts);
        	if(number!=0){
        		resultList = eli.getPartialList(start, number);
        	}else{
        		resultList = eli.getCompleteList();
        	}
		//	resultList = delegator.findList(viewName, mainCond, fieldsToSelectSet, orderBy, findOpts, true);
			if(resultList!=null&&resultList.size()>0){
				pObject.setFlag("S");
				pObject.setData(resultList);
				
			}
			
			TransactionUtil.commit();
        } catch (Exception e) {
			// TODO Auto-generated catch block
        	pObject.setFlag("F");
        	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}finally{
			if(eli!=null){
				try {
					eli.close();
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
       
        
		return pObject;
	}
}
