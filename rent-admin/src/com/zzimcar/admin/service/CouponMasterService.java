package com.zzimcar.admin.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.config.ZzimcarConstants;
import com.zzimcar.admin.controller.NmCarModelController;
import com.zzimcar.admin.dao.CouponMasterDao;
import com.zzimcar.admin.dao.NmCarModelDao;
import com.zzimcar.admin.dao.PromotionDao;
import com.zzimcar.admin.utils.PropConst;

import com.zzimcar.admin.utils.Sha256Encoder;;


/**
 * 2018.06.07 문득수 NmCar차종관리
 * 
 * @author BCOM
 *
 */

@Service
public class CouponMasterService extends ZzimcarService {

	@Autowired
	private CouponMasterDao dao;
	
	
	private static Logger logger = LoggerFactory.getLogger(CouponMasterService.class);
	
	public Map<String, Object> getCouponMasterAll(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		return dao.getCouponMasterAll(map);

	}

	public String insertCouponMasterTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		String result = "";
		try {
			dao.insertCouponMaster(map);
			String createTotalCountStr = (String) map.get("createTotalCount");
			int createTotalCount = Integer.parseInt(createTotalCountStr);
			
			String couponType = (String) map.get("couponType");
			if( couponType.equals("11") ) {	// 인터넷 발급 쿠폰의 경우 PIN 은 하나만 생성하도록 한다.
				createTotalCount = 1;
			}
			for(int i =0; i<createTotalCount;i++) {
			String	CouponKey = RandomCouponKey();
			//int pinNumber = Integer.parseInt(CouponKey);
				map.put("couponNumber", CouponKey);
				dao.insertcouponPin(map);
				
			}
			result = "Y";
		}
		catch (Exception e) {
			result = "N";
			e.printStackTrace();
			// TODO: handle exception
		}
		

		return result;
	}

	public List<Map<String, Object>> getCouponMasterView(Map<String, Object> map)throws Exception {

		// TODO Auto-generated method stub
		return dao.getCouponMasterView(map);
	}

	public String updateCouponMasterTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		String result = "";
		try {
			
			dao.updateCouponMaster(map);
			result = "Y";
		}
		catch (Exception e) {
			result = "N";
			e.printStackTrace();
			// TODO: handle exception
		}

		return result;
	}
	public Map<String,Object> deleteCouponMasterTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		Map<String, Object> resultMap= new HashMap<>();
		try {
		dao.deleteCouponMaster(map);
		resultMap.put("result", "Y");
		resultMap.put("resultMsg", PropConst.NOMAR_DELETE_SUCCESS);
		}
		catch (Exception e) {
			resultMap.put("result", "N");
			resultMap.put("resultMsg", PropConst.NOMAR_DELETE_FAIL);
			e.printStackTrace();
		}

		return resultMap;
	}

	public Map<String, Object> deleteCouponPinTx(Map<String, Object> map) {
		// TODO Auto-generated method stub

		Map<String, Object> resultMap= new HashMap<>();
		try {
		dao.deleteCouponPin(map);
		resultMap.put("result", "Y");
		resultMap.put("resultMsg", PropConst.NOMAR_DELETE_SUCCESS);
		}
		catch (Exception e) {
			resultMap.put("result", "N");
			resultMap.put("resultMsg", PropConst.NOMAR_DELETE_FAIL);
			e.printStackTrace();
		}

		return resultMap;
		
	}

	public List<Map<String, Object>> getCouponPin(Map<String, Object> map) {
		// TODO Auto-generated method stub
			
		return dao.getCouponPin(map);
	}
	
	public String RandomCouponKey() {
		String keyMatch ="";
		Random rnd =new Random();
		String RandomKey;
		StringBuffer buf =new StringBuffer();
		StringBuffer buf16 =new StringBuffer();
		for(int i=0;i<15;i++){
				/*1.2로시작
				  2.2번째 자리부터 15째 자리까지 랜덤
				  3.16번째 자리 Check Digit = (홀수 자리 숫자*2) + 짝수 자리 숫자의 합 / 10 = 나머지	
				*/

		       if(i==0) {
		    	   buf16.append("2,");
		       }
		       else {
		        buf16.append((rnd.nextInt(10))+",");	
		       }
		}
		//16번째 자리 구하기
		String buf16Str = buf16.toString();
		String bufArray [] =  buf16Str.split(",");
		int EvenNumSum = 0;
		int OddnumSum = 0;
		for(int i =0; i<bufArray.length; i++) {
			int remain = 1+i;
			if(remain % 2 == 0) {
				int EvenNum = Integer.parseInt(bufArray[i]);
				EvenNumSum = EvenNumSum+EvenNum;
			}
			else {
				int Oddnum = Integer.parseInt(bufArray[i]);
				OddnumSum = OddnumSum+Oddnum;
			}
		}
		buf16Str = buf16Str.replace(",", "");
		buf.append(buf16Str);
		String lastNum = Integer.toString((OddnumSum+(EvenNumSum*2))%10);
		buf.append(lastNum);
		RandomKey= buf.toString();


		return RandomKey;
	}


}
