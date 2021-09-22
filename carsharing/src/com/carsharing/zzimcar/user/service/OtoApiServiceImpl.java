package com.carsharing.zzimcar.user.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carsharing.zzimcar.base.CarsharingZzimcarService;
import com.carsharing.zzimcar.config.OtoApiConstants;
import com.carsharing.zzimcar.user.dao.CarsharingMemberDao;

/**
 * @FileName : CommonApiService.java
 * @Project : nm.carsharing
 * @Date : 2018. 10. 15
 * @작성자 : dev2227
 * @변경이력 :
 * @프로그램 설명 : 디즈파츠 API 호출 공통
 */
@Service(value = "OtoApiService")
public class OtoApiServiceImpl implements OtoApiService {
	
	@Autowired
	private CarsharingMemberDao carSharingMemberDao; 

	private static final String defaultUrl= "https://api.otoplug.com/ccgf/v1/";

	protected final Logger logger = LoggerFactory.getLogger(OtoApiServiceImpl.class);
	
	// GET 방식 통신
	private Object getHttps(String getUrl, Map<String, Object> params, String returnType) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		
		try {
			
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
			} };
			
			getUrl = defaultUrl+getUrl+"?";

			for( String key : params.keySet() ){
	            getUrl +=  (key+"="+params.get(key)+"&");
	        }
			
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			URL url = new URL(getUrl);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader( (InputStream)
			conn.getContent()); 
			BufferedReader br = new BufferedReader(in); 
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) { sb.append(line).append("\n"); }
			br.close(); 
			in.close(); 
			conn.disconnect();

			if(returnType.toLowerCase().equals("map")) {
				return jsonMap(sb.toString());
			}else if(returnType.toLowerCase().equals("list")) {
				return jsonList(sb.toString());
			}else {
				return null;
			}
		}

		catch (Exception e) {
			logger.error(e.toString());
		}

		return null;
	}
	
	// POST 방식 통신
	private Object postHttps(String postUrl, JSONObject params, String returnType) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		
		try {
			
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
			} };
			
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			
			URL url = new URL(defaultUrl+postUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			conn.setRequestMethod("POST");

			OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
			 
			wr.write(params.toString());
			wr.flush();

			InputStreamReader in = new InputStreamReader( (InputStream) conn.getContent()); 
			BufferedReader br = new BufferedReader(in); 
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) { sb.append(line).append("\n"); }
			br.close(); 
			in.close(); 
			conn.disconnect();

			if(returnType.toLowerCase().equals("map")) {
				return jsonMap(sb.toString());
			}else if(returnType.toLowerCase().equals("list")) {
				return jsonList(sb.toString());
			}else {
				return null;
			}	
		}
		catch (Exception e) {
			logger.error(e.toString());
		}

		return null;
	}
	
	// Json --> Map
	private Map<String, Object> jsonMap(String json){
		
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			map = mapper.readValue(json, new TypeReference<Map<String, String>>() {});
		} catch (Exception e) {
			logger.error(e.toString());
		} 
		
		return map;
	}
		
	// Json --> List
	private List<Map<String, Object>> jsonList(String json){
		
		ObjectMapper mapper = new ObjectMapper();
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		try {
			list = mapper.readValue(json, new TypeReference<List<Map<String, Object>>>(){});
		} catch (Exception e) {
			logger.error(e.toString());
		} 
		return list;
	}
	
	/**
	 * 사용자 토큰 체크
	 * @param     clientID, securedCode, sessionID
	 * @author    Dev2227
	 * @version   1.0 / Date : 2018/10/18
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String userTokenChk(JSONObject params) throws Exception {

		Map<String, Object> userParams = new HashMap<String, Object>();
		userParams.put("clientID", OtoApiConstants.CLIENT_ID);
		userParams.put("securedCode", OtoApiConstants.SECURED_CODE);
		userParams.put("sessionID", 1);
		
		JSONObject reqParams = new JSONObject();
		reqParams.put("clientID", userParams.get("clientID"));
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("memPid", 1);
		
		List<Map<String, Object>> userToken = carSharingMemberDao.getAllMemCheck(queryParams);
		
		String chk ="";
		
		if(userToken.size()!=0) {
			
			if(userToken.get(0).get("memToken").toString().equals("") || userToken.get(0).get("memToken").toString().equals(null) ) {
				
				Map<String, Object> resultAuth = (Map<String, Object>) auth(userParams, "map");				// 1차인증
				// API History 저장 추가
				reqParams.put("authorizeCode", resultAuth.get("authorizeCode"));
				
				Map<String, Object> resultToken = (Map<String, Object>) authToken(reqParams, "map");	// 2차 인증 (토큰발행)
				// API History 저장 추가
				if(resultToken.get("result").toString().equals("0")) {
					resultToken.put("memPid", 1);
					carSharingMemberDao.updateByPk("updateToken", resultToken);	// DB 저장
				}else {
					chk =  resultToken.get("result").toString();
				}
				
				chk =  resultToken.get("result").toString();
				
			}else {
				
				long time = System.currentTimeMillis(); 
				SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddHHmmss");
				String now = dayTime.format(new Date(time));

				String  day = userToken.get(0).get("memExpireIn").toString();
				Date d = new SimpleDateFormat("yyyyMMddHHmmss").parse(day);
				String expireln = dayTime.format(d);
				
				// 토큰발행 시간 체크 후 리턴
				
				long localTime = Long.valueOf(now);
				long expln = Long.valueOf(expireln);
				
				// 토크 재발행
				if(expln < localTime) {
					
					reqParams.put("refreshToken", userToken.get(0).get("memRefreshToken"));
					Map<String, Object> resultToken = (Map<String, Object>) authTokenRefresh(reqParams, "map");
					
					// API History 저장 추가
					if(resultToken.get("result").toString().equals("0")) {
						resultToken.put("memPid", 1);
						carSharingMemberDao.updateByPk("updateToken", resultToken);	// DB 저장
						chk =  resultToken.get("result").toString();
					}else {
						chk =  resultToken.get("result").toString();
					}
					
				}
				
			}
			
		}else {
			return chk;
		}
		
		return chk;
	}

	/**
	 * 인증절차를 시작한다. 
	 * @param  clientID , securedCode ,sessionID, redirectURI
	 * @url          GET -  https://{url}/ccgf/{version}/common.auth/
	 * @author  Dev2227
	 * @version 1.0 / Date : 2018/10/18
	 */
	@Override
	public Object auth(Map<String, Object> params, String returnType) throws Exception {
		
		String url = OtoApiConstants.AUTH_URL;
		return getHttps(url, params, returnType);
	}

	/**
	 * API 사용을 위한 Token을 얻는다.
	 * @param     clientID, authorizeCode, redirectURI
	 * @url 			   POST -  https://{url}/ccgf/{version}/common.auth.token 
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18
	 */
	@Override
	public Object authToken(JSONObject params, String returnType) throws Exception {
		String url = OtoApiConstants.AUTH_TOKEN_URL;
		return postHttps(url, params, returnType);
	}

	/**
	 * API 사용을 위한 Token을 새로 얻는다. 
	 * @param     clientID, refreshToken, redirectURI
	 * @url 			   POST -  https://{url}/ccgf/{version}/common.auth.token.refresh 
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18
	 */
	@Override
	public Object authTokenRefresh(JSONObject params, String returnType) throws Exception {
		String url = OtoApiConstants.AUTH_TOKEN_REFRESH_URL;
		return postHttps(url, params, returnType);
	}

	/**
	 * 예약추가 
	 * @param     terminalID, callbackURI, reservationIndex, rfid, startTime, endTime, sessionID
	 * @url 			   POST -  https://{url}/ccgf/{version}/csi.terminal.control.reservation.add/{client-id} 
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18
	 */
	@Override
	public Object reservationAdd(Map<String, Object> params, String returnType) throws Exception {
		
		String chk = userTokenChk(new JSONObject(params));	// 유효토큰확인여부
		
		if(chk.equals("0")) {
			
			JSONObject reqParams = new JSONObject();
			
			reqParams.put("terminalID", params.get("terminalID"));
			reqParams.put("callbackURI", params.get("callbackURI"));
			reqParams.put("reservationIndex", params.get("reservationIndex"));
			reqParams.put("rfid", params.get("rfid"));
			reqParams.put("startTime", params.get("startTime"));
			reqParams.put("endTime", params.get("endTime"));
			
			String url = OtoApiConstants.RESERVATION_ADD_URL + OtoApiConstants.CLIENT_ID;
			return postHttps(url, reqParams, returnType);
		}
		
		return chk;
	}

	/**
	 * 예약추가 
	 * @param     terminalID, callbackURI, reservationIndex, rfid, startTime, endTime
	 * @url 			   POST -  https://{url}/ccgf/{version}/csi.terminal.control.reservation.edit/{client-id}  
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18
	 */
	@Override
	public Object reservationEdit(Map<String, Object> params, String returnType) throws Exception {
		
		String chk = userTokenChk(new JSONObject(params));	// 유효토큰확인여부 	
		
		if(chk.equals("0")) {
			
			JSONObject reqParams = new JSONObject();
			
			reqParams.put("terminalID", params.get("terminalID"));
			reqParams.put("callbackURI", params.get("callbackURI"));
			reqParams.put("reservationIndex", params.get("reservationIndex"));
			reqParams.put("rfid", params.get("rfid"));
			reqParams.put("startTime", params.get("startTime"));
			reqParams.put("endTime", params.get("endTime"));
			
			String url = OtoApiConstants.RESERVATION_EDIT_URL + OtoApiConstants.CLIENT_ID;;
			return  postHttps(url, reqParams, returnType);
		}
		
		return chk;
	}

	/**
	 * 예약 완료 (대여완료) 
	 * @param     terminalID, callbackURI, reservationIndex, rfid, startTime, endTime
	 * @url 			   POST -  https://{url}/ccgf/{version}/csi.terminal.control.reservation.complete/{client-id}   
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18
	 */
	@Override
	public Object reservationComplete(Map<String, Object> params, String returnType) throws Exception {
		
		String chk = userTokenChk(new JSONObject(params));	// 유효토큰확인여부
		
		if(chk.equals("0")) {
			
			JSONObject reqParams = new JSONObject();
			
			reqParams.put("terminalID", params.get("terminalID"));
			reqParams.put("callbackURI", params.get("callbackURI"));
			reqParams.put("reservationIndex", params.get("reservationIndex"));
			reqParams.put("rfid", params.get("rfid"));
			reqParams.put("startTime", params.get("startTime"));
			reqParams.put("endTime", params.get("endTime"));
			
			String url = OtoApiConstants.RESERVATION_COMPLETE_URL + OtoApiConstants.CLIENT_ID;;
			return postHttps(url, reqParams, returnType);
		}
		
		return chk;
	}

	/**
	 * 예약 삭제 
	 * @param     terminalID, callbackURI
	 * @url 			   POST -  https://{url}/ccgf/{version}/csi.terminal.control.reservation.delete/{client-id}   
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18
	 */
	@Override
	public Object reservationDelete(Map<String, Object> params, String returnType) throws Exception {
		
		String chk = userTokenChk(new JSONObject(params));	// 유효토큰확인여부 
		
		if(chk.equals("0")) {
			
			JSONObject reqParams = new JSONObject();
			
			reqParams.put("terminalID", params.get("terminalID"));
			reqParams.put("callbackURI", params.get("callbackURI"));
			
			String url = OtoApiConstants.RESERVATION_DELETE_URL + OtoApiConstants.CLIENT_ID;;
			return postHttps(url, reqParams, returnType);
		}
		
		return chk;
	}
	
	/**
	 * 예약 취소 
	 * @param     terminalID, callbackURI, reservationIndex, rfid, startTime, endTime, sessionID
	 * @url 			   POST -  https://{url}/ccgf/{version}/csi.terminal.control.reservation.cancel/{client-id}   
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18
	 */
	@Override
	public Object reservationCancel(Map<String, Object> params, String returnType) throws Exception {
		
		String chk = userTokenChk(new JSONObject(params));	// 유효토큰확인여부 
		
		if(chk.equals("0")) {
			
			JSONObject reqParams = new JSONObject();
			
			reqParams.put("terminalID", params.get("terminalID"));
			reqParams.put("callbackURI", params.get("callbackURI"));
			reqParams.put("reservationIndex", params.get("reservationIndex"));
			reqParams.put("rfid", params.get("rfid"));
			reqParams.put("startTime", params.get("startTime"));
			reqParams.put("endTime", params.get("endTime"));
			
			String url = OtoApiConstants.RESERVATION_CANCEL_URL + OtoApiConstants.CLIENT_ID;
			return postHttps(url, reqParams, returnType);
		}
		
		return chk;
	}

	/**
	 * 비상등을 켠다. 
	 * @param     terminalID, callbackURI, reservationIndex, commandFinishTime,  forceCommand
	 * @url 			   POST -  https://{url}/ccgf/{version}/csi.terminal.control.vehicle.hazard.on/{client-id}    
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18
	 */
	@Override
	public Object vehicleHazardOn(Map<String, Object> params, String returnType) throws Exception {
		
		String chk = userTokenChk(new JSONObject(params));	// 유효토큰확인여부
		
		if(chk.equals("0")) {
			
			JSONObject reqParams = new JSONObject();
			
			reqParams.put("terminalID", params.get("terminalID"));
			reqParams.put("callbackURI", params.get("callbackURI"));
			reqParams.put("reservationIndex", params.get("reservationIndex"));
			reqParams.put("commandFinishTime", params.get("commandFinishTime"));
			reqParams.put("forceCommand", params.get("forceCommand"));
			
			String url = OtoApiConstants.VEHICLE_HAZARD_ON_URL + OtoApiConstants.CLIENT_ID;
			return postHttps(url, reqParams, returnType);
		}
		
		return chk;
		
	}

	/**
	 * 비상등을 끈다. 
	 * @param     terminalID, callbackURI, reservationIndex, commandFinishTime,  forceCommand
	 * @url 			   POST -  https://{url}/ccgf/{version}/csi.terminal.control.vehicle.hazard.off/{client-id}     
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18
	 */
	@Override
	public Object vehicleHazardOff(Map<String, Object> params, String returnType) throws Exception {
		
		String chk = userTokenChk(new JSONObject(params));	// 유효토큰확인여부 
		
		if(chk.equals("0")) {
			
			JSONObject reqParams = new JSONObject();
			
			reqParams.put("terminalID", params.get("terminalID"));
			reqParams.put("callbackURI", params.get("callbackURI"));
			reqParams.put("reservationIndex", params.get("reservationIndex"));
			reqParams.put("commandFinishTime", params.get("commandFinishTime"));
			reqParams.put("forceCommand", params.get("forceCommand"));
			
			String url = OtoApiConstants.VEHICLE_HAZARD_OFF_URL;
			return postHttps(url, reqParams, returnType);
		}
		
		return chk;
	}

	/**
	 * 문잠금 
	 * @param     terminalID, callbackURI, reservationIndex, commandFinishTime,  forceCommand
	 * @url 			   POST -  https://{url}/ccgf/{version}/csi.terminal.control.vehicle.door.lock/{client-id}     
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18
	 */
	@Override
	public Object vehiceDoorLock(Map<String, Object> params, String returnType) throws Exception {
		
		String chk = userTokenChk(new JSONObject(params));	// 유효토큰확인여부
		
		if(chk.equals("0")) {
			
			JSONObject reqParams = new JSONObject();
			
			reqParams.put("terminalID", params.get("terminalID"));
			reqParams.put("callbackURI", params.get("callbackURI"));
			reqParams.put("reservationIndex", params.get("reservationIndex"));
			reqParams.put("commandFinishTime", params.get("commandFinishTime"));
			reqParams.put("forceCommand", params.get("forceCommand"));
			
			String url = OtoApiConstants.VEHICLE_DOOR_LOCK_URL+ OtoApiConstants.CLIENT_ID;
			return postHttps(url, reqParams, returnType);
		}
		
		return chk;
		
	}

	/**
	 * 문잠금 해제
	 * @param     terminalID, callbackURI, reservationIndex, commandFinishTime,  forceCommand
	 * @url 			   POST -  https://{url}/ccgf/{version}/csi.terminal.control.vehicle.door.unlock/{client-id}     
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18
	 */
	@Override
	public Object vehiceDoorUnlock(Map<String, Object> params, String returnType) throws Exception {
		
		String chk = userTokenChk(new JSONObject(params));	// 유효토큰확인여부
		
		if(chk.equals("0")) {
			
			JSONObject reqParams = new JSONObject();
			
			reqParams.put("terminalID", params.get("terminalID"));
			reqParams.put("callbackURI", params.get("callbackURI"));
			reqParams.put("reservationIndex", params.get("reservationIndex"));
			reqParams.put("commandFinishTime", params.get("commandFinishTime"));
			reqParams.put("forceCommand", params.get("forceCommand"));
			
			String url = OtoApiConstants.VEHICLE_DOOR_UNLOCK_URL+ OtoApiConstants.CLIENT_ID;
			return postHttps(url, reqParams, returnType);
		}
		
		return chk;
		
	}

	/**
	 * 경적을 킨다
	 * @param     terminalID, callbackURI, reservationIndex, commandFinishTime,  forceCommand, duration, dutyOn, dutyOff
	 * @url 			   POST -  https://{url}/ccgf/{version}/csi.terminal.control.vehicle.horn.on/{client-id}      
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18
	 */
	@Override
	public Object vehiceHornOn(Map<String, Object> params, String returnType) throws Exception {
		
		String chk = userTokenChk(new JSONObject(params));	// 유효토큰확인여부
		
		if(chk.equals("0")) {
			
			JSONObject reqParams = new JSONObject();
			
			reqParams.put("terminalID", params.get("terminalID"));
			reqParams.put("callbackURI", params.get("callbackURI"));
			reqParams.put("reservationIndex", params.get("reservationIndex"));
			reqParams.put("commandFinishTime", params.get("commandFinishTime"));
			reqParams.put("forceCommand", params.get("forceCommand"));
			reqParams.put("duration", params.get("duration"));
			reqParams.put("dutyOn", params.get("dutyOn"));
			reqParams.put("dutyOff", params.get("dutyOff"));
			
			String url = OtoApiConstants.VEHICLE_HORN_ON_URL+ OtoApiConstants.CLIENT_ID;
			return postHttps(url, reqParams, returnType);
		}
		
		return chk;
		
	}

	/**
	 * 경적을 끈다
	 * @param     terminalID, callbackURI, reservationIndex, commandFinishTime,  forceCommand, duration, dutyOn, dutyOff
	 * @url 			   POST -  https://{url}/ccgf/{version}/csi.terminal.control.vehicle.horn.off/{client-id}      
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18
	 */
	@Override
	public Object vehiceHornOff(Map<String, Object> params, String returnType) throws Exception {
		
		String chk = userTokenChk(new JSONObject(params));	// 유효토큰확인여부
		
		if(chk.equals("0")) {
			
			JSONObject reqParams = new JSONObject();
			
			reqParams.put("terminalID", params.get("terminalID"));
			reqParams.put("callbackURI", params.get("callbackURI"));
			reqParams.put("reservationIndex", params.get("reservationIndex"));
			reqParams.put("commandFinishTime", params.get("commandFinishTime"));
			reqParams.put("forceCommand", params.get("forceCommand"));
			reqParams.put("duration", params.get("duration"));
			reqParams.put("dutyOn", params.get("dutyOn"));
			reqParams.put("dutyOff", params.get("dutyOff"));
			
			String url = OtoApiConstants.VEHICLE_HORN_OFF_URL+ OtoApiConstants.CLIENT_ID;
			return postHttps(url, reqParams, returnType);
		}
		
		return chk;
		
	}

	/**
	 * 현재 운행 종합 상태 데이터 얻기 (위치포함) 
	 * @param     id, type, address, token, expiration
	 * @url 			   POST -  https://{url}/ccgf/{version}/​csi.terminal.result.data.driving​/{client-id}/observer      
	 * @author     Dev2227
	 * @version    1.0 / Date : 2018/10/18 
	 */
	@Override
	public Object drivingCurrentStatus(Map<String, Object> params, String returnType) throws Exception {
		
		String chk = userTokenChk(new JSONObject(params));	// 유효토큰확인여부
		
		if(chk.equals("0")) {
			
			JSONObject reqParams = new JSONObject();
			
			reqParams.put("id", params.get("id"));
			reqParams.put("type", params.get("type"));
			reqParams.put("address", params.get("address"));
			reqParams.put("token", params.get("token"));
			reqParams.put("expiration", params.get("expiration"));

			String url = OtoApiConstants.DRIVING_CURRENT_STATUS_URL+ OtoApiConstants.CLIENT_ID+"/observer";
			return postHttps(url, reqParams, returnType);
		}
		
		return chk;
		
	}
	
}

