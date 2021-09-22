package com.zzimcar.admin.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.config.ZzimcarConstants.APIKEY;
import com.zzimcar.admin.config.ZzimcarConstants.EXTRACHARGE_COLUMN_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PROVIDER_TYPE;
import com.zzimcar.admin.dao.DirectSkinDao;
import com.zzimcar.admin.dao.ProviderCompanyDao;
import com.zzimcar.admin.dao.ProviderExtrachargeDao;
import com.zzimcar.admin.dao.ProviderRcModelDao;
import com.zzimcar.admin.exception.DomainException;
import com.zzimcar.admin.exception.NotFoundLocationException;
import com.zzimcar.admin.exception.OverLapDataException;

@Service(value = "ProviderService")
public class ProviderService extends ZzimcarService {
	
	@Resource(name = "ProviderCompanyDao")
	ProviderCompanyDao providerCompanyDao;

	@Resource(name = "ProviderExtrachargeDao")
	ProviderExtrachargeDao providerExtrachargeDao;

	@Resource(name = "ProviderRcModelDao")
	ProviderRcModelDao providerRcModelDao;
	
	@Resource(name = "DirectSkinDao")
	DirectSkinDao directSkinDao;

	public Map<String, Object> getProviderList(Map<String, Object> map) throws Exception {
		return providerCompanyDao.search("select_page", map);
	}
	
	public List<Map<String, Object>> getErpList(){
		return providerCompanyDao.selectList("erp_api_list");
	}

	public Map<String, Object> listExtraCharge(Map<String, Object> map) throws Exception {
		return providerExtrachargeDao.search("select_page", map);
	}
	
	public int deleteExtraChargeTx(Map<String, Object> mp) throws Exception {
    	return providerExtrachargeDao.updateByPk("update_delete", mp);
	}

	public Map<String, Object> getRcList(Map<String, Object> map) throws Exception {
		return providerRcModelDao.search("select_page", map);
	}
	
	public void updateStatusProviderTx(Map<String, Object> map) throws Exception {
		Object checkitems = map.get("myCheck");
		Map<String, Object> hashMap = new HashMap<String, Object>();
		String[] updateItem = new String[1];

		if (checkitems instanceof String[]) {
			updateItem = (String[]) checkitems;
		} else {
			updateItem[0] = (String) checkitems;
		}

		for (int i = 0; i < updateItem.length; i++) {
			hashMap.clear();
			
			hashMap.put("provider_pid", updateItem[i]);
			hashMap.put("mod_mem_pid", map.get("mod_mem_pid"));
			hashMap.put("provider_status", map.get("corpStatus"));
			hashMap.put("charge_status", map.get("chargeStatus"));
			
			providerCompanyDao.updateStatusProviderCorp(hashMap);
			providerCompanyDao.updateStatusRentcarCorp(hashMap);
			providerCompanyDao.updateStatusExtracharge(hashMap);
			providerCompanyDao.updateStatusRentcarMaster(hashMap);
		}
	}

	public void updateStatusRcModelItemTx(Map<String, Object> map) throws Exception {
		Object checkitems = map.get("myCheck");
		Map<String, Object> hashMap = new HashMap<String, Object>();
		String[] updateItem = new String[1];

		if (checkitems instanceof String[]) {
			updateItem = (String[]) checkitems;
		} else {
			updateItem[0] = (String) checkitems;
		}

		for (int i = 0; i < updateItem.length; i++) {
			hashMap.clear();
			
			hashMap.put("rcmodel_pid", updateItem[i]);
			hashMap.put("mod_mem_pid", map.get("mod_mem_pid"));
			hashMap.put("model_status", map.get("corpStatus"));

			providerRcModelDao.updateStatus(hashMap);
		}
	}

	@SuppressWarnings("unchecked")
	public void insertProviderItemTx(Map<String, Object> param) throws Exception {
		int checkCodeCount = providerCompanyDao.select_code_count(param);
		if (checkCodeCount != 0) {
			throw new OverLapDataException();
		}
		
		int checkDomainCount = providerCompanyDao.getDomainCheck((String) param.get("dir_domain"), -1);
		if(checkDomainCount > 0) {
			throw new DomainException();
		}
		
		if(param.get("map_x") == null ||  param.get("map_y") == null) {
			// 위도 경도 부분
			Double x = 0.0, y = 0.0;
			String Address = (String) param.get("corp_address");
			String RequestUrl = "https://dapi.kakao.com/v2/local/search/address.json";

			// post 파라미터 설정
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair("query", Address));

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(RequestUrl);

			// 인증 형식에 카카오 추가
			post.addHeader("Authorization", "KakaoAK " + APIKEY.KAKAO_REST_API_KEY);
			// post 파라미터 한글 ?팁 방지
			post.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));

			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			String data = EntityUtils.toString(entity);

			HashMap<String, Object> rs = new ObjectMapper().readValue(data, HashMap.class);
			ArrayList<Map<String, Object>> searchAddress = (ArrayList<Map<String, Object>>) rs.get("documents");
			if (searchAddress.size() == 0) {
				throw new NotFoundLocationException();
			}

			Map<String, Object> temp = (Map<String, Object>) searchAddress.get(0).get("road_address");
			x = Double.parseDouble((String) temp.get("x"));
			y = Double.parseDouble((String) temp.get("y"));
			param.put("map_x", y);
			param.put("map_y", x);
		}

		providerCompanyDao.insert(param);

		if(param.get("corp_type").equals(PROVIDER_TYPE.RENTCAR.name())) {
			param.put("providerPid", param.get("pid"));
			providerCompanyDao.insert("insert_rentcar_corp_by_pk", param);
			
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
			ExtraMap.put("reg_mem_pid", param.get("reg_mem_pid"));
			ExtraMap.put("mod_mem_pid", param.get("reg_mem_pid"));
			// 업체 고유 PID
			ExtraMap.put("rccorp_pid", param.get("rentcar_pid"));

			providerExtrachargeDao.insert(ExtraMap);
			
			if(param.get("is_direct").equals("Y")) {
				int pid = Integer.parseInt(String.valueOf(param.get("rentcar_pid")));
				if(directSkinDao.getCountLayoutSetting(pid) == 0) {
					Map<String, Object> DirectMap = new HashMap<String, Object>();
					DirectMap.put("reg_mem_pid", param.get("reg_mem_pid"));
					DirectMap.put("rccorp_pid", param.get("rentcar_pid"));
					DirectMap.put("corp_name", param.get("corp_name"));
					
					directSkinDao.insertDefault(DirectMap);
				}
			}
		}
	}

	public void insertRCModlItemTx(Map<String, Object> param) throws Exception {
		providerRcModelDao.insert(param);
		
		Object checkitems = param.get("rmoption");
		if(checkitems != null) {
			String[] updateItem = new String[1];
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			if (checkitems instanceof String[]) {
				updateItem = (String[]) checkitems;
			} else {
				updateItem[0] = (String) checkitems;
			}

			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < updateItem.length; i++) {
				map = new HashMap<String, Object>();
				map.put("rcmodel_pid", param.get("pid"));

				String[] temp = updateItem[i].split(":-/-:");
				map.put("code_div", temp[0]);
				map.put("code_key", temp[1]);

				list.add(map);
			}
			providerRcModelDao.deleteAfterInsert(list);
		}
	}

	@SuppressWarnings({ "unchecked"})
	public void updateProviderItemTx(Map<String, Object> param) throws Exception {
		Map<String, Object> originalItem = selectProviderItemByPk(
				Integer.parseInt((String) param.get("providerPid")));
		
		if (!param.get("corp_address").equals(originalItem.get("corpAddress"))) {
			
			if(param.get("map_x") == null || param.get("map_y") == null) {
				Double x = 0.0;
				Double y = 0.0;
				String Address = (String) param.get("corp_address");
				String RequestUrl = "https://dapi.kakao.com/v2/local/search/address.json";

				// post 파라미터 설정
				List<NameValuePair> postParams = new ArrayList<NameValuePair>();
				postParams.add(new BasicNameValuePair("query", Address));

				HttpClient client = HttpClientBuilder.create().build();
				HttpPost post = new HttpPost(RequestUrl);

				// 인증 형식에 카카오 추가
				post.addHeader("Authorization", "KakaoAK " + APIKEY.KAKAO_REST_API_KEY);
				// post 파라미터 한글 ?팁 방지
				post.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));

				HttpResponse response = client.execute(post);
				HttpEntity entity = response.getEntity();
				String data = EntityUtils.toString(entity);

				HashMap<String, Object> rs = new ObjectMapper().readValue(data, HashMap.class);
				ArrayList<Map<String, Object>> searchAddress = (ArrayList<Map<String, Object>>) rs.get("documents");
				if (searchAddress.size() == 0) {
					throw new NotFoundLocationException();
				}
				Map<String, Object> temp = (Map<String, Object>) searchAddress.get(0).get("road_address");
				x = Double.parseDouble((String) temp.get("x"));
				y = Double.parseDouble((String) temp.get("y"));
				param.put("map_x", y);
				param.put("map_y", x);
			}
		}

		providerCompanyDao.updateByPk(param);
		
		String pids = String.valueOf(param.get("providerPid"));
		int pid = Integer.parseInt(pids.equals("") ? "0" : pids);
		
		Map<String, Object> rentcarCompany = selectRentcarCorpData(pid);
		// 은행명, 은행계좌, 계좌주중 하나라도 바꼈을때
		if (!param.get("corpBankNum").equals(originalItem.get("corp_bank_num")) || !param.get("corpBankName").equals(originalItem.get("corp_bank_name")) || !param.get("corpBankCode").equals(originalItem.get("corp_bank_code")) || !param.get("corpBankHolder").equals(originalItem.get("corp_bank_holder"))) {
			// 렌터카 업체만 정산 하기 때문에
			if(rentcarCompany != null) {
				List<Map<String, Object>> settList = providerCompanyDao.selectList("getSettleMentListBeforeFinish", rentcarCompany);
				
				if(settList != null && settList.size() > 0) {
					Map<String, Object> backAndList = new HashMap<>();
					backAndList.put("corp_bank_num", param.get("corpBankNum"));
					backAndList.put("corp_bank_code", param.get("corpBankCode"));
					backAndList.put("corp_bank_holder", param.get("corpBankHolder"));
					backAndList.put("corp_bank_name", param.get("corpBankName"));
					
					backAndList.put("settList", settList);
					
					backAndList.put("reg_mem_pid", param.get("reg_mem_pid"));
					
					providerCompanyDao.updateSettlementBankInfo(backAndList);
				}
			}
		}
		
		if(param.get("corp_type").equals(PROVIDER_TYPE.RENTCAR.name())) {
			if(rentcarCompany == null) {
				int checkDomainCount = providerCompanyDao.getDomainCheck((String) param.get("dir_domain"), -1);
				if(checkDomainCount > 0) {
					throw new DomainException();
				}
				providerCompanyDao.insert("insert_rentcar_corp_by_pk", param);
			} else {
				param.put("rentcar_pid", rentcarCompany.get("rccorpPid"));
				
				int rpid = Integer.parseInt(String.valueOf(param.get("rentcar_pid")));
				int checkDomainCount = providerCompanyDao.getDomainCheck((String) param.get("dir_domain"), rpid);
				if(checkDomainCount > 0) {
					throw new DomainException();
				}
				
				providerCompanyDao.updateByPk("update_rentcar_corp_by_pk", param);
			}
			
			/*Map<String, Object> ExtrachargeItem = providerExtrachargeDao
					.selectByProviderPid(Integer.parseInt((String) param.get("providerPid")));
			if (ExtrachargeItem == null) {
				Map<String, Object> ExtraMap = new HashMap<>();
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
				ExtraMap.put("reg_mem_pid", param.get("reg_mem_pid"));
				ExtraMap.put("mod_mem_pid", param.get("reg_mem_pid"));
				// 업체 고유 PID
				ExtraMap.put("rccorp_pid", param.get("rentcar_pid"));
				providerExtrachargeDao.insert(ExtraMap);
			}
			*/
			
			if(param.get("is_direct").equals("Y")) {
				int dpid = Integer.parseInt(String.valueOf(param.get("rentcar_pid")));
				if(directSkinDao.getCountLayoutSetting(dpid) == 0) {
					Map<String, Object> DirectMap = new HashMap<String, Object>();
					DirectMap.put("reg_mem_pid", param.get("reg_mem_pid"));
					DirectMap.put("rccorp_pid", param.get("rentcar_pid"));
					DirectMap.put("corp_name", param.get("corp_name"));
					
					directSkinDao.insertDefault(DirectMap);
				}
			}
		}
	}

	public void updateRCModlItemTx(Map<String, Object> param) throws Exception {
		providerRcModelDao.updateByPk(param);
		
		Object checkitems = param.get("rmoption");
		String[] updateItem = new String[1];
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		if (checkitems instanceof String[]) {
			updateItem = (String[]) checkitems;
		} else {
			updateItem[0] = (String) checkitems;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < updateItem.length; i++) {
			map = new HashMap<String, Object>();
			map.put("rcmodel_pid", param.get("rcmodel_pid"));

			String[] temp = updateItem[i].split(":-/-:");
			map.put("code_div", temp[0]);
			map.put("code_key", temp[1]);

			list.add(map);
		}
		providerRcModelDao.deleteAfterInsert(list);
	}

	public Map<String, Object> selectProviderItemByPk(int pid) throws Exception {
		return providerCompanyDao.selectByPk(pid);
	}

	public Map<String, Object> selectRcModelItemByPk(int pid) throws Exception {
		return providerRcModelDao.selectByPk(pid);
	}

	public List<Map<String, Object>> selectRcModelItemOptionByPk(int index) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("rcmodel_pid", index);
		return providerRcModelDao.selectList("select_rcmodel_option_by_pk", map);
	}
	
	public Map<String, Object> selectRentcarCorpData(int pid) {
		return providerCompanyDao.selectByPk("select_rentcar_corp", pid);
	}
	
	public Map<String, Object> selectRentcarManagerCorpData(Map<String, Object> map){
		return providerCompanyDao.search("select_rentcarManager_corp", map);
	}

	public List<Map<String, Object>> getRentcarCompanyList(Map<String, Object> param) throws Exception {
		return providerRcModelDao.selectList("select_company_list", param);
	}

	public List<Map<String, Object>> getRentcarModelList(Map<String, Object> param) throws Exception {
		return providerRcModelDao.selectList("select_car_list", param);
	}

	public Map<String, Object> writeExtraCharge(int pid) throws Exception {
		return providerExtrachargeDao.selectByPk("select_by_view", pid);
	}
	
	public Map<String, Object> dateCheckExtraCharge(Map<String, Object> map) throws Exception {
		return providerExtrachargeDao.selectOne("select_by_datecheck", map);
	}

	public int saveExtraCharge(Map<String, Object> map) throws Exception {
		return providerExtrachargeDao.updateByPk(map);
	}
	
	public int insertExtraCharge(Map<String, Object> map) throws Exception {
		return providerExtrachargeDao.insert(map);
	}
	
	// 정산 리스트
	public Map<String, Object> selectSettlementTolCnt(Map<String, Object> param){
		return providerCompanyDao.search("selectSettlementList", param);
	}
	
	// 정산 Excel 리스트
	public List<Map<String, Object>> selectSettlementListExcel(Map<String, Object> param){
		return providerCompanyDao.selectSettlementListExcel(param);
	}
	
	// 정산 상세리스트
	public Map<String, Object> selectSettlementDetailTolCnt(Map<String, Object> param){
		return providerCompanyDao.search("selectSettlementDetail", param);
	}
	
	// 정산 Excel 리스트
	public List<Map<String, Object>> selectSettlementDetailListExcel(Map<String, Object> param){
		return providerCompanyDao.selectSettlementDetailListExcel(param);
	}
	
	// 정산 인보이스 리스트
	public List<Map<String, Object>> selectSettleInVoceList(Map<String, Object> param){
		return providerCompanyDao.selectSettleInVoceList(param);
	}
	
	// 정산 완료 처리
	public void updateSettlementTx(Map<String, Object> param) throws Exception{
		providerCompanyDao.updateSettlement(param);
	}
	
	// 정산 INSERT
	@SuppressWarnings("unused")
	public void insertSettleTx(Map<String, Object> param) throws Exception {
		
		Map<String, Object> settlementResult = new HashMap<String, Object>();
		
		Calendar c = Calendar.getInstance();
		String ntime = new String();
		ntime = String.valueOf(c.get(Calendar.YEAR));
		
		if(c.get(Calendar.MONTH+1)<10) {
			ntime += "0"+String.valueOf(c.get(Calendar.MONTH)+1);
		}else {
			ntime += String.valueOf(c.get(Calendar.MONTH)+1);
		}
		
		int day = c.get(Calendar.DATE);
		
		if( day!=01 && day<16 && day!=16) {
			
			settlementResult.put("settl_sdate",ntime+"01000000");										// 1회 정산 시작일자
			settlementResult.put("settl_edate",ntime+"15235959");										// 1회 정산 끝일자 
			
			settlementResult.put("sdate", ntime+"01");
			settlementResult.put("edate", ntime+"15");
		
		}else {
			
			Calendar c1 = new GregorianCalendar();

			c1.add(Calendar.DATE, -1); // 오늘날짜로부터 -1

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			SimpleDateFormat edf = new SimpleDateFormat("yyyyMMdd");
			

			String sDate = sdf.format(c1.getTime());
			String eDate = edf.format(c1.getTime());
			
			
			settlementResult.put("settl_sdate",sDate+"16000000");									// 2회 정산 시작일자
			settlementResult.put("settl_edate",eDate+"235959");										// 2회 정산 끝일자 
			
			settlementResult.put("sdate", sDate+"16");
			settlementResult.put("edate", eDate);

		}
		
		
		if( param.get("type").equals("01")) {
			
			settlementResult.put("settl_sdate",param.get("sDate")+"000000");				// 1회 정산 시작일자
			settlementResult.put("settl_edate",param.get("eDate")+"235959");			// 1회 정산 끝일자 
			
			settlementResult.put("sdate", param.get("sDate"));
			settlementResult.put("edate", param.get("eDate"));
		
		}else if(param.get("type").equals("02")) {
			
			settlementResult.put("settl_sdate",param.get("sDate")+"000000");												// 2회 정산 시작일자
			settlementResult.put("settl_edate",param.get("eDate")+"235959");											// 2회 정산 끝일자 
			
			settlementResult.put("sdate", param.get("sDate"));
			settlementResult.put("edate", param.get("eDate"));
			
		}
		
		settlementResult.put("is_direct", "N");
		settlementResult.put("rate_chk",0);
		
		// 찜카 정산 리스트조회
		List<Map<String, Object>> result = providerCompanyDao.selectSettlementOrderList(settlementResult);

		
		// 찜카 정산
		for(int i=0; i<result.size(); i++) {
			
			float rateSettlRentcar = Float.parseFloat(result.get(i).get("rateSettlRentcar").toString());											// 대여료 정산율
			float rateSettlExtraCharge = Float.parseFloat(result.get(i).get("rateSettlExtraCharge").toString());							// 할증금 정산율
			
			int priceSale = Integer.parseInt(result.get(i).get("priceSale").toString());																			// 전체 차량대여료
			int priceInsuSale = Integer.parseInt(result.get(i).get("priceInsuSale").toString());															// 전체 차량 보험료
			int priceSum = priceSale + priceInsuSale;																																	// 전체 차량대여료 + 전체 차량 보험료
			float reteSettlRentCar = 1-(rateSettlRentcar/100); 																													// (1 - 대여료정산율/100) 
			
			
			
			int cancelRentcarPrice = Integer.parseInt(result.get(i).get("cancelPriceSale").toString());											// 전체 취소차량 대여료
			int cancelPriceInsuSale = Integer.parseInt(result.get(i).get("cancelPriceInsuSale").toString());									// 전체 취소차량 보험료
			int cancelSumPrice = cancelRentcarPrice + cancelPriceInsuSale;																							// 전체 취소차량 대여료 + 전체 취소차량 보험료
			//float cancelReteSettlRentCar = 1-(rateSettlRentcar/100); 																									// (1 - 대여료정산율/100)

			
			int rentSumPrice = priceSum - cancelSumPrice;
			int settlRentcarSumPrice = (int) (rentSumPrice * reteSettlRentCar) ;																					// 대여료 정산금액 합계 ((차량대여료+차량보험료) - (취소차량대여료+취소보험료)) * 정산율														

			
			int extraCharge = Integer.parseInt(result.get(i).get("extrachargePrice").toString());														// 전체 할증료 
			int cancelExtrachargePrice = Integer.parseInt(result.get(i).get("cancelExtrachargePrice").toString());					// 전체 취소 할증료
			int settlExtrachargePrice =  extraCharge - cancelExtrachargePrice;																						
			settlExtrachargePrice = (int)(settlExtrachargePrice * (1-(rateSettlExtraCharge/100)));													// 할증금 정산금액 (전체 할증료 - 전체 취소 할증료) * 0.5
			
			// 대여/반납 탁송비
			int priceConsignSum = Integer.parseInt(result.get(i).get("priceConsignSum").toString());
			
			int settlRefundPrice = Integer.parseInt(result.get(i).get("orderRefundSettPrice").toString());	 								// 환불건의 합계 (환불정산금액)
			int settlFinalPrice = settlRentcarSumPrice + settlExtrachargePrice +priceConsignSum;																						// 총 정산금액 (대여료정산금액 + 할증료 정산금액 + 환불)
			/*
			int taxSettlFinalPrice = settlFinalPrice/10;															// 정산금액 부가세
			int taxSumSettlFinalPrice = (settlFinalPrice+taxSettlFinalPrice);										// 부가세포함 총 정산금액(총 정산금액+부가세)
			*/
			int zimcar_fee = ((priceSum-cancelSumPrice)+ settlRefundPrice + priceConsignSum) - settlFinalPrice;															// 찜카 수수료마진 = ((전체차량대여료 + 전체보험료) - (전체취소차량대여료 + 전체취소보험료) + 환불합계금액) -  총 정산금액
			
			settlementResult.put("rccorp_pid",result.get(i).get("rccorpPid"));																						// 렌트카 PID
			settlementResult.put("rccorp_name",result.get(i).get("rccorpName"));																			// 렌트카명
			
			settlementResult.put("settl_cycle","M2");																																	// 정산 주기

			settlementResult.put("rentcar_price",priceSale);																														// 전체차량대여료
			settlementResult.put("insu_price",priceInsuSale);																													// 전체차량보험료
			settlementResult.put("extra_charge",extraCharge);																												// 전체 할증료 
			settlementResult.put("settl_extracharge_price",settlExtrachargePrice);																			// 할증료 정산금액
			settlementResult.put("settl_rentcar_sum_price",settlRentcarSumPrice);																			// 대여료 정산금액 합계
			
			settlementResult.put("cancel_rentcar_price", cancelRentcarPrice); 																					// 전체 취소차량대여료
			settlementResult.put("cancel_insu_price", cancelPriceInsuSale); 																						// 전체 취소차량 보험료 
			settlementResult.put("cancel_extra_charge", cancelExtrachargePrice); 																			// 전체 취소 할증료
			
			settlementResult.put("settl_refund_price", settlRefundPrice); 																								// 환불 정산금액 합계
			
			settlementResult.put("settl_consign_price", priceConsignSum);
			
			settlementResult.put("settl_final_price", settlFinalPrice); 																										// 총 정산금액
			settlementResult.put("zimcar_fee", zimcar_fee); 																													// 찜카 마진
			
			settlementResult.put("corp_bank_num",result.get(i).get("bankNum"));																			// 업체 입금계좌번호
			settlementResult.put("corp_bank_name",result.get(i).get("bankName"));																		// 업체 은행명
			settlementResult.put("corp_bank_code",result.get(i).get("bankCode"));																			// 은행코드
			settlementResult.put("corp_bank_holder",result.get(i).get("bankHolder"));																	// 업체 예금주
			settlementResult.put("rate_settl_rentcar", result.get(i).get("rateSettlRentcar"));															// 대여료 정산율
			settlementResult.put("rate_settl_extra_charge", result.get(i).get("rateSettlExtraCharge"));										// 할증금 정산율
			settlementResult.put("rate_cancel_in_rent24",result.get(i).get("rateCancelInRent24"));											// 대여 24시간 전 수수료율
			settlementResult.put("rate_bankruptcy_booking",90);																											// 예약부도 (사용안함)
			settlementResult.put("settl_status","00");																																	// 상태
			settlementResult.put("settl_mem_pid",0000);																														    // 정산처리자

			//settlementResult.put("taxSumSettlFinalPrice", taxSumSettlFinalPrice);															//부사세포함정산금액

			settlementResult.put("tax_settl_final_price", 0);																	//부사세
			
			
			// rccorpPid / sdate, edate
			List<Map<String, Object>> settlCnt = providerCompanyDao.selectList("settlCnt", settlementResult);
			
			if(settlCnt.size()==0) {
				providerCompanyDao.insertSettle(settlementResult);
			}
			
		} // END for
		
		
		settlementResult.put("is_direct", "Y");
		settlementResult.put("rate_chk",1);
		
		// 다이렉트 정산 리스트조회
		List<Map<String, Object>> result2 = providerCompanyDao.selectSettlementOrderList(settlementResult);

		
		// 다이렉트 정산
		for(int i=0; i<result2.size(); i++) {
			
			float rateSettlRentcar = Float.parseFloat(result2.get(i).get("rateSettlRentcar").toString());											// 대여료 정산율
			float rateSettlExtraCharge = Float.parseFloat(result2.get(i).get("rateSettlExtraCharge").toString());							// 할증금 정산율
			
			int priceSale = Integer.parseInt(result2.get(i).get("priceSale").toString());																			// 전체 차량대여료
			int priceInsuSale = Integer.parseInt(result2.get(i).get("priceInsuSale").toString());															// 전체 차량 보험료
			int priceSum = priceSale + priceInsuSale;																																	// 전체 차량대여료 + 전체 차량 보험료
			float reteSettlRentCar = 1-(rateSettlRentcar/100); 																													// (1 - 대여료정산율/100) 
			
			
			
			int cancelRentcarPrice = Integer.parseInt(result2.get(i).get("cancelPriceSale").toString());											// 전체 취소차량 대여료
			int cancelPriceInsuSale = Integer.parseInt(result2.get(i).get("cancelPriceInsuSale").toString());									// 전체 취소차량 보험료
			int cancelSumPrice = cancelRentcarPrice + cancelPriceInsuSale;																							// 전체 취소차량 대여료 + 전체 취소차량 보험료
			//float cancelReteSettlRentCar = 1-(rateSettlRentcar/100); 																									// (1 - 대여료정산율/100)

			
			int rentSumPrice = priceSum - cancelSumPrice;
			int settlRentcarSumPrice = (int) (rentSumPrice * reteSettlRentCar) ;																					// 대여료 정산금액 합계 ((차량대여료+차량보험료) - (취소차량대여료+취소보험료)) * 정산율														

			
			int extraCharge = Integer.parseInt(result2.get(i).get("extrachargePrice").toString());														// 전체 할증료 
			int cancelExtrachargePrice = Integer.parseInt(result2.get(i).get("cancelExtrachargePrice").toString());					// 전체 취소 할증료
			int settlExtrachargePrice =  extraCharge - cancelExtrachargePrice;																						
			settlExtrachargePrice = (int)(settlExtrachargePrice * (1-(rateSettlExtraCharge/100)));													// 할증금 정산금액 (전체 할증료 - 전체 취소 할증료) * 0.5
			
			// 대여/반납 탁송비
			int priceConsignSum = Integer.parseInt(result2.get(i).get("priceConsignSum").toString());
			
			int settlRefundPrice = Integer.parseInt(result2.get(i).get("orderRefundSettPrice").toString());	 								// 환불건의 합계 (환불정산금액)
			int settlFinalPrice = settlRentcarSumPrice + settlExtrachargePrice + priceConsignSum;																						// 총 정산금액 (대여료정산금액 + 할증료 정산금액 + 환불)
			
			/*int taxSettlFinalPrice = settlFinalPrice/10;															// 정산금액 부가세
			int taxSumSettlFinalPrice = (settlFinalPrice+taxSettlFinalPrice);										// 부가세포함 총 정산금액(총 정산금액+부가세)
*/			
			int zimcar_fee = (((priceSum+extraCharge)-(cancelSumPrice+cancelExtrachargePrice))+ settlRefundPrice + priceConsignSum) - settlFinalPrice;			// 찜카 수수료마진 = ((전체차량대여료 + 전체보험료+전체할증료) - (전체취소차량대여료 + 전체취소보험료+전체취소할증료) + 환불합계금액) -  총 정산금액
			
			settlementResult.put("rccorp_pid",result2.get(i).get("rccorpPid"));																						// 렌트카 PID
			settlementResult.put("rccorp_name",result2.get(i).get("rccorpName"));																			// 렌트카명
			
			settlementResult.put("settl_cycle","M2");																																	// 정산 주기

			settlementResult.put("rentcar_price",priceSale);																														// 전체차량대여료
			settlementResult.put("insu_price",priceInsuSale);																													// 전체차량보험료
			settlementResult.put("extra_charge",extraCharge);																												// 전체 할증료 
			settlementResult.put("settl_extracharge_price",settlExtrachargePrice);																			// 할증료 정산금액
			settlementResult.put("settl_rentcar_sum_price",settlRentcarSumPrice);																			// 대여료 정산금액 합계
			
			settlementResult.put("cancel_rentcar_price", cancelRentcarPrice); 																					// 전체 취소차량대여료
			settlementResult.put("cancel_insu_price", cancelPriceInsuSale); 																						// 전체 취소차량 보험료 
			settlementResult.put("cancel_extra_charge", cancelExtrachargePrice); 																			// 전체 취소 할증료
			
			settlementResult.put("settl_refund_price", settlRefundPrice); 																								// 환불 정산금액 합계
			
			settlementResult.put("settl_consign_price", priceConsignSum);
			
			settlementResult.put("settl_final_price", settlFinalPrice); 																										// 총 정산금액
			settlementResult.put("zimcar_fee", zimcar_fee); 																													// 찜카 마진
			
			settlementResult.put("corp_bank_num",result2.get(i).get("bankNum"));																			// 업체 입금계좌번호
			settlementResult.put("corp_bank_name",result2.get(i).get("bankName"));																		// 업체 은행명
			settlementResult.put("corp_bank_code",result2.get(i).get("bankCode"));																			// 은행코드
			settlementResult.put("corp_bank_holder",result2.get(i).get("bankHolder"));																	// 업체 예금주
			settlementResult.put("rate_settl_rentcar", result2.get(i).get("rateSettlRentcar"));															// 대여료 정산율
			settlementResult.put("rate_settl_extra_charge", result2.get(i).get("rateSettlExtraCharge"));										// 할증금 정산율
			settlementResult.put("rate_cancel_in_rent24",result2.get(i).get("rateCancelInRent24"));											// 대여 24시간 전 수수료율
			settlementResult.put("rate_bankruptcy_booking",90);																											// 예약부도 (사용안함)
			settlementResult.put("settl_status","00");																																	// 상태
			settlementResult.put("settl_mem_pid",0000);																														    // 정산처리자

			
			//settlementResult.put("taxSumSettlFinalPrice", taxSumSettlFinalPrice);															//부사세포함정산금액

			//settlementResult.put("tax_settl_final_price", taxSettlFinalPrice);																	//부사세
			
			// rccorpPid / sdate, edate
			List<Map<String, Object>> settlCnt = providerCompanyDao.selectList("settlCnt", settlementResult);
			
			if(settlCnt.size()==0) {
				providerCompanyDao.insertSettle(settlementResult);
			}
			
		} // END for
		
		
	}
	//정산 대기 현황
	@SuppressWarnings("unchecked")
	public Map<String, Object> getSettlePriceWaitInfo(Map<String, Object> map) throws Exception {

		GregorianCalendar  cal = new GregorianCalendar();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String today = sdf.format(date);
		//15일이 지났는지 확인
		String agoDate = String.valueOf(cal.get(Calendar.YEAR));
		String toMonth ="";
		String nextMonth = "";
		if((cal.get(Calendar.MONTH)+1)<10) {
			//이번달
			toMonth += "0"+String.valueOf(cal.get(Calendar.MONTH)+1);
			//다음달
			if((cal.get(Calendar.MONTH)+1) == 9) {
				nextMonth += String.valueOf(cal.get(Calendar.MONTH)+2);
			}else {
				nextMonth += "0"+String.valueOf(cal.get(Calendar.MONTH)+2);
			}

		}else {
			toMonth += String.valueOf(cal.get(Calendar.MONTH)+1);
			//다음달
			if((cal.get(Calendar.MONTH)+1) == 12) {
				nextMonth += "0"+String.valueOf(cal.get(Calendar.MONTH)+2);
			}
			else {
				nextMonth += String.valueOf(cal.get(Calendar.MONTH)+2);
			}

		}	
		String agoOnehalfStartDay ="01000000";
		String agoOnehalfLastDay = "15235959";		
		//날짜비교 기준점
		//1일기준날짜
		String totOnehalfStartDate = agoDate+toMonth+agoOnehalfStartDay;
		//15일기준날짜
		String totOnehalfLastDate = agoDate+toMonth+agoOnehalfLastDay;
		
		String agoTwohalfStartDay ="16000000";
		String agoTwoalfThisLastDay = String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH))+"235959";

		//16일 기준 이번달꺼
		String totTwoHalfThisStartDay = agoDate+toMonth+agoTwohalfStartDay;
		//말일
		String totTwoHalfThisLastDay = agoDate+toMonth+agoTwoalfThisLastDay;
		
		//날짜 비교
		int dateCompare = totOnehalfLastDate.compareTo(today);
		List<Map<String, Object>> checkSettleSize = new ArrayList<>();
		//List<Map<String, Object>> check16SettleSize = new ArrayList<>();
		Map<String, Object> agoOrderListMap = new HashMap<>();
		//등록된 제외 회사들
		List<String> rccorpPidList = new ArrayList<>();
		if(dateCompare >= 0) {
			map.put("settlSdate", totOnehalfStartDate);
			map.put("settlEdate", totOnehalfLastDate);
			//현재 15일까지 정산 진행중인지 확인 
			checkSettleSize = providerCompanyDao.getSettlePriceInfo(map);

					//15기준 시작일
					//업체코드 설정
			if(checkSettleSize.size() > 0){
			rccorpPidList = checkSettleSize.stream()
					.map(pid -> pid.get("rccorpPid").toString()).collect(Collectors.toList());
			
			map.put("rccorpPidList", rccorpPidList);
			}
			map.put("settlSdate", totOnehalfStartDate);
			map.put("toDay", today);
			//정산일 설정
			String setttleDate = agoDate+toMonth+"26";
			map.put("setttleDateCheck", "before");	
			map.put("setttleDate", setttleDate);	
			
			agoOrderListMap = providerCompanyDao.getSettlePriceWaitInfo(map);
				
		}
		else {
			map.put("settlSdate", totTwoHalfThisStartDay);
			map.put("settlEdate", totTwoHalfThisLastDay);
			//현재 16일부터 정산 진행중인지 확인 
			checkSettleSize = providerCompanyDao.getSettlePriceInfo(map);
			//check16SettleSize = getSettlePriceCheck.size();
			if(checkSettleSize.size() > 0){
			rccorpPidList = checkSettleSize.stream()
					.map(pid -> pid.get("rccorpPid").toString()).collect(Collectors.toList());
				
			map.put("rccorpPidList", rccorpPidList);
			}
			map.put("settlSdate", totTwoHalfThisStartDay);
			map.put("toDay", today);
			//정산일 설정
			String setttleDate = agoDate+nextMonth+"11";
			map.put("setttleDateCheck", "after");	
			map.put("setttleDate", setttleDate);
			agoOrderListMap =providerCompanyDao.getSettlePriceWaitInfo(map);
			
			
			}	
		Map<String, Object> resultMap = new HashMap<>();

		List<Map<String, Object>> agoOrderList = new ArrayList<>();
		agoOrderList = (List<Map<String, Object>>) agoOrderListMap.get("rows");
		
		List<Map<String, Object>> settleOrderList = new ArrayList<>();
		if(agoOrderList != null) {
			
			for(int i = 0; i < agoOrderList.size(); i++) {

				Map<String,Object> resultOrderList = new HashMap<>();
				
				float rateSettlRentcar = Float.parseFloat(agoOrderList.get(i).get("rateSettlRentcar").toString());				// 대여료 정산율
				float rateSettlExtraCharge = Float.parseFloat(agoOrderList.get(i).get("rateSettlExtraCharge").toString());		// 할증금 정산율
				
				int priceSale = Integer.parseInt(agoOrderList.get(i).get("priceSale").toString());								// 전체 차량대여료
				int priceInsuSale = Integer.parseInt(agoOrderList.get(i).get("priceInsuSale").toString());						// 전체 차량 보험료
				int priceSum = priceSale + priceInsuSale;																		// 전체 차량대여료 + 전체 차량 보험료
				float reteSettlRentCar = 1-(rateSettlRentcar/100); 																// (1 - 대여료정산율/100) 
				
				
				
				int cancelRentcarPrice = Integer.parseInt(agoOrderList.get(i).get("cancelPriceSale").toString());				// 전체 취소차량 대여료
				int cancelPriceInsuSale = Integer.parseInt(agoOrderList.get(i).get("cancelPriceInsuSale").toString());			// 전체 취소차량 보험료
				int cancelSumPrice = cancelRentcarPrice + cancelPriceInsuSale;													// 찜카 수수료마진 = ((전체차량대여료 + 전체보험료+전체할증료) - (전체취소차량대여료 + 전체취소보험료+전체취소할증료) + 환불합계금액) -  총 정산금액											// 전체 취소차량 대여료 + 전체 취소차량 보험료
				
				@SuppressWarnings("unused")
				float cancelReteSettlRentCar = 1-(rateSettlRentcar/100); 														// (1 - 대여료정산율/100)

				
				int rentSumPrice = priceSum - cancelSumPrice;
				int settlRentcarSumPrice = (int) (rentSumPrice * reteSettlRentCar) ;											// 대여료 정산금액 합계 ((차량대여료+차량보험료) - (취소차량대여료+취소보험료)) * 정산율														

				
				int extraCharge = Integer.parseInt(agoOrderList.get(i).get("extrachargePrice").toString());						// 전체 할증료 
				int cancelExtrachargePrice = Integer.parseInt(agoOrderList.get(i).get("cancelExtrachargePrice").toString());	// 전체 취소 할증료
				int settlExtrachargePrice =  extraCharge - cancelExtrachargePrice;																						
				settlExtrachargePrice = (int)(settlExtrachargePrice * (1-(rateSettlExtraCharge/100)));							// 할증금 정산금액 (전체 할증료 - 전체 취소 할증료) * 0.5
				
				// 대여/반납 탁송비
				int priceConsignSum = Integer.parseInt(agoOrderList.get(i).get("priceConsignSum").toString());
				
				int settlRefundPrice = Integer.parseInt(agoOrderList.get(i).get("orderRefundSettPrice").toString());	// 환불건의 합계 (환불정산금액)
				int settlFinalPrice = settlRentcarSumPrice + settlExtrachargePrice + priceConsignSum;										// 총 정산금액 (대여료정산금액 + 할증료 정산금액 + 환불 + 탁송비)
				int taxSettlFinalPrice = settlFinalPrice/10;															// 정산금액 부가세
				int taxSumSettlFinalPrice = (settlFinalPrice+taxSettlFinalPrice);										// 부가세포함 총 정산금액(총 정산금액+부가세)
				int zimcar_fee = (((priceSum+extraCharge)-(cancelSumPrice+cancelExtrachargePrice))+ settlRefundPrice + priceConsignSum) - settlFinalPrice;															// 찜카 수수료마진 = ((전체차량대여료 + 전체보험료+전체할증료) - (전체취소차량대여료 + 전체취소보험료+전체취소할증료) + 환불합계금액 + 탁송비) -  총 정산금액
				//정산 계산된 데이터
				resultOrderList.put("rccorpPid",agoOrderList.get(i).get("rccorpPid"));									// 렌트카 PID
				resultOrderList.put("rccorpName",agoOrderList.get(i).get("rccorpName"));								// 렌트카명
				
				
				resultOrderList.put("settlCycle","M2");																// 정산 주기
				

				resultOrderList.put("rentcarPrice",priceSale);														// 전체차량대여료
				resultOrderList.put("insuPrice",priceInsuSale);														// 전체차량보험료
				resultOrderList.put("extraCharge",extraCharge);														// 전체 할증료 
				resultOrderList.put("settlExtrachargePrice",settlExtrachargePrice);									// 할증료 정산금액
				resultOrderList.put("settlRentcarSumPrice",settlRentcarSumPrice);									// 대여료 정산금액 합계
				
				resultOrderList.put("cancelRentcarPrice", cancelRentcarPrice); 										// 전체 취소차량대여료
				resultOrderList.put("cancelInsuPrice", cancelPriceInsuSale); 										// 전체 취소차량 보험료 
				resultOrderList.put("cancelExtraCharge", cancelExtrachargePrice); 									// 전체 취소 할증료
				
				resultOrderList.put("settlRefundPrice", settlRefundPrice); 											// 환불 정산금액 합계
				
				resultOrderList.put("priceConsignSum", priceConsignSum);
				
				resultOrderList.put("settlFinalPrice", settlFinalPrice); 											// 총 정산금액(순수익)
				resultOrderList.put("zimcarFee", zimcar_fee); 														// 찜카 마진
				
				resultOrderList.put("corpBankNum",agoOrderList.get(i).get("bankNum"));								// 업체 입금계좌번호
				resultOrderList.put("corpBankName",agoOrderList.get(i).get("bankName"));							// 업체 은행명
				resultOrderList.put("corpBankCode",agoOrderList.get(i).get("bankCode"));							// 은행코드
				resultOrderList.put("corpBankHolder",agoOrderList.get(i).get("bankHolder"));						// 업체 예금주
				resultOrderList.put("rateSettlRentcar", agoOrderList.get(i).get("rateSettlRentcar"));				// 대여료 정산율
				resultOrderList.put("rateSettlExtraCharge", agoOrderList.get(i).get("rateSettlExtraCharge"));		// 할증금 정산율
				resultOrderList.put("rateCancelInRent24",agoOrderList.get(i).get("rateCancelInRent24"));			// 대여 24시간 전 수수료율
				resultOrderList.put("rateBankruptcyBooking",90);													// 예약부도 (사용안함)
				resultOrderList.put("settlStatus","00");															// 상태
				resultOrderList.put("settlMemPid",0000);

				resultOrderList.put("settleFinishSDate",map.get("settlSdate"));
				resultOrderList.put("settleFinishEDate",map.get("settlEdate"));
				
				resultOrderList.put("setttleDateCheck", map.get("setttleDateCheck"));
				resultOrderList.put("setttleDate", map.get("setttleDate"));

				resultOrderList.put("isDirect", agoOrderList.get(i).get("isDirect"));								// 다이렉트 여부(Y/N)
				
				resultOrderList.put("taxSumSettlFinalPrice", taxSumSettlFinalPrice);								//부사세포함정산금액

				resultOrderList.put("taxSettlFinalPrice", taxSettlFinalPrice);										//부사세
				
				settleOrderList.add(resultOrderList);
				
			}
			//최종 결과 넘겨주기
			resultMap.put("settleTotal", agoOrderListMap.get("total"));
			resultMap.put("settleOrderList", settleOrderList);
		}

		return resultMap;
	}

	//정산 대기 상세 현황
	public Map<String, Object> getSettlePriceWaitDetailInfo(Map<String, Object> map) throws Exception {
		GregorianCalendar  cal = new GregorianCalendar();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String today = sdf.format(date);
		//15일이 지났는지 확인
		String agoDate = String.valueOf(cal.get(Calendar.YEAR));
		String toMonth ="";
		//String agoMonth = "";
		if((cal.get(Calendar.MONTH)+1)<10) {
			//이번달
			toMonth += "0"+String.valueOf(cal.get(Calendar.MONTH)+1);
			//저번달
			//agoMonth += "0"+String.valueOf(cal.get(Calendar.MONTH));
		}else {
			toMonth += String.valueOf(cal.get(Calendar.MONTH)+1);
			//agoMonth += "0"+String.valueOf(cal.get(Calendar.MONTH));
		}
		String agoOnehalfStartDay ="01000000";
		String agoOnehalfLastDay = "15235959";		
		//날짜비교 기준점
		//1일기준날짜
		String totOnehalfStartDate = agoDate+toMonth+agoOnehalfStartDay;
		//15일기준날짜
		String totOnehalfLastDate = agoDate+toMonth+agoOnehalfLastDay;
		
		String agoTwohalfStartDay ="16000000";
		String agoTwoalfThisLastDay = String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH))+"235959";

		//16일 기준 이번달꺼
		String totTwoHalfThisStartDay = agoDate+toMonth+agoTwohalfStartDay;
		//말일
		@SuppressWarnings("unused")
		String totTwoHalfThisLastDay = agoDate+toMonth+agoTwoalfThisLastDay;
		//날짜 비교
		int dateCompare = totOnehalfLastDate.compareTo(today);
		Map<String, Object> agoOrderListMap = new HashMap<>();
		if(dateCompare >= 0) {
			map.put("settlSdate", totOnehalfStartDate);
			map.put("today", today);
			agoOrderListMap = providerCompanyDao.getSettlePriceWaitDetailInfo(map);
		}
		else {
			map.put("settlSdate", totTwoHalfThisStartDay);
			map.put("today", today);
		
			agoOrderListMap = providerCompanyDao.getSettlePriceWaitDetailInfo(map);
		}

		return agoOrderListMap;
	}
}