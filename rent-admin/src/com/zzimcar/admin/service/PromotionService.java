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
import com.zzimcar.admin.utils.PropConst;


/**
 * 2018.06.07 문득수 NmCar차종관리
 * 
 * @author BCOM
 *
 */

@Service
public class PromotionService extends ZzimcarService {

	@Autowired
	private PromotionDao dao;
	
	private static Logger logger = LoggerFactory.getLogger(PromotionService.class);
	
	public Map<String, Object> getPromotionAll(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
				
		return dao.getPromotionAll(map);

	}

	public String insertPromotioTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		String result = "";
		try {
			
			dao.insertPromotion(map);
			result = "Y";
		}
		catch (Exception e) {
			logger.error("NmCarModelService.insertCarModelTx() => 오류");
			result = "N";
			System.err.println(e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		

		return result;
	}

	public List<Map<String, Object>> getPromotionView(Map<String, Object> map)throws Exception {

		// TODO Auto-generated method stub
		return dao.getPromotionView(map);
	}

	public String updatePromotionTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		String result = "";
		try {
			
			dao.updatePromotion(map);
			result = "Y";
		}
		catch (Exception e) {
			logger.error("NmCarModelService.updateCarModelTx() => 오류");
			result = "N";
			System.err.println(e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}

		return result;
	}
	public Map<String,Object> deletePromotionTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		Map<String, Object> resultMap= new HashMap<>();
		try {
		dao.deletePromotion(map);
		resultMap.put("result", "Y");
		resultMap.put("resultMsg", PropConst.NOMAR_DELETE_SUCCESS);
		}
		catch (Exception e) {
			logger.error("NmCarModelService.deleteNmModel() => 오류");
			resultMap.put("result", "N");
			resultMap.put("resultMsg", PropConst.NOMAR_DELETE_FAIL);
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		return resultMap;
	}

	public List<Map<String, Object>> getRcModelSearch(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return dao.getRcModelSearch(map);
	}

	public Map<String, Object> getReviewList(Map<String, Object> map) {
		// TODO Auto-generated method stub
			Map<String, Object> resultMap = new HashMap<>();
			List<Map<String, Object>> rcModelPidMapList = dao.getRcModelPid(map);
			List<String> rcmodelPidList =rcModelPidMapList.stream().map(i->i.get("rcmodelPid").toString()).collect(Collectors.toList());
			if(rcModelPidMapList.size() >0) {
				map.put("rcmodelPidList",rcmodelPidList );
				Map<String, Object> reviewList = dao.getReviewList(map);
				resultMap.put("reviewList", reviewList.get("rows"));
				resultMap.put("reviewListCnt", reviewList.get("total"));
				resultMap.put("result", "OK");
			}
			else {
				resultMap.put("result", "Empty");
			}
			return resultMap;
	}
}
