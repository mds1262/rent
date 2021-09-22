package com.carsharing.zzimcar.config;

public class OtoApiConstants {

	public static final String CLIENT_ID = "5d56018c7e8442abbacacd3b202a83e5";
	public static final String SECURED_CODE = "RJKKhwiwGy1jxXnHeTxOiMuL5tpShNh3sAs6dxgc59n9li54P70DGdYKcYqry7fNor6tCrrh8";
	
	// 사용자인증 1단계
	public static final String AUTH_URL = "common.auth";
	
	// 사용자인증 2단계(Token)
	public static final String AUTH_TOKEN_URL = "common.auth.token";
	
	// 사용자인증 3단계(Token Refresh)
	public static final String AUTH_TOKEN_REFRESH_URL = "common.auth.token.refresh";
	
	// 예약추가
	public static final String RESERVATION_ADD_URL = "csi.terminal.control.reservation.add/";
	
	// 예약변경
	public static final String RESERVATION_EDIT_URL = "csi.terminal.control.reservation.edit/";
	
	// 예약완료(대여완료)
	public static final String RESERVATION_COMPLETE_URL = "csi.terminal.control.reservation.complete/";
	
	// 예약삭제
	public static final String RESERVATION_DELETE_URL = "csi.terminal.control.reservation.delete/";
	
	// 예약취소
	public static final String RESERVATION_CANCEL_URL = "csi.terminal.control.reservation.cancel/";	
	
	// 비상등 켬
	public static final String VEHICLE_HAZARD_ON_URL = "csi.terminal.control.vehicle.hazard.on/";
	
	// 비상등 끔
	public static final String VEHICLE_HAZARD_OFF_URL = "csi.terminal.control.vehicle.hazard.off/";
	
	// 문잠금
	public static final String VEHICLE_DOOR_LOCK_URL = "csi.terminal.control.vehicle.door.lock/";
	
	// 문잠금 해제
	public static final String VEHICLE_DOOR_UNLOCK_URL = "csi.terminal.control.vehicle.door.unlock/";
	
	// 경적 ON
	public static final String VEHICLE_HORN_ON_URL = "csi.terminal.control.vehicle.horn.on/";
	
	// 경적 OFF
	public static final String VEHICLE_HORN_OFF_URL = "csi.terminal.control.vehicle.horn.off/";
	
	// 현재 운행 종합상태얻기 (위치정보)
	public static final String DRIVING_CURRENT_STATUS_URL = "csi.terminal.query.driving.currentStatus/";
	
}
