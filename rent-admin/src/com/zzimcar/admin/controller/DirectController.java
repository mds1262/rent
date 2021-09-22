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
import com.zzimcar.admin.config.ZzimcarConstants.AREA_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.DIRECT_SEARCH_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.service.CodeService;
import com.zzimcar.admin.service.DirectSkinService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.PropConst;
import com.zzimcar.admin.utils.RequestParameterMapUtils;

@Controller
@RequestMapping("/direct")
public class DirectController extends ZzimcarController {
	@Resource(name="DirectSkinService")
	private DirectSkinService directSkinService;
	
	@Resource(name="CodeService")
	private CodeService codeservice;
	
	@RequestMapping(value = "/list_skin.do")
	public ModelAndView listProvider(HttpServletRequest request, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(VIEW_ROOT_PC + "/direct/list_skin");

		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			String cPage = ParamUtils.getString(param, "pages", "1");
			
			if(param.get("scType") == null) {
				param.put("scType", DIRECT_SEARCH_CODE.CORP_NAME.getCode());
			}
			
			int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);

			param.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
			param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
			
			Map<String, Object> list = directSkinService.getSkinList(param);
			
			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);
			paging.setNumberOfRecords((int) list.get(PAGING.RES_KEY_TOTAL_COUNT));
			paging.pagingUtil(cPage);
			
			mav.addObject("list", list.get(PAGING.RES_KEY_ROWS));
			mav.addObject("total", list.get(PAGING.RES_KEY_TOTAL_COUNT));
			mav.addObject("paging", paging);
			mav.addObject("params", param);
			
			List<Map<String, Object>> scTypeArr = new ArrayList<>();
	        for (DIRECT_SEARCH_CODE code : DIRECT_SEARCH_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("value", code.getCode());
	            map.put("name", code.getKorean());
	            scTypeArr.add(map);
	        }
	        mav.addObject("scTypeArr", scTypeArr);
	        
	        List<Map<String, Object>> select_area_code_list = new ArrayList<>();
			for(AREA_CODE code : AREA_CODE.values()) {
				Map<String, Object> map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				select_area_code_list.add(map);
			}
			mav.addObject("selectAreaCodeList", select_area_code_list);
	        
	        List<Map<String, Object>> SkinArr = codeservice.getSkinTypeCodeGroup();
	        mav.addObject("skinArr", SkinArr);
	        
	        List<Map<String, Object>> LayoutArr = codeservice.getLayoutTypeCodeGroup();
	        mav.addObject("layoutArr", LayoutArr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mav;
	}
	
	@RequestMapping(value = "/write_skin.do")
	public ModelAndView writeProvider(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(VIEW_ROOT_PC + "/direct/write_skin");

		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			int index = ParamUtils.getIntValue(param, "selected_id", 0);
			if (index <= 0) {
				mav.setViewName("redirect:/");
				return mav;
			}
			
			Map<String, Object> layout_item = directSkinService.selectByPk(index);
			mav.addObject("item", layout_item);

			String scType = ParamUtils.getString(param, "scType", "");
			mav.addObject("scType", scType);

			String scValue = ParamUtils.getString(param, "scValue", "");
			mav.addObject("scValue", scValue);

			int pages = ParamUtils.getIntValue(param, "pages", 1);
			mav.addObject("pages", pages);
			
			String areacode_view = ParamUtils.getString(param, "areacode_view", "");
			mav.addObject("areacode_view", areacode_view);			
			
			List<Map<String, Object>> SkinArr = codeservice.getSkinTypeCodeGroup();
			mav.addObject("SkinArr", SkinArr);
			
			List<Map<String, Object>> LayoutArr = codeservice.getLayoutTypeCodeGroup();
			mav.addObject("LayoutArr", LayoutArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav;
	}
	
	@ResponseBody
	@RequestMapping(value="/rentGuideUpload.do", method=RequestMethod.POST)
	public void rentGuideUpload(MultipartHttpServletRequest request,HttpServletResponse response)throws Exception {
		response.setContentType("text/plain; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> fileresult = new HashMap<>();
		String result ="";
		// 파일업로드
		String fileName = request.getFiles("imgList").get(0).getOriginalFilename();
		if (fileName != null && !(fileName.equals(""))) {
			Map<String, Object> location = directSkinService.fileupload(request, "direct");
			fileresult.put("location", String.valueOf(location.get("filepath0")));
			result =  gson.toJson(fileresult);
		}
		
		print.append(result);
	}
	
	@ResponseBody
	@RequestMapping(value="/directSkinImgUpload.do", method=RequestMethod.POST)
	public void directSkinImgUpload(MultipartHttpServletRequest request,HttpServletResponse response)throws Exception {
		response.setContentType("text/plain; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> fileresult = null;
		String result ="";
		// 파일업로드
		String fileName = request.getFiles("imgList").get(0).getOriginalFilename();
		if (fileName != null && !(fileName.equals(""))) {
			fileresult = directSkinService.fileupload(request, "rcmodel");
			result =  gson.toJson(fileresult);
		}
		else {
			result = gson.toJson("N");
		}
		print.append(result);

	}
	
	@RequestMapping(value="/save_skin.do")
	public @ResponseBody Map<String, Object> saveExtraCharge(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		System.out.println( "param =========== " + param.toString() );
		Map<String, Object> result = new HashMap<String, Object>();
		String retMsg = new String();
		
		param.put("main_title", ParamUtils.getString(param, "main_title", null));
		param.put("sub_title", ParamUtils.getString(param, "sub_title", null));
		param.put("naver_site_verification", ParamUtils.getString(param, "naver_site_verification", null));
		param.put("google_site_verification", ParamUtils.getString(param, "google_site_verification", null));
		
		if(param.get("layoutPid")!=null && !param.get("layoutPid").equals("")) {
			param.put("reg_mem_pid", session.getAttribute("mem_pid"));
			try {
				directSkinService.update(param);
				result.put("result", true);
				retMsg = PropConst.WRITE_SUCCESS_UPDATE;
			} catch (Exception e) {
				result.put("result", false);
				retMsg = PropConst.WRITE_FAIL_UPDATE;
			}
		} else {
			result.put("result", false);
			retMsg = PropConst.WRITE_FAIL_UPDATE;
		}
		result.put("msg", retMsg);
        return result;
	}
}