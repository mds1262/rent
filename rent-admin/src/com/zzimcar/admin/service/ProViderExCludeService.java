package com.zzimcar.admin.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.controller.ProViderExCludeController;
import com.zzimcar.admin.dao.NmCarModelDao;
import com.zzimcar.admin.dao.ProViderExCludeDao;
import com.zzimcar.admin.utils.PropConst;


/**
 * 2018.06.07 문득수 NmCar차종관리
 * 
 * @author BCOM
 *
 */

@Service
public class ProViderExCludeService extends ZzimcarService {

	@Autowired
	private ProViderExCludeDao dao;

	private static Logger logger = LoggerFactory.getLogger(ProViderExCludeService.class);

	
	public Map<String, Object> getPVExculdeAll(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		return dao.getPVExculdeAll(map);

	}

	public String insertProExcludeTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		String result = "";
		try {
			
			dao.insertProExclude(map);
			result = "Y";
		}
		catch (Exception e) {
			result = "N";
			e.printStackTrace();
			// TODO: handle exception
		}
		return result;
	}

	public Map<String, Object> getProExcludeView(Map<String, Object> map, HttpServletResponse response)throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = new HashMap<>();
		try {
			
			List<Map<String, Object>> ProExcludeView =	dao.getProExcludeView(map);
			resultMap.put("excludeView", ProExcludeView.get(0));
		} catch (Exception e) {
			
			e.printStackTrace();
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('정보를 가져오는 중 문제가 발생됬습니다'); history.go(-1);</script>");
            out.flush();
		}
		
		return resultMap;
	}
	

	public Map<String, Object> getRcModelNameModelGroup(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = new HashMap<>();
			try {
			List<Map<String, Object>> rcCopModelList = dao.getRcModelNameModelGroup(map);	
				resultMap.put("rcCopModel", rcCopModelList);
				resultMap.put("rcCopModelSize", rcCopModelList.size());
				
			} catch (Exception e) {
				// TODO: handle exception
				resultMap.put("rcCopModel", "N");
				e.printStackTrace();
			}
		
		return resultMap;
	}

	public List<Map<String, Object>> getSubExcludeModelPid(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		
		return dao.getSubExcludeModelPid(map);
	}

	public String updateProExcludeTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		String result = "";
		try {
			
			dao.updateProExclude(map);
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

	public Map<String,Object> deleteNmModel(Map<String, Object> map)throws Exception {
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


}
