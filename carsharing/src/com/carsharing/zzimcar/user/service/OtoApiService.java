package com.carsharing.zzimcar.user.service;

import java.util.Map;

import org.json.JSONObject;

/**
 * @FileName : CommonApiService.java
 * @Project : nm.carsharing
 * @Date : 2018. 10. 15
 * @작성자 : dev2227
 * @변경이력 :
 * @프로그램 설명 : 디즈파츠 API 호출 인터페이스
 */
public interface OtoApiService {

	// 사용자인증 1단계
	public Object auth(Map<String, Object> params, String returnType) throws Exception;
	
	// 사용자인증 2단계(Token)
	public Object authToken(JSONObject params, String returnType) throws Exception;
	
	// 사용자인증 3단계(Token Refresh)
	public Object authTokenRefresh(JSONObject params, String returnType) throws Exception;
	
	// 예약추가
	public Object reservationAdd(Map<String, Object> params, String returnType) throws Exception;
	
	// 예약변경
	public Object reservationEdit(Map<String, Object> params, String returnType) throws Exception;
	
	// 예약완료(대여완료)
	public Object reservationComplete(Map<String, Object> params, String returnType) throws Exception;
	
	// 예약삭제
	public Object reservationDelete(Map<String, Object> params, String returnType) throws Exception;
	
	// 예약취소
	public Object reservationCancel(Map<String, Object> params, String returnType) throws Exception;
	
	// 비상등 켬
	public Object vehicleHazardOn(Map<String, Object> params, String returnType) throws Exception;
	
	// 비상등 끔
	public Object vehicleHazardOff(Map<String, Object> params, String returnType) throws Exception;
	
	// 문잠금
	public Object vehiceDoorLock(Map<String, Object> params, String returnType) throws Exception;
	
	// 문잠금 해제
	public Object vehiceDoorUnlock(Map<String, Object> params, String returnType) throws Exception;
	
	// 경적 ON
	public Object vehiceHornOn(Map<String, Object> params, String returnType) throws Exception;
	
	// 경적 OFF
	public Object vehiceHornOff(Map<String, Object> params, String returnType) throws Exception;
	
	// 현재 운행 종합상태얻기 (위치정보)
	public Object drivingCurrentStatus(Map<String, Object> params, String returnType) throws Exception;
	
	// 사용자 토큰
	public String userTokenChk(JSONObject params) throws Exception;
}
