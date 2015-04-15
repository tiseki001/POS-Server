package org.ofbiz.querydataforsales;

public class Customer {
	//会员姓名
private  String partyId;
private  String firstName;
private  String middleName;
private String lastName;
private String phoneMobile;

public String getPartyId() {
	return partyId;
}
public void setPartyId(String partyId) {
	this.partyId = partyId;
}
public String getFirstName() {
	return firstName;
}
public void setFirstName(String firstName) {
	this.firstName = firstName;
}
public String getMiddleName() {
	return middleName;
}
public void setMiddleName(String middleName) {
	this.middleName = middleName;
}
public String getLastName() {
	return lastName;
}
public void setLastName(String lastName) {
	this.lastName = lastName;
}
public String getPhoneMobile() {
	return phoneMobile;
}
public void setPhoneMobile(String phoneMobile) {
	this.phoneMobile = phoneMobile;
}

}
