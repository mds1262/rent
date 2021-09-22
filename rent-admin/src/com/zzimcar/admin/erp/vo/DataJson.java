package com.zzimcar.admin.erp.vo;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DataJson {
	
	// 인스 데이터목록
	@SerializedName("resCode")
	public String ins_resCode;
	
	@SerializedName("resMsg")
	public String ins_resMsg;
	
	@SerializedName("resID")
	public String ins_resID;
	
	@SerializedName("R회사목록")
    public List<InsCompanyList> insCompanyList;
	
	@SerializedName("모델목록")
    public List<InsCarList> insCarList;
	
	@SerializedName("보험목록")
	public List<InsPubList> insPubList;
	
	//인스 렌트카 회사목록
	public class InsCompanyList{
		
		@SerializedName("연결번호")
		public String connectNo;
		
		@SerializedName("L렌트회사코드")
		public String ins_rentComCode;
		
		@SerializedName("R렌트회사")
		public String ins_rentCompany;
		
		@SerializedName("R거래처")
		public String ins_accountName;
		
		@SerializedName("거래상태")
		public String ins_dealStatus;
		
		@SerializedName("R차량수수료")
		public String ins_carFees;
		
		@SerializedName("R자차수수료")
		public String ins_insuranceFees ;
		
		@SerializedName("R트래픽비용처리")
		public String ins_trafficCost ;
		
		@SerializedName("R취소가능시간")
		public String ins_cancelableTime;
		
	}
	
	//인스 렌트카 차량목록
	public class InsCarList{
		
		@SerializedName("L모델번호")
		public String modelNo;
		
		@SerializedName("L모델명")
		public String modelName;
		
		@SerializedName("L모델코드")
		public String modelCode;
		
		@SerializedName("L분류")
		public String carSize;
		
		@SerializedName("L렌트회사코드")
		public String rentCarCompanyCode;
		
		@SerializedName("R렌트회사")
		public String rentCarName;
		
		@SerializedName("R모델번호")
		public String rentCarModelNo;
		
		@SerializedName("R모델명")
		public String rentCarModelName;
		
		@SerializedName("R모델옵션")
		public String rentCarModelOption;
		
		@SerializedName("R연료")
		public String rentCarFuel;
		
		@SerializedName("R승차인원")
		public String rentCarPersonnel;
		
		@SerializedName("R가능연령")
		public String rentCarAvailableAge;
		
		@SerializedName("R연식")
		public String rentCarYear;
		
		@SerializedName("R구동방식")
		public String rentCarDrivingMethod;
		
		@SerializedName("R배기량")
		public String rentCarCc;
		
		@SerializedName("R변속기")
		public String rentCarTransmission;
		
		@SerializedName("R보유수량")
		public String rentCarCnt;
	}
	
	//인스 보험목록
	public class InsPubList{
		
		@SerializedName("L렌트회사코드")
		public String rentCarCompanyCode;
		
		@SerializedName("R렌트회사")
		public String rentCarName;
		
		@SerializedName("R보험번호")
		public String pubNo;
		
		@SerializedName("R보험명")
		public String pubName;
		
		@SerializedName("R보상한도")
		public String compensationLimit;
		
		@SerializedName("R면책금")
		public String exemption;
		
		@SerializedName("R휴차보상료")
		public String reward;
		
		@SerializedName("R가입제한")
		public String joinRestrictions;
		
		@SerializedName("R보험내용")
		public String pubContents;
		
		@SerializedName("R적용모델")
		public String applyModel;
		
		@SerializedName("R계산법")
		public String calculus;
		
		@SerializedName("R보험요금DATA")
		public String pubFeeData;
	}
	
	
	public List<InsPubList> getInsPubList() {
		return insPubList;
	}

	public void setInsPubList(List<InsPubList> insPubList) {
		this.insPubList = insPubList;
	}

	public List<InsCarList> getInsCarList() {
		return insCarList;
	}

	public void setInsCarList(List<InsCarList> insCarList) {
		this.insCarList = insCarList;
	}

	public String getIns_resCode() {
		return ins_resCode;
	}

	public void setIns_resCode(String ins_resCode) {
		this.ins_resCode = ins_resCode;
	}

	public String getIns_resMsg() {
		return ins_resMsg;
	}

	public void setIns_resMsg(String ins_resMsg) {
		this.ins_resMsg = ins_resMsg;
	}

	public String getIns_resID() {
		return ins_resID;
	}

	public List<InsCompanyList> getInsCompanyList() {
		return insCompanyList;
	}

	public void setInsCompanyList(List<InsCompanyList> insCompanyList) {
		this.insCompanyList = insCompanyList;
	}

	public void setIns_resID(String ins_resID) {
		this.ins_resID = ins_resID;
	}

}
