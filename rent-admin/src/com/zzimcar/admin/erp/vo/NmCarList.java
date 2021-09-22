package com.zzimcar.admin.erp.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class NmCarList {

	private String code;		// 모델코드
	private String name;		// 모델명
	private String gubun;		// 차종구분코드
	private String gear;		// 기어코드
	private String maker;		// 제조사
	private String fuel;		// 유류타입
	private String baegi;		// 배기량
	private String jeongwon;	// 정원수

	public NmCarList() {}
	
	public NmCarList(String code,
					 String name,
					 String gubun,
					 String gear,
					 String maker,
					 String fuel,
					 String baegi,
					 String jeongwon){
		super();
		this.code     = code;
		this.name  	  = name;
		this.gubun    = gubun;
		this.gear     = gear;
		this.maker    = maker;
		this.fuel     = fuel;
		this.baegi    = baegi;
		this.jeongwon = jeongwon;
	}

	// Setter & Getter
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGubun() {
		return gubun;
	}

	public void setGubun(String gubun) {
		this.gubun = gubun;
	}

	public String getGear() {
		return gear;
	}

	public void setGear(String gear) {
		this.gear = gear;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public String getFuel() {
		return fuel;
	}

	public void setFuel(String fuel) {
		this.fuel = fuel;
	}

	public String getBaegi() {
		return baegi;
	}

	public void setBaegi(String baegi) {
		this.baegi = baegi;
	}

	public String getJeongwon() {
		return jeongwon;
	}

	public void setJeongwon(String jeongwon) {
		this.jeongwon = jeongwon;
	}
	
}
