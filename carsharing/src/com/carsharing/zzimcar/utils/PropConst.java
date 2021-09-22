package com.carsharing.zzimcar.utils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.carsharing.zzimcar.utils.PropConst;

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
		
		PropConst.SIGN_UP_ID_ERR = prop.get("sign_up_id_err");
		PropConst.SIGN_UP_EMAIL_ERR = prop.get("sign_up_email_err");
		PropConst.SIGN_UP_PHONE_ERR = prop.get("sign_up_phone_err");
		PropConst.SIGN_UP_BIRTHDAY_ERR = prop.get("sign_up_birthday_err");
		PropConst.SIGN_UP_PASSWORD_ERR = prop.get("sign_up_password_err");
		PropConst.SIGN_UP_OK = prop.get("sign_up_ok");
		
		PropConst.MEMBER_MODIFY_OK = prop.get("member_modify_ok");
		
		PropConst.LOGIN_ERR_MSG = prop.get("login_err_msg");
		PropConst.LOGOUT_OK_MSG = prop.get("logout_ok_msg");
		PropConst.USER_STOP_MSG = prop.get("user_stop_msg");
		
		PropConst.PW_CHANGE_SUCCESS = prop.get("pw_change_success");
		PropConst.PW_CHANGE_FAIL = prop.get("pw_change_fail");
		
		PropConst.HTTP_STATUS_OK = prop.get("result_msg_ok");
		PropConst.HTTP_STATUS_ERR_SYSTEM = prop.get("result_msg_err_system");
		
		PropConst.AUTH_FAIL_MSG = prop.get("auth_fail_msg");
	}
	
	
	
	public PropConst() {}

	public String get( String msgId ) {
		return this.prop.get(msgId);
	}
	
	//로그인 처리 메세지
	public static String LOGIN_ERR_IDMSG;
	public static String LOGIN_ERR_PWMSG;
	public static String LOGIN_ACCESS_FAIL;
	
	//어드민 처리 메세지
	public static String WRITE_SUCCESS_INSERT;
	public static String WRITE_FAIL_INSERT;
	public static String WRITE_SUCCESS_UPDATE;
	public static String WRITE_FAIL_UPDATE;
	public static String WRITE_ERROR_OCCURED;
	
	public static String SIGN_UP_ID_ERR;
	public static String SIGN_UP_EMAIL_ERR;
	public static String SIGN_UP_PHONE_ERR;
	public static String SIGN_UP_BIRTHDAY_ERR;
	public static String SIGN_UP_PASSWORD_ERR;
	public static String SIGN_UP_OK;
	
	public static String MEMBER_MODIFY_OK;
	
	public static String LOGIN_ERR_MSG;
	public static String LOGOUT_OK_MSG;
	public static String USER_STOP_MSG;

	public static String HTTP_STATUS_OK;
	public static String HTTP_STATUS_ERR_SYSTEM;
	
	public static String PW_CHANGE_SUCCESS;
	public static String PW_CHANGE_FAIL;  
	
	public static String AUTH_FAIL_MSG;
}
