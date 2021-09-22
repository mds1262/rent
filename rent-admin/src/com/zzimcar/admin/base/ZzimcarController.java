package com.zzimcar.admin.base;

import java.util.HashMap;
import java.util.Map;

import com.zzimcar.admin.config.ZzimcarConstants.RES_STATUS;
import com.zzimcar.admin.utils.PropConst;

public abstract class ZzimcarController {

	public static final String VIEW_ROOT_PC	= "web";
	public static final String VIEW_ROOT_MO	= "/mobile";	
	
	// ERP
	public static final String ERP_INS = "INS";
	public static final String INS_URL = "http://rt.wrent.seejeju.com/RTAPI/ws.aspx";	// INS
	public static final String INS_UID = "zzimcar";
	public static final String INS_ERPCODE = "2";
	public static final String INS_PASSCODE = "CPKYW6ETCWFKU27E3DDI";
	public static final String INS_CMD_CARLIST = "carlist";								// 차량리스트
	public static final String INS_CMD_COMPANYLIST = "companylist";						// 회사리스트
	public static final String INS_CMD_INSLIST_R = "inslist_r";							// 보험리스트 R
	public static final String INS_CMD_INSLIST = "inslist";								// 보험리스트
	
	
	
	public static final String ERP_JEJU_GRIM = "JEJU_GRIM";
	public static final String JEJU_GRIM_FIRST_URL="http://data.";
	public static final String JEJU_GRIM_SECOND_URL="/basic/";
	
	public static final String ERP_REBORN = "REBORN";
	public static final String REBORN_URL= "https://api.roof926.com/api/v1/external/carModelInfo.xml";
	//public static final String CRTFCKEY = "29500knbv157ksv9"; 		// TEST
	//public static final String CRTFCAUTHORKEY = "95l17an53ph2c3r9";   // TEST
	public static final String CRTFCKEY = "7f3htb9p96du5cm4"; 		    // REAL 
	public static final String CRTFCAUTHORKEY = "so21z3z5x5h080fj";		// REAL
	
	
	public static final String JEJU_GRIM_CARLIST = "carinfo.php";
	public static final String REBORN_CARLIST = "carModelInfo.xml";
	
	protected Map<String, Object> resultJsonSuc() {
		return resultJson(RES_STATUS.HTTP_OK, PropConst.HTTP_STATUS_OK, null );
	}
	protected Map<String, Object> resultJsonSuc(Map<String, Object> resData ) {
		return resultJson(RES_STATUS.HTTP_OK, PropConst.HTTP_STATUS_OK, resData );
	}
	protected Map<String, Object> resultJsonError() {
		return resultJson(RES_STATUS.HTTP_SERVER_ERROR, PropConst.HTTP_STATUS_ERR_SYSTEM, null );
	}
	protected Map<String, Object> resultJsonError(String resMsg) {
		return resultJson(RES_STATUS.HTTP_SERVER_ERROR, resMsg, null );
	}
	protected Map<String, Object> resultJson(int resCode, String resMsg, Map<String, Object> resData ) {
		Map<String, Object> resJson = new HashMap<String, Object>();
		resJson.put("res_code", resCode);
		if( resMsg != null ) resJson.put("message", resMsg);
		if( resData != null ) resJson.put("data", resData);

		return resJson;
	}
	
}
