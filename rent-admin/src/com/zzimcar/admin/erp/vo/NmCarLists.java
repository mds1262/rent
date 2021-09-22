package com.zzimcar.admin.erp.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response") 
@XmlAccessorType (XmlAccessType.FIELD)
public class NmCarLists {
	
	@XmlElement(name = "item") 
	
	private List<NmCarList> nmCarLists = null; 
	
	public List<NmCarList> getNmCarLists() { 
		return nmCarLists; 
	} 
	
	public void setNmCarLists(List<NmCarList> nmCarLists) { 
		this.nmCarLists = nmCarLists; 
	}

}
