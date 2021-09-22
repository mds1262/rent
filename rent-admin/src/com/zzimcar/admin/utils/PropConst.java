package com.zzimcar.admin.utils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropConst {
	
	@Autowired
	@Resource(name="prop")
	public Prop prop;  
	 
	@PostConstruct
	public void init() {
		//로그인 처리 메세지
		PropConst.LOGIN_ERR_IDMSG = prop.get("login_err_idmsg");
		PropConst.LOGIN_ERR_PWMSG = prop.get("login_err_pwmsg");
		PropConst.LOGIN_ACCESS_FAIL = prop.get("login_access_fail");
		//Write Update 처리 메세지		
		PropConst.WRITE_SUCCESS_INSERT = prop.get("write_success_insert");
		PropConst.WRITE_FAIL_INSERT = prop.get("write_fail_insert");
		PropConst.WRITE_SUCCESS_UPDATE = prop.get("write_success_update");
		PropConst.WRITE_FAIL_UPDATE = prop.get("write_fail_update");
		PropConst.WRITE_ERROR_OCCURED = prop.get("write_error_occured");
		//Provider 메세지	
		PropConst.PROVIDER_SUCCESS_MSG = prop.get("provider_success_msg");
		PropConst.PROVIDER_FAIL_MSG = prop.get("provider_fail_msg");
		PropConst.PROVIDER_DELETE_SUCCESS = prop.get("provider_delete_success");
		PropConst.PROVIDER_DELETE_FAILED = prop.get("provider_delete_failed");
		PropConst.PROVIDER_KAKAO_ERROR = prop.get("provider_kakao_error");
		PropConst.PROVIDER_ALREADY_EXSIT = prop.get("provider_already_exsit");
		PropConst.PROVIDER_NOT_FOUND_LOCATION = prop.get("provider_not_found_location");
		PropConst.PROVIDER_DATE_FAIL_MSG = prop.get("provuder_date_fail_msg");
		PropConst.PROVIDER_ALREADY_DOMAIN_EXIST = prop.get("provider_already_domain_exist");
		//렌트카 메세지		
		PropConst.RENTCAR_FAIL_MSG = prop.get("rentcar_fail_msg");
		// 관리자 메세지
		PropConst.AUTHORITYDELETE_SUCCESS = prop.get("authority_delete_success");
		PropConst.AUTHORITYDELETE_FAIL = prop.get("authority_delete_fail");
		PropConst.IDAUTHORITYCHECK_SUCCESS = prop.get("idAuthority_check_success");
		PropConst.IDAUTHORITYCHECK_FAIL = prop.get("idAuthority_check_fail");
		//메뉴관리 메세지
		PropConst.NOMAR_DELETE_SUCCESS = prop.get("nomar_delete_success");
		PropConst.NOMAR_DELETE_FAIL = prop.get("nomar_delete_fail");
		
		PropConst.HTTP_STATUS_OK = prop.get("result_msg_ok");
		PropConst.HTTP_STATUS_ERR_SYSTEM = prop.get("result_msg_err_system");
	}
	
	//현재 메뉴를 사용중인 이용자들의 메뉴권한도 삭제되였습니다
	//메뉴가 삭제되였습니다 
	
	
	//로그인 처리 메세지
	public static String LOGIN_ERR_IDMSG;
	public static String LOGIN_ERR_PWMSG;
	public static String LOGIN_ACCESS_FAIL;
	//기본 처리 메세지
	public static String WRITE_SUCCESS_INSERT;
	public static String WRITE_FAIL_INSERT;
	public static String WRITE_SUCCESS_UPDATE;
	public static String WRITE_FAIL_UPDATE;
	public static String WRITE_ERROR_OCCURED;
	public static String NOMAR_DELETE_SUCCESS;
	public static String NOMAR_DELETE_FAIL;
	//Provider 메세지
	public static String PROVIDER_SUCCESS_MSG;
	public static String PROVIDER_FAIL_MSG;	
	public static String PROVIDER_DELETE_SUCCESS;
	public static String PROVIDER_DELETE_FAILED;
	public static String PROVIDER_ALREADY_EXSIT;
	public static String PROVIDER_KAKAO_ERROR;
	public static String PROVIDER_NOT_FOUND_LOCATION;
	public static String PROVIDER_DATE_FAIL_MSG;
	public static String PROVIDER_ALREADY_DOMAIN_EXIST;
	//렌트카 메세지
	public static String RENTCAR_FAIL_MSG;
	// 관리자 메세지
	public static String AUTHORITYDELETE_SUCCESS;
	public static String AUTHORITYDELETE_FAIL;
	public static String IDAUTHORITYCHECK_SUCCESS;
	public static String IDAUTHORITYCHECK_FAIL;
	
	public static String HTTP_STATUS_OK;
	public static String HTTP_STATUS_ERR_SYSTEM;
}

