package com.zzimcar.admin.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.controller.ProViderExCludeController;
import com.zzimcar.admin.dao.MenuauthorityDao;
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
public class MenuauthorityService extends ZzimcarService {

	@Autowired
	private MenuauthorityDao dao;
	
	@Autowired
	private NmMenuCtrlDao menuDao;

	private static Logger logger = LoggerFactory.getLogger(MenuauthorityService.class);

	
	public Map<String, Object> getAuthorityMember(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		return dao.getAuthorityMember(map);

	}

	public String insertmenuAuthorityTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		String result = "";
		try {
			dao.insertmenuAuthority(map);
			result = "Y";
		}
		catch (Exception e) {
			result = "N";
			e.printStackTrace();
			// TODO: handle exception
		}
		return result;
	}

	public Map<String, Object> getMenuAuthorityView(Map<String, Object> map, HttpServletResponse response)throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = new HashMap<>();
		try {
			//태그용 String
			String tag = "";
			//확인용
			boolean check = false; 
			
			//아이디
			List<Map<String, Object>> MemberAuthorityView =	dao.getMemberAuthorityView(map);
			//권한 등록 메뉴
			List<Map<String, Object>> MenuAuthorityView =	dao.getMenuAuthorityView(map);
			//메뉴 리스트
			Map<String, Object>	getMenuList = menuDao.getNmMenuCtrlList(map);
			
			List<Map<String, Object>> menuList = (List<Map<String, Object>>) getMenuList.get("rows");
			for(Map<String, Object> menuMap : menuList) {
				check = false;
				for(Map<String, Object> roleMap : MenuAuthorityView) {
				int intMenuPid =(Integer)menuMap.get("menuPid");
				String memPid = Integer.toString(intMenuPid);
					if(memPid.equals((String)roleMap.get("menuPid"))) {
						tag = tag+"<p class=\"check-box mg20\" >";
						tag = tag+"<input type='checkbox' id='myCheck_"+menuMap.get("menuPid")+"' class='menuPid' value='"+menuMap.get("menuPid")+"'checked/>";
						tag = tag+"<label for ='myCheck_"+menuMap.get("menuPid")+"' >"+menuMap.get("menuName")+"</label>";		
						tag = tag+"</p><br/>";
						check = true;
					}
					
				}
				if(!check) {
					tag = tag+"<p class=\"check-box mg20\" >";
					tag = tag+"<input type='checkbox' id='myCheck_"+menuMap.get("menuPid")+"' class='menuPid' value='"+menuMap.get("menuPid")+"' />";
					tag = tag+"<label for ='myCheck_"+menuMap.get("menuPid")+"' >"+menuMap.get("menuName")+"</label>";		
					tag = tag+"</p><br/>";
				}
				
			}
			resultMap.put("checkTag", tag);
			resultMap.put("MemberAuthorityView", MemberAuthorityView.get(0));
			
					
		
		} catch (Exception e) {
			
			e.printStackTrace();
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('정보를 가져오는 중 문제가 발생됬습니다'); history.go(-1);</script>");
            out.flush();
		}
		
		return resultMap;
	}
	public String updatemenuAuthorityTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		String result = "";

		try {
			List<Map<String, Object>> getCheckRole = dao.getCheckRoleMenu(map);
			if(getCheckRole.size() == 0) {
				String dataEmpty = (String) map.get("DataEmpty");
				if(dataEmpty.equals("Y")) {
					dao.insertmenuAuthority(map);
				}
				else if(dataEmpty.equals("N")) {
					map.put("useChange", 1);	
					dao.updateRoleUseChange(map);
				}

			}
			else {
				dao.updateRoleUseChange(map);	
			}
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

	public Map<String,Object> deletemenuAuthorityTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> resultMap= new HashMap<>();
		try {
		dao.deleteMemberAuthority(map);
		dao.deletemenuAuthority(map);
		resultMap.put("result", "Y");
		}
		catch (Exception e) {
			resultMap.put("result", "N");
			e.printStackTrace();
		}
		return resultMap;
	}

	public Map<String, Object> updateMemberAuthorityTx(Map<String, Object> map) {
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = new HashMap<>();
		try {
		dao.updateMemberAuthority(map);
		resultMap.put("result", "Y");
		}
		catch (Exception e) {
			resultMap.put("result", "N");
			e.printStackTrace();
		}
	return 	resultMap;
	}

	public Map<String, Object> updateMemberDeleteAuthorityTx(Map<String, Object> map) {
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = new HashMap<>();
		try {
		map.put("delAuth", "OK");
		dao.deleteAuthority(map);
		dao.updateMemberAuthority(map);
		resultMap.put("result", "Y");
		resultMap.put("resultMsg",PropConst.AUTHORITYDELETE_SUCCESS);
		}
		catch (Exception e) {
			resultMap.put("result", "N");
			resultMap.put("resultMsg",PropConst.AUTHORITYDELETE_FAIL);
			e.printStackTrace();
		}
	return 	resultMap;
	}

	public Map<String, Object> deleteAuthorityTx(Map<String, Object> map) {
		// TODO Auto-generated method stub
		Map<String, Object> resultMap = new HashMap<>();
		try {
		dao.deleteAuthority(map);
		resultMap.put("result", "Y");
		}
		catch (Exception e) {
			resultMap.put("result", "N");
			e.printStackTrace();
		}
	return 	resultMap;
		
	}

}
