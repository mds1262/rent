package com.zzimcar.admin.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.config.ZzimcarConstants.SERVICE_CENTER_BBS_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.SERVICE_CENTER_CODE;
import com.zzimcar.admin.service.ServiceCenterService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.PropConst;
import com.zzimcar.admin.utils.RequestParameterMapUtils;

@Controller
@RequestMapping("/servicecenter")
public class ServiceCenterController extends ZzimcarController {
	@Resource(name = "ServiceCenterService")
	ServiceCenterService serviceCenterService;
	
	@RequestMapping(value = "/list.do")
	public ModelAndView list(HttpServletRequest request, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(VIEW_ROOT_PC + "/servicecenter/list");

		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			String cPage = ParamUtils.getString(param, "pages", "1");

			int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);
			
			String bbsCode = (param.get("bbsCode") == null) ? "000" : param.get("bbsCode").toString();
			param.put("bbsCode", bbsCode);
			mav.addObject("bbsCode", bbsCode);
			

			Map<String, Object> map = new HashMap<>();
			
			List<Map<String, Object>> selectBox_bbsCode = new ArrayList<>();
			List<String> bbsCodeArr = new ArrayList<>();
			for (SERVICE_CENTER_BBS_CODE code : SERVICE_CENTER_BBS_CODE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				selectBox_bbsCode.add(map);
				
				bbsCodeArr.add(code.getCode());
			}
			
			mav.addObject("bbsCodeArr", selectBox_bbsCode);
			param.put("bbsCodeArr", bbsCodeArr);
			
			param.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
			param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);

			Map<String, Object> list = serviceCenterService.getList(param);

			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);
			paging.setNumberOfRecords((int) list.get(PAGING.RES_KEY_TOTAL_COUNT));
			paging.pagingUtil(cPage);
			
			
			List<Map<String, Object>> selectBox_ScType = new ArrayList<>();
			for (SERVICE_CENTER_CODE code : SERVICE_CENTER_CODE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				selectBox_ScType.add(map);
			}
			mav.addObject("scTypeArr", selectBox_ScType);
			
			mav.addObject("list", list.get(PAGING.RES_KEY_ROWS));
			mav.addObject("total", list.get(PAGING.RES_KEY_TOTAL_COUNT));
			mav.addObject("paging", paging);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mav;
	}
	
	@RequestMapping(value = "/write.do")
	public ModelAndView write(HttpServletRequest request, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(VIEW_ROOT_PC + "/servicecenter/write");

		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			int index = ParamUtils.getIntValue(param, "selected_id", 0);
			if (index != 0) {
				Map<String, Object> board = serviceCenterService.selectByPk(index);
				mav.addObject("board", board);
			}
			
			String scType = ParamUtils.getString(param, "scType", "");
			mav.addObject("scType", scType);

			String scValue = ParamUtils.getString(param, "scValue", "");
			mav.addObject("scValue", scValue);

			int pages = ParamUtils.getIntValue(param, "pages", 1);
			mav.addObject("pages", pages);
			
			String bbsCode = ParamUtils.getString(param, "bbsCode", null);
			mav.addObject("bbsCode", bbsCode);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mav;
	}
	
	@RequestMapping(value = "/delete.do")
	public @ResponseBody Map<String, Object> deleteProvider(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Map<String, Object> result = new HashMap<String, Object>();
		String resultMsg = "";

		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			param.put("mod_mem_pid", session.getAttribute("mem_pid"));
			resultMsg = PropConst.PROVIDER_DELETE_SUCCESS;
			serviceCenterService.deleteBoardTx(param);
			result.put("result", true);
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = PropConst.PROVIDER_DELETE_FAILED;
			result.put("result", false);
		}

		result.put("msg", resultMsg);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/fileUpload.do", method=RequestMethod.POST)
	public void RcModelfileUpload(MultipartHttpServletRequest request,HttpServletResponse response)throws Exception {
		response.setContentType("text/plain; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> fileresult = null;
		String result ="";
		// 파일업로드
		String fileName = request.getFiles("imgList").get(0).getOriginalFilename();
		if (fileName != null && !(fileName.equals(""))) {
			fileresult = serviceCenterService.fileupload(request, "serviceCenter");
			result =  gson.toJson(fileresult);
		}
		else {
			result = gson.toJson("N");
		}
		print.append(result);

	}
	
	private String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
 
        System.out.println(">>>> X-FORWARDED-FOR : " + ip);
 
        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
            System.out.println(">>>> Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP"); // 웹로직
            System.out.println(">>>> WL-Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            System.out.println(">>>> HTTP_CLIENT_IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            System.out.println(">>>> HTTP_X_FORWARDED_FOR : " + ip);
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        
        if(ip.equals("0:0:0:0:0:0:0:1")) {
        	ip = "127.0.0.1";
        }
        
        System.out.println(">>>> Result : IP Address : "+ip);
 
        return ip;
    }

	@RequestMapping(value = "/save.do")
	public @ResponseBody Map<String, Object> saveProvider(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);

		String resultMsg = "";
		try {
			param.put("reg_mem_pid", session.getAttribute("mem_pid"));
			if (param.get("bbs_pid") == null || param.get("bbs_pid").toString().trim().equals("")) {
				
				param.put("bbs_remote_ip", getIp(request));
		        
				serviceCenterService.insertBoardItemTx(param);
				resultMsg = PropConst.WRITE_SUCCESS_INSERT;
			} else {
				serviceCenterService.updateBoardItemTx(param);
				resultMsg = PropConst.WRITE_SUCCESS_UPDATE;
			}

			result.put("result", true);
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = PropConst.WRITE_ERROR_OCCURED;
			result.put("result", false);
		}

		result.put("msg", resultMsg);
		return result;
	}
}
