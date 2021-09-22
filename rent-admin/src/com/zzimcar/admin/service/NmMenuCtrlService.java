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
import com.zzimcar.admin.dao.NmMenuCtrlDao;
import com.zzimcar.admin.dao.ProViderExCludeDao;
import com.zzimcar.admin.utils.PropConst;


/**
 * 2018.06.07 문득수 NmCar차종관리
 * 
 * @author BCOM
 *
 */

@Service
public class NmMenuCtrlService extends ZzimcarService {

	@Autowired
	private NmMenuCtrlDao dao;

	private static Logger logger = LoggerFactory.getLogger(NmMenuCtrlService.class);

	
	public Map<String, Object> getNmMenuCtrlList(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		return dao.getNmMenuCtrlList(map);

	}

	public String insertNmMenuCtrlTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		String result = "";
		try {
			
			dao.insertNmMenuCtrl(map);
			map.put("menuPid", map.get("menuPid"));
			List<Map<String, Object>> getSuperMasterPid = dao.getSuperMasterPid(map);
			if(getSuperMasterPid.size() >0) {
				for(Map<String, Object> PidMap : getSuperMasterPid) {
					map.put("roleMemPid", PidMap.get("memPid"));
					dao.insetRollMenu(map);
				}
			}
			result = "Y";
		}
		catch (Exception e) {
			result = "N";
			logger.error("ProViderExCludeService.insertProExcludeTx => 오류발생");
			System.err.println("ProViderExCludeService.insertProExcludeTx"+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return result;
	}

	public Map<String, Object> getNmMenuCtrlView(Map<String, Object> map, HttpServletResponse response)throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = new HashMap<>();
		try {
			
			List<Map<String, Object>> nmMenuCtrlView =	dao.getNmMenuCtrlView(map);
			resultMap.put("nmMenuCtrlView", nmMenuCtrlView.get(0));
		
		} catch (Exception e) {
			
			logger.error("ProViderExCludeService.getRcModelNameModelGroup => 오류발생");
			System.err.println("ProViderExCludeService.getRcModelNameModelGroup"+e.getMessage());
			e.printStackTrace();
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('정보를 가져오는 중 문제가 발생됬습니다'); history.go(-1);</script>");
            out.flush();
		}
		
		return resultMap;
	}
	public String updateNmMenuCtrlTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		String result = "";
		try {
			
			dao.updateNmMenuCtrl(map);
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

	public Map<String,Object> deleteNmMenuCtrlTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> resultMap= new HashMap<>();
		try {
		dao.deleteNmMenuCtrl(map);
		dao.deleteRoleMenu(map);
		resultMap.put("result", "Y");
		resultMap.put("resultMsg", PropConst.NOMAR_DELETE_SUCCESS);
		}
		catch (Exception e) {
			resultMap.put("result", "N");
			resultMap.put("resultMsg", PropConst.NOMAR_DELETE_FAIL);
			System.err.println("ProViderExCludeService.deleteNmModel()"+e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}


}
