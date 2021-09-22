package com.zzimcar.admin.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.config.ZzimcarConstants;
import com.zzimcar.admin.config.ZzimcarConstants.EXTRACHARGE_COLUMN_CODE;
import com.zzimcar.admin.dao.ErpPeristalsisDao;
import com.zzimcar.admin.dao.ProviderExtrachargeDao;
import com.zzimcar.admin.erp.vo.DataJson;

@SuppressWarnings({ "resource", "deprecation" })
@Service(value="ErpPeristalsisService")
public class ErpPeristalsisService extends ZzimcarService {
	
	@Resource(name = "ErpPeristalsisDao")
	ErpPeristalsisDao erpPeristalsisDao;
	
	@Resource(name = "ProviderExtrachargeDao")
	ProviderExtrachargeDao providerExtrachargeDao;
	
	private DataJson dataJson;
	
	public Map<String, Object> erpMasterList(Map<String, Object> mp){
		return erpPeristalsisDao.erpMasterList(mp);
	}
	
	public List<Map<String, Object>> rentCarCorpList(Map<String, Object> mp){
		return erpPeristalsisDao.rentCarCorpList(mp);
	}
	
	public int insertCorpTx(Map<String, Object> mp) throws Exception {
		int result = erpPeristalsisDao.insertCorp(mp);
		
		Map<String, Object> ExtraMap = new HashMap<String, Object>();

		// 날짜 지정
		Calendar calendar = Calendar.getInstance();
		String year = String.format("%d", calendar.get(Calendar.YEAR));
		String month = String.format("%02d", (calendar.get(Calendar.MONTH) + 1));
		String day = String.format("%02d", calendar.get(Calendar.DATE));

		ExtraMap.put("charge_sdate", year + month + day);
		ExtraMap.put("charge_edate", year + "12" + "31");

		// 할증율
		for (EXTRACHARGE_COLUMN_CODE code : EXTRACHARGE_COLUMN_CODE.values()) {
			ExtraMap.put(code.getCode(), 0);
		}

		// 등록자
		ExtraMap.put("reg_mem_pid", mp.get("reg_mem_pid"));
		ExtraMap.put("mod_mem_pid", mp.get("reg_mem_pid"));
		// 업체 고유 PID
		ExtraMap.put("rccorp_pid", mp.get("rentcar_corp_pid"));

		providerExtrachargeDao.insert(ExtraMap);
		
		return result;
	}
	
	public List<Map<String, Object>> selectNmCarModel(Map<String, Object> mp){
		return erpPeristalsisDao.selectNmCarModel(mp);
	}
	
	public List<Map<String, Object>> selectRentCarModel(Map<String, Object> mp){
		return erpPeristalsisDao.selectRentCarModel(mp);
	}
	
	public List<Map<String, Object>> selectRentCarModelJeju(Map<String, Object> mp){
		return erpPeristalsisDao.selectRentCarModelJeju(mp);
	}
	
	public Map<String, Object> selectRcCorpList(Map<String, Object> mp){
		return erpPeristalsisDao.selectRcCorpList(mp);
	}
	
	public List<Map<String, Object>> selecterpSyncList(Map<String, Object> mp){
		return erpPeristalsisDao.selecterpSyncList(mp);
	}
	
	//public void insRentCarTx(DataJson carJsonList,DataJson pubJsonList, DataJson insuJsonList, Map<String, Object> mp) throws Exception{
	public void insRentCarTx(DataJson carJsonList, Map<String, Object> mp) throws Exception{
		
		//List<String> result = new ArrayList<String>();
		//Map<String, String> pubList = new HashMap<String, String>();
		
		for(DataJson.InsCarList carList : carJsonList.insCarList){
			
			// nmcar_model 차량 등록확인
			mp.put("model_code", carList.modelCode);						// 모델코드
			mp.put("model_name", carList.rentCarModelName);					// 모델명
			
			List<Map<String,Object>> nmCarList = this.selectNmCarModel(mp);
			List<Map<String,Object>> rcCarList = this.selectRentCarModel(mp);
			
			Map<String, Object> rcCorpList = this.selectRcCorpList(mp);
			mp.put("rccorp_pid", rcCorpList.get("rccorp_pid"));				// rccorp_pid
			
			// nmcar_model 차량 있으면
			if(nmCarList.size()>=1 && rcCarList.size()==0){
				
				// price_standard,price_dc_rate, price_sale, price_insu_pub, price_insu_full = 0
				
				// 연료 1개는 master, 2개는 option
				String[] rentCarFuelArr = carList.rentCarFuel.toString().split(",");
				ArrayList<String> rentCarFuelList = new ArrayList<String>(Arrays.asList(rentCarFuelArr));
				
				// 사용연료
				if(rentCarFuelList.size()!=0) {

					for(int i=0; i<rentCarFuelList.size(); i++) {
						
						for(ZzimcarConstants.RENTCAR_FUEL_CODE code : ZzimcarConstants.RENTCAR_FUEL_CODE.values()) {	
							
							if(code.getKey().equals("전기")) {
								mp.put("iselectric_car", "Y");
							}else {
								mp.put("iselectric_car", "N");
							}
						}
					}
				}
				
				// 승합/승용 구분(차량분류 동일) (1개이상 절대 금지 - 관리자 숙지)
				/*				
 				String carSize = carList.carSize.replaceAll(",", "");

				if(carSize.trim().equals("승합")) {
					
					mp.put("carrier_code", "CARRI_M");
					mp.put("class_code", "CLS_07");
					
				}else if(carSize.trim().equals("대형")) {
					
					mp.put("carrier_code", "CARRI_P");
					mp.put("class_code", "CLS_05");
					
				}else if(carSize.trim().equals("중형")) {
					
					mp.put("carrier_code", "CARRI_P");
					mp.put("class_code", "CLS_04");
					
				}else if(carSize.trim().equals("준중형")) {
					
					mp.put("carrier_code", "CARRI_P");
					mp.put("class_code", "CLS_03");
					
				}else if(carSize.trim().equals("소형")) {
					
					mp.put("carrier_code", "CARRI_P");
					mp.put("class_code", "CLS_02");
					
				}else if(carSize.trim().equals("경형")) {
					
					mp.put("carrier_code", "CARRI_P");
					mp.put("class_code", "CLS_01");
					
				}else {
					
					mp.put("carrier_code", "CARRI_P");
					mp.put("class_code", "CLS_06");
				}
				*/
				mp.put("nmmodel_pid", nmCarList.get(0).get("nmmodelPid")); 	// nmmodel_pid	
				mp.put("erp_model_code", carList.rentCarModelNo); 			// ERP 모델명
				mp.put("car_count", carList.rentCarCnt); 					// 보유수량
				mp.put("seater_count", carList.rentCarPersonnel); 			// 승차인원
				mp.put("engine_cc", carList.rentCarCc); 					// 배기량
				mp.put("license_age", carList.rentCarAvailableAge);			// 가능연령
				mp.put("model_status", "1");

				// rentcar_model_master INSERT
				erpPeristalsisDao.insertRentcarModel(mp);

				// 보험등록을 위한 차량 리스트저장
				//result.add(mp.get("rcmodel_pid").toString());
				//pubList.put(carList.rentCarModelNo, mp.get("rcmodel_pid").toString());
				
				// rentcar_model_option INSERT
				Map<String, Object> optionList = new HashMap<String, Object>();
				optionList.put("rcmodel_pid", mp.get("rcmodel_pid"));
				
				// 모델옵션 Fule
				if(rentCarFuelList.size()!=0) {

					for(int i=0; i<rentCarFuelList.size(); i++) {
						
						for(ZzimcarConstants.RENTCAR_FUEL_CODE code : ZzimcarConstants.RENTCAR_FUEL_CODE.values()) {	
							
							if(rentCarFuelList.get(i).trim().equals(code.getKey())) {
								mp.put("code_div", code.name().substring(0, code.name().length() - 1));
								mp.put("code_key", code.getValue());
								
								erpPeristalsisDao.insertRcModelOption(mp);
							}
						}
					}
				}

				// 모델옵션 -List (ex: 가죽시트,운전석에어백,조수석에어백,후방센서...)
				String[] carModelOptionArr = carList.rentCarModelOption.toString().split(",");
				 
				List<String> carModelOptionList = new ArrayList<String>(Arrays.asList(carModelOptionArr));
				
				for (int i = 0; i < carModelOptionList.size(); i++) {
					
					for(ZzimcarConstants.RENTCAR_OPTION_CODE code : ZzimcarConstants.RENTCAR_OPTION_CODE.values()) {

						if(carModelOptionList.get(i).trim().equals(code.getKey())) {
							
							optionList.put("code_div", code.name().substring(0, code.name().length() - 1));
							optionList.put("code_key", code.getValue());
							
							erpPeristalsisDao.insertRcModelOption(optionList);
							
						}
					}
				}
				
				optionList = new HashMap<String, Object>();
				optionList.put("rcmodel_pid", mp.get("rcmodel_pid"));

				// 연식
				String[] rentCarYearArr = carList.rentCarYear.toString().split("~");
				List<String> rentCarYearList = new ArrayList<String>(Arrays.asList(rentCarYearArr));
				
				if(rentCarYearList.size()>1) {
					
					int oldYear = Integer.parseInt(rentCarYearArr[0]);
					int newYear = Integer.parseInt(rentCarYearArr[1]);
					//int yearCnt = newYear-oldYear;
					
					if(oldYear!=0 && oldYear!=newYear) {
						// oldYear_ + yearCnt
						// String.valueOf(newYear).substring(String.valueOf(newYear).length()-2)); 

						int yearList = newYear-oldYear;
						
						for(int k=0; k<=yearList; k++) {
							
							optionList.put("code_div", "CAR_YEAR_CODE");
							optionList.put("code_key", "Y_"+(oldYear+k));
							
							erpPeristalsisDao.insertRcModelOption(optionList);
						}
						
						//optionList.put("code_key", oldYear+"_"+String.valueOf(newYear).substring(String.valueOf(newYear).length()-2));
						
					}else {
						
						optionList.put("code_div", "CAR_YEAR_CODE");
						optionList.put("code_key", "Y_"+newYear);
						
						erpPeristalsisDao.insertRcModelOption(optionList);
						
						// newYear 만 넣기
					}
					
					
					
				}else {
					
					optionList.put("code_div", "CAR_YEAR_CODE");
					optionList.put("code_key", rentCarYearList.get(0));
					
					erpPeristalsisDao.insertRcModelOption(optionList);
					
				}
				
				// 보험등록을 위한 차량 리스트저장
				//String pubcar ="";
				//String[] pubCarListArr = null;

				//Map<String, Object> carPubList = new HashMap<String, Object>();
				//carPubList.put("rcmodel_pid", mp.get("rcmodel_pid"));
				
				// 보험 차량번호 모으기
				//for(DataJson.InsPubList pubList : pubJsonList.insPubList){
					
				//	System.out.println("111 @@@@ " + pubList.applyModel);
					
				//	for(DataJson.InsPubList pubLists :  insuJsonList.insPubList) {
						//System.out.println("222 @@@@ " + pubLists.applyModel);
				//	}
				//}
				
			} // end if

		} // end for
	}
	
	public void jejuRentCarTx(Map<String, Object> mp) throws Exception{
		
		Map<String, Object> carMp = new HashMap<String, Object>();
		
		int provider_pid = Integer.parseInt(mp.get("provider_pid").toString());
		
		// xml 조회
		NodeList carList = httpClientXml(ZzimcarController.ERP_JEJU_GRIM, mp);
		Node current = null;
		
		// 정보 Insert
		for (int i = 0; i < carList.getLength(); i++) {
			
			current = carList.item(i);

			NodeList testChildNodes = current.getChildNodes(); // 노드의 자식노드 가져옴

			for (int j = 0; j<testChildNodes.getLength(); j++) {
				
				Node info = testChildNodes.item(j);

				if (info.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) info;
					carMp.put(element.getTagName(), element.getTextContent());
					//code = if (element.getTagName() == "code") {element.getTextContent());}
				}
			}
			
			// nmcar_model 차량 등록확인
			mp.put("erp_model_code", carMp.get("code"));					// 모델코드
			mp.put("model_name", carMp.get("name"));						// 모델명
			mp.put("erp_code", carMp.get("code"));
			mp.put("provider_pid", provider_pid);
			
			Map<String, Object> rcCorpList = this.selectRcCorpList(mp);
			mp.put("rccorp_pid", rcCorpList.get("rccorp_pid"));				// rccorp_pid
			
			List<Map<String,Object>> erpSyncList = this.selecterpSyncList(mp);
			
			List<Map<String,Object>> rcCarList = this.selectRentCarModelJeju(mp);
			
			// nmcar_model 차량 있으면
		
			if(erpSyncList.size()>=1 && rcCarList.size()==0){
				/*
				if(carMp.get("gubun").equals("승합")) {
					mp.put("carrier_code", "CARRI_M");
					mp.put("class_code", "CLS_07");
				}else if(carMp.get("gubun").equals("대형")) {
					mp.put("carrier_code", "CARRI_P");
					mp.put("class_code", "CLS_05");
				}else if(carMp.get("gubun").equals("중형")) {
					mp.put("carrier_code", "CARRI_P");
					mp.put("class_code", "CLS_04");
				}else if(carMp.get("gubun").equals("준중형")) {
					mp.put("carrier_code", "CARRI_P");
					mp.put("class_code", "CLS_03");
				}else if(carMp.get("gubun").equals("소형")) {
					mp.put("carrier_code", "CARRI_P");
					mp.put("class_code", "CLS_02");
				}else if(carMp.get("gubun").equals("경형")) {
					mp.put("carrier_code", "CARRI_P");
					mp.put("class_code", "CLS_01");
				}else {
					mp.put("carrier_code", "CARRI_P");
					mp.put("class_code", "CLS_06");
				}
				*/
				if(carMp.get("fuel").toString().trim().equals("전기") || carMp.get("fuel").toString().trim().equals("전기차")){
					mp.put("iselectric_car", "Y");
				}else {
					mp.put("iselectric_car", "N");
				}
				
				
				// 라이센스 나이 경력 나누기
				String[] permitArr = carMp.get("permit").toString().trim().split("/");
				
				//recorp_pid
				//erp_pid
				mp.put("model_code", erpSyncList.get(0).get("nmCode"));			// 모델코드
				mp.put("erp_model_code", carMp.get("code"));					//erp_model_pid
				mp.put("nmmodel_pid", erpSyncList.get(0).get("nmmodelPid")); 	// nmmodel_pid	
				mp.put("model_name", carMp.get("name"));						// model_name
				mp.put("engine_cc", Integer.parseInt(carMp.get("baegi").toString().replaceAll("cc", "").trim()));						// engine_cc
				mp.put("seater_count", carMp.get("jeongwon"));					// seater_count
				mp.put("license_age", permitArr[0]);							// license_age
				mp.put("license_year", permitArr[1]);							// license_year
				mp.put("model_status", "1");
				// rentcar_model_master INSERT
				erpPeristalsisDao.insertRentcarModel(mp);
				
				// 모델옵션 -List (ex: 가죽시트,운전석에어백,조수석에어백,후방센서...)
				String[] carModelOptionArr = carMp.get("option").toString().split(",");					 
				List<String> carModelOptionList = new ArrayList<String>(Arrays.asList(carModelOptionArr));

				for (int k=0; k<carModelOptionList.size(); k++) {
					
					for(ZzimcarConstants.RENTCAR_OPTION_CODE code : ZzimcarConstants.RENTCAR_OPTION_CODE.values()) {

						if(carModelOptionList.get(k).trim().equals(code.getKey())) {
							
							if(code.name().length()==23) {
								
								if(code.name().toString().indexOf("HEALTH") <= 0) {
									mp.put("code_div", code.name().substring(0, code.name().length() - 2));
								} else {
									mp.put("code_div", code.name().substring(0, code.name().length() - 1));
								}
								
							} else {
								mp.put("code_div", code.name().substring(0, code.name().length() - 1));
							}
							mp.put("code_key", code.getValue());
							
							erpPeristalsisDao.insertRcModelOption(mp);
							
						}
					}
				}
				
				// 연식
				String[] carModelYearArr = carMp.get("old").toString().split("~");					 
				List<String> carModelYearList = new ArrayList<String>(Arrays.asList(carModelYearArr));

				if(carModelYearList.size()==1) {
					
					mp.put("code_div", "CAR_YEAR_CODE");
					mp.put("code_key", "Y_"+carModelYearList.get(0).substring(0, 4));
					
					erpPeristalsisDao.insertRcModelOption(mp);
					
				}else {
					
					if (carModelYearList.get(0).trim().equals(carModelYearList.get(1).trim())) {
						
						mp.put("code_div", "CAR_YEAR_CODE");
						
						String year = carModelYearList.get(0).substring(0, 2);
						
						if(year.contains("20")) {
							mp.put("code_key", "Y_"+carModelYearList.get(0).substring(0, 4));
						}else {
							mp.put("code_key", "Y_20"+carModelYearList.get(0).substring(0, 2));
						}
						
						erpPeristalsisDao.insertRcModelOption(mp);
						
					}else {
						
						mp.put("code_div", "CAR_YEAR_CODE");
						
						String yearChk = carModelYearList.get(0).substring(0, 2);
						
						if(yearChk.contains("20")) {
							
							int year = (Integer.parseInt(carModelYearList.get(1).substring(0, 4)) - Integer.parseInt(carModelYearList.get(0).substring(0, 4)));
							
							for(int k = 0; k<=year; k++) {
								
								int yearList = Integer.parseInt(carModelYearList.get(0).substring(0, 4));
								
								mp.put("code_key", "Y_"+(yearList+k));
								
								erpPeristalsisDao.insertRcModelOption(mp);
							}	
							
						}else {
							mp.put("code_key", "Y_20"+carModelYearList.get(0).substring(0, 2));
							
							erpPeristalsisDao.insertRcModelOption(mp);
						}
						
					}
					
				}
				
				// 연료
				String[] rentCarFuelArr = carMp.get("fuel").toString().split(",");
				ArrayList<String> rentCarFuelList = new ArrayList<String>(Arrays.asList(rentCarFuelArr));
				
				if(rentCarFuelList.size() >= 1 && rentCarFuelList.size()!=0) {
					
					// 1개이상은 rentcar_model_option 저장
					for(int k=0; k<rentCarFuelList.size(); k++) {
						
						for(ZzimcarConstants.RENTCAR_FUEL_CODE code : ZzimcarConstants.RENTCAR_FUEL_CODE.values()) {	
							
							if(rentCarFuelList.get(k).trim().equals(code.getKey())) {
								if(code.name().length() > 14) {
									mp.put("code_div", code.name().substring(0, code.name().length() - 2));
								} else {
									mp.put("code_div", code.name().substring(0, code.name().length() - 1));
								}
								mp.put("code_key", code.getValue());
								erpPeristalsisDao.insertRcModelOption(mp);
							}
						}	
					}
				}
				
			} // END IF
		}
		
	}
	
	// REBORN
	public int rebornRentCarTx(Map<String, Object> mp) throws Exception{

		Map<String, Object> carMp = new HashMap<String, Object>();
		
		int provider_pid = Integer.parseInt(mp.get("provider_pid").toString());
		int rebornCnt = 0;
		
		// xml 조회
		NodeList carList = httpClientXml(ZzimcarController.ERP_REBORN, mp);
		Node current = null;
		
		// 정보 Insert
		for (int i = 0; i < carList.getLength(); i++) {
			
			current = carList.item(i);

			NodeList testChildNodes = current.getChildNodes(); // 노드의 자식노드 가져옴

			for (int j = 0; j<testChildNodes.getLength(); j++) {
				
				Node info = testChildNodes.item(j);

				if (info.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) info;
					carMp.put(element.getTagName(), element.getTextContent());
				}
			}// END FOR
			
			mp.put("model_code", carMp.get("bcncVhctyCode"));
			mp.put("erp_model_code", carMp.get("vhctyCode"));
			
			List<Map<String,Object>> nmCarList = this.selectNmCarModel(mp);
			List<Map<String,Object>> rcCarList = this.selectRentCarModelJeju(mp);
			
			
			if(nmCarList.size() ==1 && rcCarList.size()==0) {
				
				// rentcar_model_master SAVE
				mp.put("nmmodel_pid", nmCarList.get(0).get("nmmodelPid"));
				mp.put("model_code", nmCarList.get(0).get("modelCode"));
				mp.put("model_name", nmCarList.get(0).get("modelName"));
				mp.put("engine_cc", nmCarList.get(0).get("engineCc"));
				mp.put("seater_count", carMp.get("passengers"));
				//mp.put("min_price", nmCarList.get(0).get("minPrice"));
				mp.put("license_age", carMp.get("licenseLmttAge"));
				mp.put("model_status", "1");
				
				erpPeristalsisDao.insertRentcarModel(mp);
				
				// 연료 타입 코드 
				String[] fuelCodeArr = carMp.get("fuelCode").toString().split(",");					 
				List<String> fuelCodeList = new ArrayList<String>(Arrays.asList(fuelCodeArr));

				for(int k=0; k<fuelCodeList.size(); k++) {
					
					for(ZzimcarConstants.RENTCAR_FUEL_CODE code : ZzimcarConstants.RENTCAR_FUEL_CODE.values()) {

						if(fuelCodeList.get(k).trim().equals(code.getKey())) {
							
							if(code.name().length()==15) {
								mp.put("code_div", code.name().substring(0, code.name().length() - 2));
							}else {
								mp.put("code_div", code.name().substring(0, code.name().length() - 1));
							}
							mp.put("code_key", code.getValue());
							
							if(mp.get("code_key").toString().equals("FUEL_E")) {
								mp.put("iselectric_car", "Y");
							}else {
								mp.put("iselectric_car", "N");
							}
							
							erpPeristalsisDao.insertRcModelOption(mp);
						}
					}
				}
				
				
				
				// rentcar_model_option SAVE
				// optionSafeCode		차량 옵션 안전 코드 (REBORN CODE 정의 301:운전석에어백 302:조수석에어백 303:후방카메라 304:후방센서 305:블랙박스)
				// optionCnvncCode		차량 옵션 편의 코드 (REBORN CODE 정의 401:네비게이션 402:리모컨키 403:스마트키 404:열선시트 405:가죽시트 406:썬루프)
				// optionSondCode		차량 옵션 음향 코드 (REBORN CODE 정의 501:블루투스 502:AUX 503:USB
				// fuelCode				연료 타입 코드 	    (REBORN CODE 정의 G:휘발유 D:경유 L:LPG E:전기 H:하이브리드)
				
				// 차량 옵션 안전코드
				String[] optionSafeCodeArr = carMp.get("optionSafeCode").toString().split(",");					 
				List<String> optionSafeCodeList = new ArrayList<String>(Arrays.asList(optionSafeCodeArr));

				for(int k=0; k<optionSafeCodeList.size(); k++) {
					
					for(ZzimcarConstants.RENTCAR_OPTION_CODE code : ZzimcarConstants.RENTCAR_OPTION_CODE.values()) {

						if(optionSafeCodeList.get(k).trim().equals(code.getKey())) {
							mp.put("code_div", code.name().substring(0, code.name().length() - 1));
							mp.put("code_key", code.getValue());
							erpPeristalsisDao.insertRcModelOption(mp);
						}
					}
				}
				
				// 차량 옵션 편의 코드
				String[] optionCnvncCodeArr = carMp.get("optionCnvncCode").toString().split(",");					 
				List<String> optionCnvncCodeList = new ArrayList<String>(Arrays.asList(optionCnvncCodeArr));

				for(int k=0; k<optionCnvncCodeList.size(); k++) {
					
					for(ZzimcarConstants.RENTCAR_OPTION_CODE code : ZzimcarConstants.RENTCAR_OPTION_CODE.values()) {

						if(optionCnvncCodeList.get(k).trim().equals(code.getKey())) {
							
							if(code.name().length()==23 ) {
								if(code.name().toString().indexOf("HEALTH") <= 0) {
									mp.put("code_div", code.name().substring(0, code.name().length() - 2));
								} else {
									mp.put("code_div", code.name().substring(0, code.name().length() - 1));
								}
							}else {
								mp.put("code_div", code.name().substring(0, code.name().length() - 1));
							}
							
							mp.put("code_key", code.getValue());
							erpPeristalsisDao.insertRcModelOption(mp);
						}
					}
				}
				
				// 차량 옵션 음향 코드
				String[] optionSondCodeArr = carMp.get("optionSondCode").toString().split(",");					 
				List<String> optionSondCodeList = new ArrayList<String>(Arrays.asList(optionSondCodeArr));

				for(int k=0; k<optionSondCodeList.size(); k++) {
					
					for(ZzimcarConstants.RENTCAR_OPTION_CODE code : ZzimcarConstants.RENTCAR_OPTION_CODE.values()) {

						if(optionSondCodeList.get(k).trim().equals(code.getKey())) {
							mp.put("code_div", code.name().substring(0, code.name().length() - 1));
							mp.put("code_key", code.getValue());
							erpPeristalsisDao.insertRcModelOption(mp);
						}
					}
				}
				
				// 차량 연도 코드
				
				int yemodelMin = Integer.parseInt(carMp.get("yemodelMin").toString());
				int yemodelMax = Integer.parseInt(carMp.get("yemodelMax").toString());;
				int yemodel = yemodelMax-yemodelMin;
				
				for(int k=0; k<=yemodel; k++) {
					mp.put("code_div", "CAR_YEAR_CODE");
					mp.put("code_key", "Y_"+(yemodelMin+k));
					erpPeristalsisDao.insertRcModelOption(mp);
				}
				
				rebornCnt++;
				
			}
		} // END FOR
		
		return rebornCnt;
	}

	// ERP JSON 타입 가져오기
	public DataJson httpClientJson(String url, Map<String, Object> postParams, String erp_pid) {
		
		long start = System.currentTimeMillis();
		
		String content ="";
		
		try {

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			for(String key : postParams.keySet()) {
				params.add(new BasicNameValuePair(key, postParams.get(key).toString()));
			}
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity respEntity = response.getEntity();
			
			long end = System.currentTimeMillis();
			
			if (respEntity != null) {
				content = EntityUtils.toString(respEntity);
			}
			
			Gson gson = new Gson();
			dataJson = gson.fromJson(content, DataJson.class);
			
			Map<String, Object> mp = new HashMap<String, Object>();
			mp.put("erp_name", ZzimcarController.ERP_INS);
			
			Map<String, Object> erpList = erpPeristalsisDao.erpMasterList(mp);
			
			// LOG
			Map<String, Object> log_list = new HashMap<String, Object>();

			log_list.put("erp_pid", erpList.get("erp_pid"));		// ERP 업체 고유 PID
			log_list.put("api_cmd", postParams.get("cmd"));			// 요청 명령어
			log_list.put("req_message", params.toString());			// 요청 메시지
			log_list.put("res_message", content.toString());		// 응답 메시지
			log_list.put("duration", ( end - start )/1000.0);		// 처리시간
			
			erpPeristalsisDao.inserterpMasterLog(log_list);
			
			
		} catch (ClientProtocolException ex) {
			ex.printStackTrace();
		} catch (IOException ec) {
			ec.printStackTrace();
		}
		
		return dataJson;
	}
	
	// ERP SYNC 등록
	public List<Map<String,Object>> erpSyncChkEx(Map<String, Object> mp) throws Exception{
		
		Map<String, Object> carMp = new HashMap<String, Object>();
		
		carMp.put("erp_pid", mp.get("erp_pid"));
		carMp.put("rccorp_pid", mp.get("rccorp_pid"));
		
		NodeList carList = httpClientXml(ZzimcarController.ERP_JEJU_GRIM, mp);
		Node current = null;
		
		// 정보 Insert
		for (int i = 0; i < carList.getLength(); i++) {

			current = carList.item(i);

			NodeList testChildNodes = current.getChildNodes(); // 노드의 자식노드 가져옴

			for (int j = 0; j<testChildNodes.getLength(); j++) {
				
				Node info = testChildNodes.item(j);

				if (info.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) info;
					carMp.put(element.getTagName(), element.getTextContent());
					//code = if (element.getTagName() == "code") {element.getTextContent());}
				}
			}
			
			Map<String, Object> erpSyncMp = erpPeristalsisDao.selectErpSyncCode(carMp);
			
			if(erpSyncMp == null) {

				carMp.put("erp_pid", mp.get("erp_pid"));
				carMp.put("sync_type", "ERP_CAR_LIST");
				carMp.put("reg_mem_pid", mp.get("reg_mem_pid"));
				
				erpPeristalsisDao.insertErpSyncCode(carMp);
			}
			
		}

		return erpPeristalsisDao.selectAllErpSyncCode(carMp);
	}
	
	// ERP XML 타입 가져오기
	public NodeList httpClientXml(String erpType, Map<String, Object> mp) throws ParserConfigurationException, SAXException, IOException {
	
		
		String url = null;
		
		// XML 
		int INDENT_FACTOR = 4;
		
		// LOG
		Map<String, Object> log_list = new HashMap<String, Object>();
		
		
		if(erpType.equals("REBORN")) {
			url = ZzimcarController.REBORN_URL +
					"?crtfcKey="+ ZzimcarController.CRTFCKEY +
					"&crtfcAuthorKey="+ZzimcarController.CRTFCAUTHORKEY +
					"&clientId=" + mp.get("clientId");
			
			log_list.put("api_cmd", ZzimcarController.JEJU_GRIM_CARLIST);			// 요청 명령어
			
		}else {
			url = ZzimcarController.JEJU_GRIM_FIRST_URL+mp.get("erp_client_cd")+ZzimcarController.JEJU_GRIM_SECOND_URL+ZzimcarController.JEJU_GRIM_CARLIST;
			
			// 유로 렌트카 한정 추가 변수 삽입
			if(mp.get("erp_client_cd").equals("matrixcar.co.kr") && mp.get("erp_call_code") != null) {
				url += "?callcode="+ String.valueOf(mp.get("erp_call_code"));
			}
			log_list.put("api_cmd", ZzimcarController.JEJU_GRIM_CARLIST);			// 요청 명령어
		}
		
		long start = System.currentTimeMillis();
	
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(url);
		Element rootElement = document.getDocumentElement(); // docment의 엘리먼트를 rootElement 객체에 반환
	
		NodeList nodelist;
		
		if(erpType.equals("REBORN")) {
			nodelist = rootElement.getElementsByTagName("body");
			nodelist = rootElement.getElementsByTagName("item");
		}else {
			nodelist = rootElement.getElementsByTagName("Item"); // rootElement에 있는 testlist 의 node는 nodelist의 객체에 담아서 반환됨
		}
    
        long end = System.currentTimeMillis();

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.connect();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuffer st = new StringBuffer();
        String line;
        
        while ((line = reader.readLine()) != null) {
            st.append(line);
        }
 
        JSONObject xmlJSONObj = XML.toJSONObject(st.toString());
        String jsonPrettyPrintString = xmlJSONObj.toString(INDENT_FACTOR);

		log_list.put("erp_pid", mp.get("erp_pid"));		// ERP 업체 고유 PID
		log_list.put("req_message", url);			// 요청 메시지
		log_list.put("res_message", jsonPrettyPrintString);		// 응답 메시지
		log_list.put("duration", ( end - start )/1000.0);		// 처리시간
		
		erpPeristalsisDao.inserterpMasterLog(log_list);
        
		return nodelist;
	}
	
	public List<Map<String, Object>> selectNmcarModelList(){
		return erpPeristalsisDao.selectNmcarModelList();
	}
	
	public void updateErpSyncTx(Map<String, Object> mp) throws Exception{
		erpPeristalsisDao.updateErpSync(mp);
	}

}
