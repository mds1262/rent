package com.zzimcar.admin.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collector;
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
import com.zzimcar.admin.dao.NmCarModelDao;
import com.zzimcar.admin.dao.PromotionDao;
import com.zzimcar.admin.dao.RentcarCarDao;
import com.zzimcar.admin.utils.PropConst;


/**
 * 2018.06.07 문득수 NmCar차종관리
 * 
 * @author BCOM
 *
 */

@Service
public class RentcarCarService extends ZzimcarService {

	@Autowired
	private RentcarCarDao rentcarCardao;
	
	private static Logger logger = LoggerFactory.getLogger(RentcarCarService.class);
	
	public Map<String, Object> getRentcarCar(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
				
		return rentcarCardao.getRentcarCar(map);

	}

	public String insertRentcarCarTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		String result = "";
		try {
			
			rentcarCardao.insertRentcarCar(map);
			result = "Y";
		}
		catch (Exception e) {
			result = "N";
			e.printStackTrace();
			// TODO: handle exception
		}
		

		return result;
	}
	
	public Map<String, Object> getRentcarCarView(Map<String, Object> map)throws Exception {

		// TODO Auto-generated method stub
		return rentcarCardao.getRentCarcarView(map);
	}


	public String updateRentcarCarTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		String result = "";
		try {
			
			rentcarCardao.updateRentcarCar(map);
			result = "Y";
		}
		catch (Exception e) {
			result = "N";
			System.err.println(e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}

		return result;
	}

	public Map<String,Object> deleteRentcarCarTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		Map<String, Object> resultMap= new HashMap<>();
		try {
			rentcarCardao.deleteRentcarCar(map);
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


}
