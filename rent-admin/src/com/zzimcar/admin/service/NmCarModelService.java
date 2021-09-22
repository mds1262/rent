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
import com.zzimcar.admin.erp.vo.NmCarList;
import com.zzimcar.admin.utils.PropConst;


/**
 * 2018.06.07 문득수 NmCar차종관리
 * 
 * @author BCOM
 *
 */

@Service
public class NmCarModelService extends ZzimcarService {

	@Autowired
	private NmCarModelDao dao;
	
	private static Logger logger = LoggerFactory.getLogger(NmCarModelService.class);
	
	public Map<String, Object> getNmCarModelAll(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
				
		return dao.getNmCarModelAll(map);

	}

	public String insertCarModelTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		String result = "";
		try {
			
			dao.insertCarModel(map);
			result = "Y";
		}
		catch (Exception e) {
			result = "N";
			e.printStackTrace();
			// TODO: handle exception
		}
		

		return result;
	}

	public List<Map<String, Object>> getCarModelView(Map<String, Object> map)throws Exception {

		// TODO Auto-generated method stub
		return dao.getCarModelView(map);
	}

	public String updateCarModelTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		String result = "";
		try {
			
			dao.updateCarModel(map);
			result = "Y";
			if(result.equals("Y")) {	
				//rentcar_model_master 수정	
					dao.updateRentCarModelMasterCode(map);
					dao.updateErpSyscCode(map);
				}	

		}
		catch (Exception e) {
			result = "N";
			e.printStackTrace();
			// TODO: handle exception
		}

		return result;
	}
	public Map<String,Object> deleteNmModelTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		Map<String, Object> resultMap= new HashMap<>();
		try {
		dao.deleteNmModel(map);
		
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

	public String modelCodeCheck(Map<String, Object> map)throws Exception {
		String result = "";
		
		List<Map<String, Object>> modelCodeCheck = dao.modelCodeCheck(map);
		
		if(modelCodeCheck.size()>0) {
			result = "N";
			String momelPid = (String) map.get("nmmodelPid");
			if(momelPid != null) {
				int intModelPid = Integer.parseInt(momelPid);
				int nmModelPid = (Integer)modelCodeCheck.get(0).get("nmmodelPid");
				if(intModelPid == nmModelPid) {
					result = "Y";
				}
			}
			
		}
		else {

			result = "Y";
		}
		return result;
	}
	
	public List<NmCarList> getNmCarLists()throws Exception {
		return dao.getNmCarLists();
	}
}
