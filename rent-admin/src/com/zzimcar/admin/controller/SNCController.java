package com.zzimcar.admin.controller;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.service.SNCService;
import com.zzimcar.admin.utils.PropConst;
import com.zzimcar.admin.utils.RequestParameterMapUtils;
import com.zzimcar.admin.utils.HMapUtils;

@Controller
public class SNCController extends ZzimcarController{
	
	@Resource(name="SNCService")
	private SNCService sncService;
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 2018.09.06 김준태 공동구매 제조사 관리
	 */
	@RequestMapping(value="/snc/list_maker.do")
	public ModelAndView listMaker(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
        mav.setViewName(VIEW_ROOT_PC+"/snc/list_maker");
        
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
				
		try {
			Map<String, Object> items =sncService.search("select_maker_page", param);

			mav.addObject("list", items.get(PAGING.RES_KEY_ROWS));
			mav.addObject("params", param);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return mav;
	}
	
	@RequestMapping(value="/snc/save_maker.do")
	public @ResponseBody Map<String, Object> saveMaker(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			param.put("mem_pid", session.getAttribute("mem_pid"));
			sncService.saveMaker(param);
			int maker_pid = Integer.valueOf(param.get("maker_pid").toString());
			System.out.println("PID는="+maker_pid);
			result.put("chk", true);
			result.put("msg", PropConst.WRITE_SUCCESS_INSERT);
			result.put("num", maker_pid);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			result.put("chk", false);
			result.put("msg", PropConst.WRITE_ERROR_OCCURED);
		}
        return result;
	}
	
	@RequestMapping(value="/snc/delete_maker.do")
	public @ResponseBody Map<String, Object> deleteMaker(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			param.put("mem_pid", session.getAttribute("mem_pid"));
			sncService.deleteMaker(param);
			result.put("chk", true);
			result.put("msg", PropConst.NOMAR_DELETE_SUCCESS);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			result.put("chk", false);
			result.put("msg", PropConst.NOMAR_DELETE_FAIL);
		}
        return result;
	}
	
	/**
	 * 2018.09.06 김준태 공동구매 차종 등록 관리
	 */
	@RequestMapping(value="/snc/regi_sncModel.do")
	public ModelAndView listSncModel(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
        mav.setViewName(VIEW_ROOT_PC+"/snc/regi_sncModel");
        
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
				
		try {
			Map<String, Object> items =sncService.search("select_sncmodel_page", param);

			mav.addObject("list", items.get(PAGING.RES_KEY_ROWS));
			mav.addObject("params", param);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return mav;
	}
	
	@RequestMapping(value="/snc/save_sncModel.do")
	public @ResponseBody Map<String, Object> saveSncModel(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			param.put("mem_pid", session.getAttribute("mem_pid"));
			param.put("snc_car_name", param.get("carName"));
			param.put("snc_car_img_url", param.get("imgUrlList"));
			sncService.saveSncModel(param);
			int snc_car_pid = Integer.valueOf(param.get("snc_car_pid").toString());
			System.out.println("PID는="+snc_car_pid);
			result.put("chk", true);
			result.put("num", snc_car_pid);
			result.put("msg", PropConst.WRITE_SUCCESS_INSERT);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			result.put("chk", false);
			result.put("msg", PropConst.WRITE_ERROR_OCCURED);
		}
        return result;
	}
	
	@RequestMapping(value="/snc/delete_sncModel.do")
	public @ResponseBody Map<String, Object> deleteSncModel(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			param.put("mem_pid", session.getAttribute("mem_pid"));
			sncService.deleteSncModel(param);
			result.put("chk", true);
			result.put("msg", PropConst.NOMAR_DELETE_SUCCESS);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			result.put("chk", false);
			result.put("msg", PropConst.NOMAR_DELETE_FAIL);
		}
        return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/snc/fileUpload.do", method=RequestMethod.POST)
	public void fileUpload(MultipartHttpServletRequest request,HttpServletResponse response)throws Exception {
		logger.info("NmCarModelController.fileUpload() => 인입");	
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> fileresult = null;
		String result ="";
		// 파일업로드
		String fileName = request.getFiles("imgList").get(0).getOriginalFilename();
		if (fileName != null && !(fileName.equals(""))) {
			fileresult = sncService.fileupload(request, "sncModel");
			result =  gson.toJson(fileresult);
		}
		else {
			result = gson.toJson("N");
		}
		print.append(result);
		logger.info("NmCarModelController.fileUpload() => 종료");	
	}
	
	
	/**
	 * 2018.09.06 김준태 공동구매 트림 등록 관리
	 */
	@RequestMapping(value="/snc/regi_sncTrim.do")
	public ModelAndView listSncTrim(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
        mav.setViewName(VIEW_ROOT_PC+"/snc/regi_sncTrim");
        
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
				
		try {
			Map<String, Object> items =sncService.search("select_sncTrim_page", param);

			mav.addObject("list", items.get(PAGING.RES_KEY_ROWS));
			mav.addObject("params", param);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return mav;
	}
	
	@RequestMapping(value="/snc/save_sncTrim.do")
	public @ResponseBody Map<String, Object> saveSncTrim(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			param.put("mem_pid", session.getAttribute("mem_pid"));
			sncService.saveSncTrim(param);
			int snc_trim_pid = Integer.valueOf(param.get("snc_trim_pid").toString());
			System.out.println("PID는="+snc_trim_pid);
			result.put("chk", true);
			result.put("num", snc_trim_pid);
			result.put("msg", PropConst.WRITE_SUCCESS_INSERT);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			result.put("chk", false);
			result.put("msg", PropConst.WRITE_ERROR_OCCURED);
		}
        return result;
	}
	
	@RequestMapping(value="/snc/delete_sncTrim.do")
	public @ResponseBody Map<String, Object> deleteSncTrim(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			param.put("mem_pid", session.getAttribute("mem_pid"));
			sncService.deleteSncTrim(param);
			result.put("chk", true);
			result.put("msg", PropConst.NOMAR_DELETE_SUCCESS);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			result.put("chk", false);
			result.put("msg", PropConst.NOMAR_DELETE_FAIL);
		}
        return result;
	}
	
	/**
	 * 2018.09.06 김준태 공동구매 컬러,옵션 등록 관리
	 */
	@RequestMapping(value="/snc/regi_sncColor.do")
	public ModelAndView listSncColor(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
        mav.setViewName(VIEW_ROOT_PC+"/snc/regi_sncColor");
        
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
				
		try {
			Map<String, Object> items =sncService.search("select_sncColor_page", param);

			mav.addObject("list", items.get(PAGING.RES_KEY_ROWS));
			mav.addObject("params", param);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return mav;
	}
	@RequestMapping(value="/snc/regi_sncOption.do")
	public ModelAndView listSncOption(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
        mav.setViewName(VIEW_ROOT_PC+"/snc/regi_sncOption");
        
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
				
		try {
			Map<String, Object> items =sncService.search("select_sncOption_page", param);

			mav.addObject("list", items.get(PAGING.RES_KEY_ROWS));
			mav.addObject("params", param);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return mav;
	}
	
	@RequestMapping(value="/snc/save_sncOption.do")
	public @ResponseBody Map<String, Object> saveSncOption(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			param.put("mem_pid", session.getAttribute("mem_pid"));
			sncService.saveSncOption(param);
			int snc_trim_pid = Integer.valueOf(param.get("snc_option_pid").toString());
			System.out.println("PID는="+snc_trim_pid);
			result.put("chk", true);
			result.put("code", param.get("snc_option_code"));
			result.put("num", param.get(""));
			result.put("msg", PropConst.WRITE_SUCCESS_INSERT);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			result.put("chk", false);
			result.put("msg", PropConst.WRITE_ERROR_OCCURED);
		}
        return result;
	}
	
	@RequestMapping(value="/snc/delete_sncOption.do")
	public @ResponseBody Map<String, Object> deleteSncOption(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			param.put("mem_pid", session.getAttribute("mem_pid"));
			sncService.deleteSncOption(param);
			result.put("chk", true);
			result.put("msg", PropConst.NOMAR_DELETE_SUCCESS);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			result.put("chk", false);
			result.put("msg", PropConst.NOMAR_DELETE_FAIL);
		}
        return result;
	}
	
	/**
	 * SNC 외장 정보  
	 * POST /snc/external_info.json
	 */
	@RequestMapping(value="/snc/external_info.json")
	public @ResponseBody Map<String, Object> externalInfo (
			HttpServletRequest request, HttpServletResponse response, HttpSession session){

		Map<String, Object> externalitem = null;
		Map<String, Object> resJson = new HashMap<String, Object>();

		// Get Parameter 
		Map<String, Object> params = RequestParameterMapUtils.initParamMap(request);

		try {
			System.out.println("#### params : " + params);
			// Service module
			externalitem = sncService.search("select_external_by_pk", params);
			if( externalitem == null ) return resultJsonError();

			
			System.out.println( "#### externalItem  = " + externalitem.get(PAGING.RES_KEY_ROWS) );
			resJson.put("item", externalitem.get(PAGING.RES_KEY_ROWS));

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return resultJsonSuc(resJson);
	}
	
	/**
	 * SNC 내장 정보  
	 * POST /snc/interior_info.json
	 */
	@RequestMapping(value="/snc/interior_info.json")
	public @ResponseBody Map<String, Object> interiorInfo (
			HttpServletRequest request, HttpServletResponse response, HttpSession session){

		Map<String, Object> interioritem = null;
		Map<String, Object> resJson = new HashMap<String, Object>();

		// Get Parameter 
		Map<String, Object> params = RequestParameterMapUtils.initParamMap(request);

		try {
			System.out.println("#### params : " + params);
			// Service module
			interioritem = sncService.search("select_interior_by_pk", params);
			if( interioritem == null ) return resultJsonError();

			
			System.out.println( "#### interiorItem  = " + interioritem.get(PAGING.RES_KEY_ROWS) );
			resJson.put("item", interioritem.get(PAGING.RES_KEY_ROWS));

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return resultJsonSuc(resJson);
	}
	
	/**
	 * SNC 옵션 정보  
	 * POST /snc/option_info.json
	 */
	@RequestMapping(value="/snc/option_info.json")
	public @ResponseBody Map<String, Object> optionInfo (
			HttpServletRequest request, HttpServletResponse response, HttpSession session){

		Map<String, Object> optionitem = null;
		Map<String, Object> resJson = new HashMap<String, Object>();

		// Get Parameter 
		Map<String, Object> params = RequestParameterMapUtils.initParamMap(request);

		try {
			System.out.println("#### params : " + params);
			// Service module
			optionitem = sncService.search("select_option_by_pk", params);
			if( optionitem == null ) return resultJsonError();

			
			System.out.println( "#### interiorItem  = " + optionitem.get(PAGING.RES_KEY_ROWS) );
			resJson.put("item", optionitem.get(PAGING.RES_KEY_ROWS));

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return resultJsonSuc(resJson);
	}
	
	/**
	 * SNC 컬러 수정
	 * POST /snc/color_update.json
	 */
	@RequestMapping(value="/snc/color_update.json")
	public @ResponseBody Map<String, Object> colorUpdate (
			HttpServletRequest request, HttpServletResponse response, HttpSession session){

		Map<String, Object> optionitem = new HashMap<String, Object>();
		Map<String, Object> resJson = new HashMap<String, Object>();

		// Get Parameter 
		Map<String, Object> params = RequestParameterMapUtils.initParamMap(request);
		
		params.put("mem_pid", session.getAttribute("mem_pid"));
		
		try {
			System.out.println("#### params : " + params);
			// Service module
			int result = sncService.updateCarColor(params);
			if(result > 0) {
				optionitem.put("result", result);
			}
			if( optionitem == null ) return resultJsonError();
			
			optionitem.put("option_code", params.get("option_code"));
			optionitem.put("before_color", params.get("before_color"));
			optionitem.put("before_price", params.get("before_price"));
			optionitem.put("update_color", params.get("update_color"));
			optionitem.put("update_price", params.get("update_price"));
			optionitem.put("btnAddress", params.get("btnAddress"));
				
			System.out.println( "#### interiorItem  = " + optionitem );
			resJson.put("item", optionitem);

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return resultJsonSuc(resJson);
	}
	
	/**
	 * SNC 다중 등록하기
	 * POST /snc/color_multiple.json
	 */
	@RequestMapping(value="/snc/color_multiple.json")
	public @ResponseBody Map<String, Object> colorMultiple (
			HttpServletRequest request, HttpServletResponse response, HttpSession session){

		Map<String, Object> optionitem = new HashMap<String, Object>();
		Map<String, Object> resJson = new HashMap<String, Object>();
		String[] listArr = request.getParameter("color_Multiple").split("//");
		List<String> colorList = new ArrayList<String>(Arrays.asList(listArr));
		
		// Get Parameter 
		Map<String, Object> params = RequestParameterMapUtils.initParamMap(request);
		
		try {
			System.out.println("#### params : " + params);
			// Service module
			
			Map<String, Object> map = new HashMap<String, Object>();
			String snc_option_code = "";
			String snc_option_pid = "";
			for(int i=0; i<colorList.size(); i++) {
			
				String[] list = colorList.get(i).split("@@");
				snc_option_code = list[0];
				map.put("snc_trim_pid", params.get("snc_trim_pid"));
				map.put("snc_car_pid", params.get("snc_car_pid"));
				map.put("mem_pid", session.getAttribute("mem_pid"));
				map.put("snc_option_code", list[0]);
				map.put("snc_option_name", list[1]);
				map.put("snc_option_price", list[2]);
				sncService.insertMultipleTx(map);
				snc_option_pid += map.get("snc_option_pid").toString()+"@@";
			}	
			
			optionitem.put("snc_option_pid", snc_option_pid);
			optionitem.put("snc_option_code", snc_option_code);
			optionitem.put("colorList", colorList);
				
			System.out.println( "#### interiorItem  = " + optionitem );
			resJson.put("item", optionitem);

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return resultJsonSuc(resJson);
	}
}