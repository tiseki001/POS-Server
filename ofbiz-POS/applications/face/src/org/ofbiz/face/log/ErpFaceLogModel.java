package org.ofbiz.face.log;

public class ErpFaceLogModel {
	private String logId ;
	public String getLogId() {
		return logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	private String model;
	private String event ;
	private String recordId;
	private String result;
	private String state ;
	private String resultInfo;
	
	public String getResultInfo() {
		return resultInfo;
	}
	public void setResultInfo(String resultInfo) {
		this.resultInfo = resultInfo;
	}
	public ErpFaceLogModel(String model,String event,String recordId,String result,String state,String resultInfo){
		this.model = model;
		this.event = event;
		this.recordId = recordId;
		this.result = result;
		this.state = state;
		this.resultInfo = resultInfo;
	}
	//public ErpFaceLogModel

}
