package com.zzimcar.admin.controller;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.AREA_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.COM_STATUS_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.DIRECTCAR_TRUE_FALSE_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.EXTRACHARGE_COLUMN_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.config.ZzimcarConstants.PICKUP_METHOD;
import com.zzimcar.admin.config.ZzimcarConstants.PROVIDER_COLUMN_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PROVIDER_TYPE;
import com.zzimcar.admin.config.ZzimcarConstants.RCMODEL_COLUMN_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.STATUS_CODE;
import com.zzimcar.admin.dao.ErpPeristalsisDao;
import com.zzimcar.admin.erp.vo.DataJson;
import com.zzimcar.admin.exception.DomainException;
import com.zzimcar.admin.exception.NotFoundLocationException;
import com.zzimcar.admin.exception.OverLapDataException;
import com.zzimcar.admin.service.CommonCodeService;
import com.zzimcar.admin.service.ErpPeristalsisService;
import com.zzimcar.admin.service.ProviderService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.PropConst;
import com.zzimcar.admin.utils.RequestParameterMapUtils;

@Controller
@RequestMapping("/provider")
public class ProviderController extends ZzimcarController {
	
	Logger logger = LoggerFactory.getLogger(ProviderController.class);
	
	@Resource(name = "ProviderService")
	ProviderService providerService;
	
	@Resource(name="CommonCodeService")
    private CommonCodeService codeService;
	
	@Resource(name = "ErpPeristalsisService")
	ErpPeristalsisService erpPeristalsisService;
	
	@Resource(name = "ErpPeristalsisDao")
	ErpPeristalsisDao erpPeristalsisDao;
	
	@RequestMapping(value = "/list_provider.do")
	public ModelAndView listProvider(HttpServletRequest request, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(VIEW_ROOT_PC + "/provider/provider_list");

		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			String cPage = ParamUtils.getString(param, "pages", "1");
			
			if(param.get("corp_status_view") == null) {
				param.put("corp_status_view", STATUS_CODE.CONTRACTED.getCode());
			}
			if(param.get("scType") == null) {
				param.put("scType", PROVIDER_COLUMN_CODE.CORP_NAME.getCode());
			}

			int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);

			param.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
			param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
			param.put("NONSELECT", STATUS_CODE.DELETED.getCode());

			Map<String, Object> list = providerService.getProviderList(param);
			List<Map<String, Object>> convertedlist = listConvertKorean(list);

			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);
			paging.setNumberOfRecords((int) list.get(PAGING.RES_KEY_TOTAL_COUNT));
			paging.pagingUtil(cPage);

			List<Map<String, Object>> selectBox_Status = new ArrayList<>();
			Map<String, Object> map = new HashMap<>();
			for (STATUS_CODE code : STATUS_CODE.values()) {
				if (code == STATUS_CODE.DELETED) {
					continue;
				}
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());

				selectBox_Status.add(map);
			}

			List<Map<String, Object>> selectBox_Type = new ArrayList<>();
			for (PROVIDER_TYPE code : PROVIDER_TYPE.values()) {
				map = new HashMap<>();
				map.put("value", code.getName());
				map.put("name", code.getName());
				selectBox_Type.add(map);
			}
			mav.addObject("TypeArr", selectBox_Type);

			List<Map<String, Object>> selectBox_ScType = new ArrayList<>();
			for(PROVIDER_COLUMN_CODE code : PROVIDER_COLUMN_CODE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				selectBox_ScType.add(map);
			}
			mav.addObject("scTypeArr", selectBox_ScType);
			
			List<Map<String, Object>> selectBox_Direct = new ArrayList<>();
			for(DIRECTCAR_TRUE_FALSE_CODE code : DIRECTCAR_TRUE_FALSE_CODE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				selectBox_Direct.add(map);
			}
			mav.addObject("DirectArr", selectBox_Direct);
			
			List<Map<String, Object>> select_area_code_list = new ArrayList<>();
			for(AREA_CODE code : AREA_CODE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				select_area_code_list.add(map);
			}
			mav.addObject("selectAreaCodeList", select_area_code_list);
			
			List<Map<String, Object>> erplist = erpPeristalsisDao.selectAll();
			
			mav.addObject("erplist", erplist);
			
			
			mav.addObject("statusArr", selectBox_Status);
			
			mav.addObject("list", convertedlist);
			mav.addObject("total", list.get(PAGING.RES_KEY_TOTAL_COUNT));
			mav.addObject("paging", paging);
			mav.addObject("params", param);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mav;
	}

	@RequestMapping(value = "/write_provider.do")
	public ModelAndView writeProvider(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(VIEW_ROOT_PC + "/provider/provider_write");

		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			int index = ParamUtils.getIntValue(param, "selected_id", 0);
			if (index != 0) {
				Map<String, Object> provider_item = providerService.selectProviderItemByPk(index);
				mav.addObject("item", provider_item);
			}

			List<Map<String, Object>> selectBox_Status = new ArrayList<>();
			
			for (STATUS_CODE code : STATUS_CODE.values()) {
				if(index == 0 && code == STATUS_CODE.DELETED) {
					continue;
				}
				Map<String, Object> map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());

				selectBox_Status.add(map);
			}
			mav.addObject("status", selectBox_Status);
			
			List<Map<String, Object>> select_erp_list = providerService.getErpList();
			for(int i = 0 ; i<select_erp_list.size(); i++) {
				for(String key : select_erp_list.get(i).keySet()) {
					if(key.equals("erp_name")) {
						Map<String, Object> data = new HashMap<>();
						switch ((String)select_erp_list.get(i).get(key)) {
						case "INS":
						case "JEJUGRIM":
							data.put("idpwshow", true);
							break;
						default:
							data.put("idpwshow", false);
							break;
						}
						select_erp_list.get(i).put("data", data);
					}
				}
			}
			mav.addObject("ERP_LIST", select_erp_list);
			

			List<Map<String, Object>> selectBox_Type = new ArrayList<>();
			
			for (PROVIDER_TYPE code : PROVIDER_TYPE.values()) {
				Map<String, Object> map = new HashMap<>();
				map.put("value", code.getName());
				map.put("name", code.getName());
				selectBox_Type.add(map);
			}
			mav.addObject("type", selectBox_Type);
			
			List<Map<String, Object>> selectBox_pickup_method = new ArrayList<>();
			for (PICKUP_METHOD code : PICKUP_METHOD.values()) {
				Map<String, Object> map = new HashMap<>();
				map.put("value", code.getKey());
				map.put("name", code.getValue());

				selectBox_pickup_method.add(map);
			}
			mav.addObject("PICKUP_METHOD", selectBox_pickup_method);
			
			List<Map<String, Object>> erp_list = providerService.getErpList();
			for(int i = 0; i<erp_list.size(); i++) {
				Map<String, Object> data = new HashMap<>();
				if(erp_list.get(i).get("erp_name").equals("INS")) {
					data.put("showInput", true);
				} else if(erp_list.get(i).get("erp_name").equals("INS")) {
					data.put("showInput", true);
				} else {
					data.put("showInput", false);
				}
				erp_list.get(i).put("data", data);
			}
			
			List<Map<String, Object>> selectBox_Direct = new ArrayList<>();
			for(DIRECTCAR_TRUE_FALSE_CODE code : DIRECTCAR_TRUE_FALSE_CODE.values()) {
				Map<String, Object> map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				selectBox_Direct.add(map);
			}
			mav.addObject("is_direct_arr", selectBox_Direct); 
			
			List<Map<String, Object>> select_area_code_list = new ArrayList<>();
			for(AREA_CODE code : AREA_CODE.values()) {
				Map<String, Object> map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				select_area_code_list.add(map);
			}
			mav.addObject("area_code_list", select_area_code_list);
			
			String corp_status_view = ParamUtils.getString(param, "corp_status_view", "");
			mav.addObject("corp_status_view", corp_status_view);
			
			
			String direct_view = ParamUtils.getString(param, "direct_view", "");
			mav.addObject("direct_view", direct_view);
			
			String corp_type_view = ParamUtils.getString(param, "corp_type_view", "");
			mav.addObject("corp_type_view", corp_type_view);
			
			String erplist_view = ParamUtils.getString(param, "erplist_view", "");
			mav.addObject("erplist_view", erplist_view);
			
			String areacode_view = ParamUtils.getString(param, "areacode_view", "");
			mav.addObject("areacode_view", areacode_view);
			

			String scType = ParamUtils.getString(param, "scType", "");
			mav.addObject("scType", scType);

			String scValue = ParamUtils.getString(param, "scValue", "");
			mav.addObject("scValue", scValue);

			int pages = ParamUtils.getIntValue(param, "pages", 1);
			mav.addObject("pages", pages);
			
			List<Map<String, Object>> resultAllList = erpPeristalsisService.selectNmcarModelList();
			Collections.sort(resultAllList, new Comparator<Map<String, Object >>() {
	            @Override
	            public int compare(Map<String, Object> first, Map<String, Object> second) {
	                return ((String) first.get("modelName")).compareTo((String) second.get("modelName")); //descending order
	            }
	        });
			mav.addObject("list", resultAllList); 
			//신규 은행명 코드 가져오기
			List<Map<String, Object>> bankList =  codeService.getBankInfo();
			mav.addObject("bankList",bankList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav;
	}

	@RequestMapping(value = "/delete_provider.do")
	public @ResponseBody Map<String, Object> deleteProvider(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Map<String, Object> result = new HashMap<String, Object>();
		String resultMsg = "";

		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			param.put("corpStatus", STATUS_CODE.DELETED.getCode());
			param.put("chargeStatus", COM_STATUS_CODE.DELETED.getCode());
			
			param.put("mod_mem_pid", session.getAttribute("mem_pid"));

			providerService.updateStatusProviderTx(param);
			resultMsg = PropConst.PROVIDER_DELETE_SUCCESS;
			result.put("result", true);
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = PropConst.PROVIDER_DELETE_FAILED;
			result.put("result", false);
		}

		result.put("msg", resultMsg);
		return result;
	}

	@RequestMapping(value = "/save_provider.do")
	public @ResponseBody Map<String, Object> saveProvider(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);

		String resultMsg = "";
		try {
			param.put("reg_mem_pid", session.getAttribute("mem_pid"));
			
			if(param.get("is_direct") == null || !param.get("is_direct").equals("Y")) {
				param.put("dir_domain", null);
				param.put("dir_rate_settl", 0);
				param.put("dir_rate_settl_extracharge", 0);
				param.put("dir_rate_bankruptcy_booking", 0);
				param.put("dir_rate_cancel_in_pg24", 0);
				param.put("dir_rate_cancel_out_pg24", 0);
				param.put("dir_rate_in_rent24", 0);
				param.put("dir_rate_in_rent72", 0);
			}
			
			if (param.get("providerPid") == null || param.get("providerPid").toString().trim().equals("")) {
				providerService.insertProviderItemTx(param);
				resultMsg = PropConst.WRITE_SUCCESS_INSERT;
			} else {
				providerService.updateProviderItemTx(param);
				resultMsg = PropConst.WRITE_SUCCESS_UPDATE;
			}

			result.put("result", true);
		} catch (DomainException e) {
			e.printStackTrace();
			resultMsg = PropConst.PROVIDER_ALREADY_DOMAIN_EXIST;

			result.put("result", false);
		} catch (OverLapDataException e) {
			e.printStackTrace();
			resultMsg = PropConst.PROVIDER_ALREADY_EXSIT;

			result.put("result", false);
		} catch (NotFoundLocationException e) {
			e.printStackTrace();
			resultMsg = PropConst.PROVIDER_NOT_FOUND_LOCATION;

			result.put("result", false);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			resultMsg = PropConst.PROVIDER_KAKAO_ERROR;

			result.put("result", false);
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = PropConst.WRITE_ERROR_OCCURED;

			result.put("result", false);
		}

		result.put("msg", resultMsg);
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> listConvertKorean(Map<String, Object> original_list) {
		List<Map<String, Object>> list_after = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> olist = (List<Map<String, Object>>) original_list.get(PAGING.RES_KEY_ROWS);
		for (Map<String, Object> map : olist) {
			Map<String, Object> newmap = new HashMap<>();
			for (String key : map.keySet()) {
				if (key.equals("corpStatus")) {
					newmap.put(key, code2KoreanConvert(map.get(key).toString()));
				} else {
					newmap.put(key, map.get(key));
				}
			}
			list_after.add(newmap);
		}
		return list_after;
	}

	public String code2KoreanConvert(String original_code) {
		for (STATUS_CODE kor : STATUS_CODE.values()) {
			if (kor.getCode().equals(original_code)) {
				return kor.getKorean();
			}
		}
		return "알수없음";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/list_rcmodel.do")
	public ModelAndView listRcmodel(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(VIEW_ROOT_PC + "/provider/rcmodel_list");

		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			String cPage = ParamUtils.getString(param, "pages", "1");

			int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);
			
			param.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
			param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
			param.put("NONSELECT", COM_STATUS_CODE.DELETED.getCode());

			Map<String, Object> list = providerService.getRcList(param);

			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);
			paging.setNumberOfRecords((int) list.get(PAGING.RES_KEY_TOTAL_COUNT));
			paging.pagingUtil(cPage);
						
			List<Map<String, Object>> selectBox_Status = new ArrayList<>();
			Map<String, Object> map = new HashMap<>();
			for (COM_STATUS_CODE code : COM_STATUS_CODE.values()) {
				if (code == COM_STATUS_CODE.DELETED) {
					continue;
				}
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());

				selectBox_Status.add(map);
			}
			mav.addObject("statusArr", selectBox_Status);
			
			String isBundlingView = ParamUtils.getString(param, "isBundlingView", "");
			mav.addObject("isBundlingView", isBundlingView);
			
			List<Map<String, Object>> selectBox_ModelGear = codeService.getModelGearGroup();
			mav.addObject("ModelGear", selectBox_ModelGear);
			
			List<Map<String, Object>> selectBox_ModelFuel = codeService.getModelFuelGroup();
			mav.addObject("ModelFuel", selectBox_ModelFuel);
			
			List<Map<String, Object>> selectBox_ModelClass = codeService.getModelClassGroup();
			mav.addObject("ModelClass", selectBox_ModelClass);
			
			List<Map<String, Object>> selectBox_ModelMaker = codeService.getModelMakerGroup();
			mav.addObject("ModelMaker", selectBox_ModelMaker);
			
			List<Map<String, Object>> selectBox_Direct = new ArrayList<>();
			for(DIRECTCAR_TRUE_FALSE_CODE code : DIRECTCAR_TRUE_FALSE_CODE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				selectBox_Direct.add(map);
			}
			mav.addObject("DirectArr", selectBox_Direct);
			
			List<Map<String, Object>> select_area_code_list = new ArrayList<>();
			for(AREA_CODE code : AREA_CODE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				select_area_code_list.add(map);
			}
			mav.addObject("selectAreaCodeList", select_area_code_list);

			List<Map<String, Object>> selectBox_ScType = new ArrayList<>();
			for(RCMODEL_COLUMN_CODE code : RCMODEL_COLUMN_CODE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				selectBox_ScType.add(map);
			}
			mav.addObject("scTypeArr", selectBox_ScType);
			
			
			String script = "<script>\n$(document).ready(function(){";
			List<Map<String, Object>> carmellist = ((List<Map<String, Object>>)list.get(PAGING.RES_KEY_ROWS));
			for(int i=0; i<carmellist.size(); i++) {
				script += "\n\t var classCode = getCodeName('CAR_CLASS_CODE', '"+carmellist.get(i).get("classCode")+"');";
				script += "\n\t classCode = classCode != '' ? classCode : \"없음\";";
				script += "\n\t $(\"#rcmodel_"+carmellist.get(i).get("rcmodelPid")+" .classCode\").html(classCode);";
				
				script += "\n\t var gearCode = getCodeName('CAR_GEAR_CODE', '"+carmellist.get(i).get("gearCode")+"');";
				script += "\n\t gearCode = gearCode != '' ? gearCode : \"없음\";";
				script += "\n\t $(\"#rcmodel_"+carmellist.get(i).get("rcmodelPid")+" .gearCode\").html(gearCode);";
				
				script += "\n\t var makerCode = getCodeName('CAR_MAKER_CODE', '"+carmellist.get(i).get("makerCode")+"');";
				script += "\n\t makerCode = makerCode != '' ? makerCode : \"없음\";";
				script += "\n\t $(\"#rcmodel_"+carmellist.get(i).get("rcmodelPid")+" .makerCode\").html(makerCode);";
				
				script += "\n\t var fuelCode = getCodeName('CAR_FUEL_CODE', '"+carmellist.get(i).get("fuelCode")+"');";
				script += "\n\t fuelCode = fuelCode != '' ? fuelCode : \"없음\";";
				script += "\n\t $(\"#rcmodel_"+carmellist.get(i).get("rcmodelPid")+" .fuelCode\").html(fuelCode);";
			}
			script += "\n})\n</script>";

			mav.addObject("script", script);
			
			mav.addObject("rccarmodel", carmellist);
			mav.addObject("total", list.get(PAGING.RES_KEY_TOTAL_COUNT));
			mav.addObject("paging", paging);
			mav.addObject("params", param);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mav;
	}
	
	@RequestMapping(value = "/write_rcmodel.do")
	public ModelAndView writeRcmodel(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(VIEW_ROOT_PC + "/provider/rcmodel_write");

		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			int index = ParamUtils.getIntValue(param, "selected_id", 0);
			if (index != 0) {
				Map<String, Object> item = providerService.selectRcModelItemByPk(index);
				mav.addObject("item", item);
				
				String jsCode = new String();
				
				jsCode = "<script>\n$(document).ready(function(){";
				if(item != null) {
					jsCode += "\n\t fuelChange();";
					jsCode += "\n\t makerChange();";
					
					if(item.get("classCode") != null && !item.get("classCode").equals("")) {
						jsCode += "\n\t var classCode = getCodeName('CAR_CLASS_CODE', '"+String.valueOf(item.get("classCode"))+"');";
						jsCode += "\n\t classCode = classCode != '' ? classCode : '없음';";
						jsCode += "\n\t $('#class_code').html(classCode);";
					}
					
					if(item.get("makerCode") != null && !item.get("makerCode").equals("")) {
						jsCode += "\n\t var makerCode = getCodeName('CAR_MAKER_CODE', '"+String.valueOf(item.get("makerCode"))+"');";
						jsCode += "\n\t makerCode = makerCode != '' ? makerCode : '없음';";
						jsCode += "\n\t $('#maker_code').html(makerCode);";
					}
					
					if(item.get("carrierCode") != null && !item.get("carrierCode").equals("")) {
						jsCode += "\n\t var carrierCode = getCodeName('CAR_CARRIER_CODE', '"+String.valueOf(item.get("carrierCode"))+"');";
						jsCode += "\n\t carrierCode = carrierCode != '' ? carrierCode : '없음';";
						jsCode += "\n\t $('#carrier_code').html(carrierCode);";
					}
					
					if(item.get("imgUrlList") == null || item.get("imgUrlList").equals("")) {
						jsCode += "\n\t $(\".imgUrlList\").hide();";
					}
					
					if(item.get("imgUrlDetail") == null || item.get("imgUrlDetail").equals("")) {
						jsCode += "\n\t $(\".imgUrlDetail\").hide();";
					}
				}
				jsCode += "\n})\n</script>";
				mav.addObject("jsCode", jsCode);
			} else {
				List<Map<String, Object>> area_code_list = new ArrayList<>();
				for (AREA_CODE code : AREA_CODE.values()) {
					Map<String, Object> map = new HashMap<>();
					map.put("value", code.getCode());
					map.put("name", code.getKorean());

					area_code_list.add(map);
				}
				mav.addObject("area_code_list", area_code_list);
				
				param.put("area_sort", "ASC");
				List<Map<String, Object>> rcCorpNameList = providerService.getRentcarCompanyList(param);
				mav.addObject("rcCorpNameList", rcCorpNameList);
				
				param.put("NONSELECT", COM_STATUS_CODE.DELETED.getCode());
				
				List<Map<String, Object>> car_list = providerService.getRentcarModelList(param);
				mav.addObject("nmCarList", car_list);
			}
			mav.addObject("pid", index);
			
			List<Map<String, Object>> optionList = providerService.selectRcModelItemOptionByPk(index);
			mav.addObject("optionList", optionList);
			
			List<Map<String, Object>> selectBox_Status = new ArrayList<>();
			for (COM_STATUS_CODE code : COM_STATUS_CODE.values()) {
				if(index == 0 && code == COM_STATUS_CODE.DELETED) {
					continue;
				}
				Map<String, Object> map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());

				selectBox_Status.add(map);
			}
			mav.addObject("status", selectBox_Status);
						
			List<Map<String, Object>> selectBox_ModelGear = codeService.getModelGearGroup();
			mav.addObject("ModelGear", selectBox_ModelGear);
			
			List<Map<String, Object>> selectBox_ModelFuel = codeService.getModelFuelGroup();
			mav.addObject("ModelFuel", selectBox_ModelFuel);
			
			List<Map<String, Object>> selectBox_LicenseCode = codeService.getLicenseCodeGroup();
			mav.addObject("LicenseCode", selectBox_LicenseCode);
			
			String gear_code_view = ParamUtils.getString(param, "gear_code_view", "");
			mav.addObject("gear_code_view", gear_code_view);
			
			String fuel_code_view = ParamUtils.getString(param, "fuel_code_view", "");
			mav.addObject("fuel_code_view", fuel_code_view);
			
			String class_code_view = ParamUtils.getString(param, "class_code_view", "");
			mav.addObject("class_code_view", class_code_view);
			
			String maker_code_view = ParamUtils.getString(param, "maker_code_view", "");
			mav.addObject("maker_code_view", maker_code_view);
			
			String model_status_view = ParamUtils.getString(param, "model_status_view", "");
			mav.addObject("model_status_view", model_status_view);
			
			String isBundlingView = ParamUtils.getString(param, "isBundlingView", "");
			mav.addObject("isBundlingView", isBundlingView);
			
			String areacode_view = ParamUtils.getString(param, "areacode_view", "");
			mav.addObject("areacode_view", areacode_view);
			
			List<Map<String, Object>> selectBox_ModelClass = codeService.getModelClassGroup();
			mav.addObject("ModelClass", selectBox_ModelClass);

			List<Map<String, Object>> selectBox_ModelMaker = codeService.getModelMakerGroup();
			mav.addObject("ModelMaker", selectBox_ModelMaker);

			List<Map<String, Object>> selectBox_ModelCarrier = codeService.getModelCarrierGroup();
			mav.addObject("ModelCarrier", selectBox_ModelCarrier);
			
			String direct_view = ParamUtils.getString(param, "direct_view", "");
			mav.addObject("direct_view", direct_view);

			String scType = ParamUtils.getString(param, "scType", "");
			mav.addObject("scType", scType);

			String scValue = ParamUtils.getString(param, "scValue", "");
			mav.addObject("scValue", scValue);

			int pages = ParamUtils.getIntValue(param, "pages", 1);
			mav.addObject("pages", pages);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav;
	}
	
	@RequestMapping(value = "/delete_rcmodel.do")
	public @ResponseBody Map<String, Object> deleteRcmodel(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Map<String, Object> result = new HashMap<String, Object>();
		String resultMsg = "";

		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			param.put("corpStatus", COM_STATUS_CODE.DELETED.getCode());
			param.put("mod_mem_pid", session.getAttribute("mem_pid"));

			providerService.updateStatusRcModelItemTx(param);
			resultMsg = PropConst.PROVIDER_DELETE_SUCCESS;
			result.put("result", true);
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = PropConst.PROVIDER_DELETE_FAILED;
			result.put("result", false);
		}

		result.put("msg", resultMsg);
		return result;
	}
	
	@RequestMapping(value = "/save_rcmodel.do")
	public @ResponseBody Map<String, Object> saveRcmodel(HttpServletRequest request, HttpServletResponse response,
			HttpSession session){
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		
		String resultMsg = "";
		
		try {
			param.put("reg_mem_pid", session.getAttribute("mem_pid"));
			if (param.get("rcmodel_pid") == null || param.get("rcmodel_pid").toString().trim().equals("")) {
				providerService.insertRCModlItemTx(param);
				resultMsg = PropConst.WRITE_SUCCESS_INSERT;
			} else {
				providerService.updateRCModlItemTx(param);
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
	
	@ResponseBody
	@RequestMapping(value="/RcModelfileUpload.do", method=RequestMethod.POST)
	public void RcModelfileUpload(MultipartHttpServletRequest request,HttpServletResponse response)throws Exception {
		response.setContentType("text/plain; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> fileresult = null;
		String result ="";
		// 파일업로드
		String fileName = request.getFiles("imgList").get(0).getOriginalFilename();
		if (fileName != null && !(fileName.equals(""))) {
			fileresult = providerService.fileupload(request, "rcmodel");
			result =  gson.toJson(fileresult);
		}
		else {
			result = gson.toJson("N");
		}
		print.append(result);

	}
	
	@RequestMapping(value = "/getRentCarCompany.json")
	public @ResponseBody Map<String, Object> getRentCarCompany(HttpServletRequest request, HttpSession session) throws Exception{
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		
		String pids = String.valueOf(param.get("provider_pid"));
		int pid = Integer.parseInt(pids.equals("") ? "0" : pids);
		Map<String, Object> data = providerService.selectRentcarCorpData(pid);
		if(data == null) {
			result.put("result", false);
		} else {
			result.put("result", data);
		}
		return result; 
	}

	@RequestMapping(value = "/getCompanyList.json")
	public @ResponseBody Map<String, Object> getCompanyList(HttpServletRequest request, HttpSession session)
			throws Exception {
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = providerService.getRentcarCompanyList(param);
		result.put("list", list);
		return result;
	}
	
	@RequestMapping(value = "/getCarModelList.json")
	public @ResponseBody Map<String, Object> getCarModelList(HttpServletRequest request, HttpSession session)
			throws Exception {
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		param.put("NONSELECT", COM_STATUS_CODE.DELETED.getCode());
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = providerService.getRentcarModelList(param);
		result.put("list", list);
		return result;
	}

	@RequestMapping(value = "/list_extracharge.do")
	public ModelAndView listExtraCharge(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
        mav.setViewName(VIEW_ROOT_PC+"/provider/list_extracharge");
        try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			String cPage = ParamUtils.getString(param,  "page", "1");
			int pgNum = NumberUtils.toInt(cPage)==0?1:NumberUtils.toInt(cPage);
			System.out.println( "param = " + param.toString() );
			
			// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 섫정한다.
			param.put(PAGING.QUERY_KEY_OFFSET , (pgNum - 1) * PAGING.ROW_COUNT );
			param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
		
			param.put("search_type"	, ParamUtils.getString(param, "search_type" , null));
			param.put("search_value", ParamUtils.getString(param, "search_value", null));
			// 페이징용 쿼리를 위해 Service의 search 를 사용
			// 결과가 Map->rows 이 결과 목록과 Map->total에 전체 갯수가 리턴된다.
			Map<String, Object> items =providerService.listExtraCharge(param);  

			// 페이징 셋팅
			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);	
			paging.setNumberOfRecords((int)items.get(PAGING.RES_KEY_TOTAL_COUNT));	
			paging.pagingUtil( cPage );

			//검색옵션 셋팅
			List<Map<String, Object>> searchOption = new ArrayList<>();
	        for (EXTRACHARGE_COLUMN_CODE code : EXTRACHARGE_COLUMN_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            searchOption.add(map);
	        }
			mav.addObject("searchOption", searchOption);
			mav.addObject("number",param.get("offset"));
			mav.addObject("list"	, items.get(PAGING.RES_KEY_ROWS));
			mav.addObject("total"	, items.get(PAGING.RES_KEY_TOTAL_COUNT));
			mav.addObject("paging"	, paging);
			mav.addObject("json"	, new Gson().toJson( items ));
			mav.addObject("params", param);
			
			List<Map<String, Object>> select_area_code_list = new ArrayList<>();
			for(AREA_CODE code : AREA_CODE.values()) {
				Map<String, Object> map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				select_area_code_list.add(map);
			}
			mav.addObject("selectAreaCodeList", select_area_code_list);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav;
	}

	@RequestMapping(value = "/write_extraCharge.do")
	public ModelAndView writeExtraCharge(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			Map<String, Object> items = providerService.writeExtraCharge(Integer.valueOf(request.getParameter("rccharge_pid")));
			mav.addObject("items", items);
			
			if(items == null) {
				List<Map<String, Object>> area_code_list = new ArrayList<>();
				for(AREA_CODE code : AREA_CODE.values()) {
					Map<String, Object> map = new HashMap<>();
					map.put("value", code.getCode());
					map.put("name", code.getKorean());
					area_code_list.add(map);
				}
				mav.addObject("area_code_list", area_code_list);
				
				param.put("area_sort", "ASC");
				List<Map<String, Object>> rcCorpNameList = providerService.getRentcarCompanyList(param);
				mav.addObject("rcCorpNameList",rcCorpNameList);
			}
			
			String areacode_view = ParamUtils.getString(param, "areacode_view", null);
			mav.addObject("areacode_view", areacode_view);
			
			String search_value = ParamUtils.getString(param, "search_value", null);
			mav.addObject("search_value", search_value);

			String search_type = ParamUtils.getString(param, "search_type", null);
			mav.addObject("search_type", search_type);
			
			int page = ParamUtils.getIntValue(param, "page", 1);
			mav.addObject("page", page);
			
	        mav.setViewName(VIEW_ROOT_PC+"/provider/write_extracharge");
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
        return mav;
	}
	
	@RequestMapping(value="/save_extraCharge.do")
	public @ResponseBody Map<String, Object> saveExtraCharge(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		System.out.println( "param =========== " + param.toString() );
		Map<String, Object> result = new HashMap<String, Object>();
		String retMsg = new String();
		
		if(param.get("rccharge_pid")==null || param.get("rccharge_pid")=="") {
			Map<String, Object> items =providerService.dateCheckExtraCharge(param);
			System.out.println( "items @@@@@@@@@@@@@@@@@@@@ " + items.toString() );
			System.out.println( "items ==================== " + items.get("chargeSdate")+""+ items.get("chargeEdate"));
			if(items.get("chargeSdate").toString().equals("0") && items.get("chargeEdate").toString().equals("0")) {
				param.put("reg_mem_pid", session.getAttribute("mem_pid"));
				param.put("mod_mem_pid", session.getAttribute("mem_pid"));
				providerService.insertExtraCharge(param);
				result.put("chk", true);
				retMsg = PropConst.WRITE_SUCCESS_INSERT;
			} else {
				result.put("chk", false);
				retMsg = PropConst.PROVIDER_DATE_FAIL_MSG;
			}
		} else {
			Map<String, Object> items =providerService.dateCheckExtraCharge(param);
			if(items.get("chargeSdate").toString().equals("0") && items.get("chargeEdate").toString().equals("0")) {
				param.put("mod_mem_pid", session.getAttribute("mem_pid"));
				providerService.saveExtraCharge(param);
				result.put("chk", true);
				retMsg = PropConst.WRITE_SUCCESS_UPDATE;
			} else {
				result.put("chk", false);
				retMsg = PropConst.PROVIDER_DATE_FAIL_MSG;
			}
			
		}
		result.put("msg", retMsg);
        return result;
	}
	
	@RequestMapping(value="/delete_extraCharge.do")
	public String deleteExtraCharge(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		String[] items = request.getParameterValues("myCheck");
		if(items == null || items.length <= 0){
			return "redirect:/provider/list_extracharge.do";
		}
		for(int i=0; i<items.length; i++){
			param.put("mod_mem_pid", session.getAttribute("mem_pid"));
			param.put("rccharge_pid", items[i]);
			providerService.deleteExtraChargeTx(param);
		}
		
        return "redirect:/provider/list_extracharge.do";
	}
	
	@RequestMapping(value="/erpProviderList.do")
	public @ResponseBody Map<String, String> erpProviderList(HttpServletRequest request,HttpServletResponse response, HttpSession session) throws UnsupportedEncodingException{
		
		DataJson jsonList = new DataJson();
		
		Map<String, Object> rm = new HashMap<String, Object>();
		Map<String, String> rmsg = new HashMap<String, String>();
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		rm.put("cmd", INS_CMD_COMPANYLIST);
		rm.put("uid", INS_UID);
		rm.put("passcode", INS_PASSCODE);
		
		try {
			
			jsonList = erpPeristalsisService.httpClientJson(ZzimcarController.INS_URL,rm,ZzimcarController.ERP_INS);
			
			rmsg.put("msg", PropConst.PROVIDER_FAIL_MSG);
			
			// 미등록 렌트카 업체 등록
			for(DataJson.InsCompanyList companyList : jsonList.insCompanyList){
				
				rm = new HashMap<String, Object>();
				
				result = new ArrayList<Map<String, Object>>();
				
				rm.put("reg_mem_pid", session.getAttribute("mem_pid"));
				
				rm.put("erp_pid", INS_ERPCODE);
				rm.put("corp_code", "NM_"+companyList.connectNo);
				rm.put("erp_client_cd", companyList.ins_rentComCode);
				
				result = erpPeristalsisService.rentCarCorpList(rm);
				
				if(result.size()==0) {
					// provider_corp , rentcar_corp
					rm.put("corp_name", companyList.ins_rentCompany);
					rm.put("corp_public_name", companyList.ins_rentCompany);
					
					erpPeristalsisService.insertCorpTx(rm);
					
					rmsg.put("msg", PropConst.PROVIDER_SUCCESS_MSG);
					
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}

        return rmsg;
	}
	
	@RequestMapping(value="/insErpRentCarList.do")
	public @ResponseBody Map<String, String> insErpRentCarList(HttpServletRequest request, HttpSession session){
		
		Map<String, String> rmsg = new HashMap<String, String>();
		
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			
			Map<String, Object> mp = new HashMap<String, Object>();
			
			// 차량리스트
			mp.put("cmd", ZzimcarController.INS_CMD_CARLIST);
			mp.put("uid", ZzimcarController.INS_UID);
			mp.put("passcode", ZzimcarController.INS_PASSCODE);
			
			mp.put("rcname", param.get("rcname"));
			mp.put("erp_pid", param.get("erp_pid"));
			mp.put("provider_pid", param.get("provider_pid"));
			
			mp.put("reg_mem_pid", session.getAttribute("mem_pid"));
			
			DataJson carJsonList =  erpPeristalsisService.httpClientJson(ZzimcarController.INS_URL,mp,ZzimcarController.ERP_INS); // 차량검색
			
			// R 보험리스트
			/*
			mp = new HashMap<String, Object>();
			
			mp.put("cmd", ZzimcarController.INS_CMD_INSLIST_R);
			mp.put("uid", ZzimcarController.INS_UID);
			mp.put("passcode", ZzimcarController.INS_PASSCODE);
			
			mp.put("rcname", param.get("rcname"));
			mp.put("erp_pid", param.get("erp_pid"));
			mp.put("provider_pid", param.get("provider_pid"));
			
			mp.put("reg_mem_pid", session.getAttribute("mem_pid"));
			
			DataJson pubJsonList =  erpPeristalsisService.httpClientJson(ZzimcarController.INS_URL,mp,ZzimcarController.ERP_INS); // 차량검색
			*/
			// 보험리스트
			/*
			mp = new HashMap<String, Object>();
			
			mp.put("cmd", ZzimcarController.INS_CMD_INSLIST_R);
			mp.put("uid", ZzimcarController.INS_UID);
			mp.put("passcode", ZzimcarController.INS_PASSCODE);
			
			mp.put("rcname", param.get("rcname"));
			mp.put("erp_pid", param.get("erp_pid"));
			mp.put("provider_pid", param.get("provider_pid"));
			
			mp.put("reg_mem_pid", session.getAttribute("mem_pid"));
			
			DataJson insuJsonList =  erpPeristalsisService.httpClientJson(ZzimcarController.INS_URL,mp,ZzimcarController.ERP_INS); // 차량검색
			*/
			
			if(carJsonList.insCarList.size()!=0) {	
				
				mp.put("rccorp_pid", param.get("rccorp_pid"));
				//erpPeristalsisService.insRentCarTx(carJsonList,pubJsonList,insuJsonList, mp);
				erpPeristalsisService.insRentCarTx(carJsonList, mp);
				
				rmsg.put("msg", PropConst.PROVIDER_SUCCESS_MSG);
				
			}else {
				
				rmsg.put("msg", PropConst.RENTCAR_FAIL_MSG);
			}				
			
		}catch(Exception e) {
			e.printStackTrace();
			rmsg.put("msg", PropConst.RENTCAR_FAIL_MSG);
		}
		
        return rmsg;
	}
	
	@RequestMapping(value="/jejuErpRentCarList.do")
	public @ResponseBody Map<String, String> jejuErpRentCarList(HttpServletRequest request, HttpSession session){
		
		Map<String, String> rmsg = new HashMap<String, String>();
		
		try {
				Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
				Map<String, Object> mp = new HashMap<String, Object>();
				mp.put("erp_client_cd", param.get("erp_client_cd"));
				mp.put("erp_call_code", param.get("erp_call_code"));
				mp.put("provider_pid", param.get("provider_pid"));
				mp.put("erp_pid", param.get("erp_pid"));
				mp.put("rccorp_pid", param.get("rccorp_pid"));

				mp.put("reg_mem_pid", session.getAttribute("mem_pid"));
				erpPeristalsisService.jejuRentCarTx(mp);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
        return rmsg;
	}
	
	@RequestMapping(value="jejuGrimMappingList.do")
	public @ResponseBody List<Map<String,Object>> jejuGrimMappingList(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		Map<String, Object> mp = new HashMap<String, Object>();
		
		mp.put("erp_client_cd", param.get("erp_client_cd"));
		mp.put("erp_call_code", param.get("erp_call_code"));
		mp.put("provider_pid", param.get("provider_pid"));
		mp.put("erp_pid", param.get("erp_pid"));
		mp.put("rccorp_pid", param.get("rccorp_pid"));
		
		mp.put("reg_mem_pid", session.getAttribute("mem_pid"));

		try {
		
			 HashMap<String,Object> map=new HashMap<String,Object>();

			 map.put("memberlist",erpPeristalsisService.erpSyncChkEx(mp));
			 model.addAttribute("list", erpPeristalsisService.erpSyncChkEx(mp));
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		return erpPeristalsisService.erpSyncChkEx(mp);
	}
	
	@RequestMapping(value="jejuGrimMappingSave.do")
	public@ResponseBody String jejuGrimMappingSave(HttpServletRequest request, HttpServletResponse response) {

		try {
			
			String[] listArr = request.getParameter("list").split("//");
			List<String> rentCarList = new ArrayList<String>(Arrays.asList(listArr));
			
			Map<String, Object> map = new HashMap<String, Object>();
			
			for(int i=0; i<rentCarList.size(); i++) {
			
				String[] list = rentCarList.get(i).split("@@");
				String[] nmmodelPidArr = list[1].split("##");
				
				map.put("erp_code", list[0]);
				map.put("nm_code", nmmodelPidArr[0]);
				map.put("nmmodel_pid", nmmodelPidArr[1]);
				map.put("rccorp_pid", request.getParameter("rccorp_pid"));
				erpPeristalsisService.updateErpSyncTx(map);
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return PropConst.WRITE_SUCCESS_INSERT;
	}
	
	// 리븐 ERP연동
	@RequestMapping(value="rebornMappingList.do")
	public @ResponseBody Map<String, String> rebornMappingList(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		
		Map<String, String> rmsg = new HashMap<String, String>();
		
		try {
			
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			Map<String, Object> mp = new HashMap<String, Object>();
			
			mp.put("erp_client_cd", param.get("erp_client_cd"));
			mp.put("provider_pid", param.get("provider_pid"));
			mp.put("erp_pid", param.get("erp_pid"));
			mp.put("rccorp_pid", param.get("rccorp_pid"));
			mp.put("clientId", param.get("corp_code"));
			
			mp.put("reg_mem_pid", session.getAttribute("mem_pid"));
			
			//mp.put("clientId", "MN_JEJUASAN");
			
			int cnt = erpPeristalsisService.rebornRentCarTx(mp);
			
			if(cnt!=0) {
				rmsg.put("msg", PropConst.PROVIDER_SUCCESS_MSG);
			}else {
				rmsg.put("msg", PropConst.RENTCAR_FAIL_MSG);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			rmsg.put("msg", "관리자에게 문의주세요");
		}
		
		return rmsg;
	}
	
	@RequestMapping(value="list_settlement.do")
	public ModelAndView settlementList(HttpServletRequest request) throws Exception {
		
		ModelAndView mav = new ModelAndView();
		
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		String cPage = ParamUtils.getString(param, "pages", "1");

		int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);

		param.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
		param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);

		Map<String, Object> pageList = providerService.selectSettlementTolCnt(param);
		
		Paging paging = new Paging();
		paging.setmaxPost(PAGING.ROW_COUNT);
		paging.setNumberOfRecords((int) pageList.get(PAGING.RES_KEY_TOTAL_COUNT));
		paging.pagingUtil(cPage);
		
		List<Map<String, Object>> select_area_code_list = new ArrayList<>();
		for(AREA_CODE code : AREA_CODE.values()) {
			Map<String, Object> map = new HashMap<>();
			map.put("value", code.getCode());
			map.put("name", code.getKorean());
			select_area_code_list.add(map);
		}
		mav.addObject("selectAreaCodeList", select_area_code_list);
		
		mav.addObject("areacodeView", param.get("areacodeView"));
		mav.addObject("settlInSdtime", param.get("settlInSdtime"));
		mav.addObject("settlInEdtime", param.get("settlInEdtime"));
		mav.addObject("rccorpName", param.get("rccorpName"));
		mav.addObject("list", pageList.get(PAGING.RES_KEY_ROWS));
		mav.addObject("total", pageList.get(PAGING.RES_KEY_TOTAL_COUNT));
		mav.addObject("paging", paging);
		
		mav.setViewName(VIEW_ROOT_PC + "/provider/list_settlement");
		
		return mav;
	}
	
	@RequestMapping(value="settlementDetails.do")
	public ModelAndView settleDetails(HttpServletRequest request) throws Exception {

		ModelAndView mav = new ModelAndView();
		
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		String cPage = ParamUtils.getString(param, "pages", "1");

		int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);

		param.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
		param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
		param.put("NONSELECT", STATUS_CODE.DELETED.getCode());

		Map<String, Object> pageList = providerService.selectSettlementDetailTolCnt(param);
		
		Paging paging = new Paging();
		paging.setmaxPost(PAGING.ROW_COUNT);
		paging.setNumberOfRecords((int) pageList.get(PAGING.RES_KEY_TOTAL_COUNT));
		paging.pagingUtil(cPage);
		
		mav.addObject("settlSdate", param.get("settlSdate"));
		mav.addObject("settlEdate", param.get("settlEdate"));
		mav.addObject("rccorpPid", param.get("rccorpPid"));
		mav.addObject("isDirect", param.get("isDirectChk"));
		
		mav.addObject("list", pageList.get(PAGING.RES_KEY_ROWS));
		mav.addObject("total", pageList.get(PAGING.RES_KEY_TOTAL_COUNT));
		mav.addObject("paging", paging);
		
		mav.setViewName(VIEW_ROOT_PC + "/provider/settlement_details");
		
		return mav;

	}
	
	@RequestMapping(value="/settlementInsert.do")
	public void settleInsert(HttpServletRequest request){
		
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			providerService.insertSettleTx(param);	
		} catch (Exception e) {
			logger.error(e.toString());
		}
		
	}
	
	@RequestMapping(value="settlementExcel")
	public String settleExcel(HttpServletRequest request,HttpServletResponse response, Map<String, Object> ModelMap, HttpSession session) throws Exception {
		
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		String cPage = ParamUtils.getString(param, "pages", "1");

		int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);

		param.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
		param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
		
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		List<String> excelTitle = new ArrayList<String>();
		List<String> excelTitleName = new ArrayList<String>();
		
		String[] header = {
											"rccorp_name",
											"is_direct",
											"settl_sdate",
											"settl_edate",
											"settl_cycle",
											"settl_final_price",
											"corp_bank_name",
											"corp_bank_num",
											"corp_bank_holder",
											"zimcar_fee",
											"rentcar_price",
											"insu_price",
											"cancel_rentcar_price",
											"cancel_insu_price",
											"settl_rentcar_sum_price",
											"extra_charge",
											"cancel_extra_charge",
											"settl_extracharge_price",
											"settl_refund_price",
											"rate_settl_rentcar",
											"rate_settl_extra_charge",
											"rate_cancel_in_rent24",
											"rate_bankruptcy_booking",
											"settl_consign_price",
											"settl_status",
											"settl_mem_pid",
											"settl_dtime"
						  };
		
		String[] headerName = {
								"렌트카명",
								"결제사이트",
								"정산시작일",
								"정산종료일",
								"정산주기",
								"총 정산금액",
								"은행명",
								"계좌번호",
								"계좌명",
								"찜카수수료(마진)",
								"차량대여료",
								"차량보험료",
								"취소차량 대여료",
								"취소차량 보험료",
								"대여료 정산금액",
								"할증료",
								"취소 할증료",
								"할증료 정산금액",
								"환불금액 합계",
								"대여료 정산율",
								"할증금 정산율",
								"대여전 24시간 취소 정산율",
								"예약부도 정산율",
								"대여/반납 탁송비",
								"처리상태",
								"처리담당자",
								"처리일시"
							   };
		
		for(int num = 0; num < headerName.length; num++) {
			excelTitle.add(header[num]);
			excelTitleName.add(headerName[num]);
		}
		
		List<Map<String, Object>> excelList= providerService.selectSettlementListExcel(param);
		
		ModelMap.put("fileName", sdf.format(today).toString()+"_정산리스트");
		ModelMap.put("sheetName", "정산리스트");
		ModelMap.put("excelTitle", excelTitle);
		ModelMap.put("excelTitleName", excelTitleName);
        ModelMap.put("excelList", excelList); 
        
        // 인보이스 처리
        String inVoceList ="";
        List<Map<String, Object>> list = providerService.selectSettleInVoceList(param);
        
        for(int i=0; i<list.size(); i++) {
        	inVoceList += ","+list.get(i).get("settlementPid");
        }
        
        inVoceList = inVoceList.startsWith(",") ? inVoceList.substring(1) : inVoceList;
        
        param.put("settl_mem_pid", session.getAttribute("mem_pid"));
        param.put("settlStatus", "01");
        param.put("settleOkList", inVoceList);
        
		providerService.updateSettlementTx(param);
        
		return "excelView";
	}
	
	@RequestMapping(value="settleDetailExcel")
	public String settleDetailExcel(HttpServletRequest request,HttpServletResponse response, Map<String, Object> ModelMap) throws Exception {
		
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		String cPage = ParamUtils.getString(param, "pages", "1");

		int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);

		param.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
		param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
		
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		List<String> excelTitle = new ArrayList<String>();
		List<String> excelTitleName = new ArrayList<String>();
		
		
		String[] header = {
							"rccorp_name",
							"booking_sdtime",
							"booking_edtime",
							"price_sale_zimcar",
							"price_sale",
							"price_insu_sale",
							"cancel_price_sale",
							"cancel_price_insu_sale",
							"extracharge_price",
							"cancel_extracharge_price",
							"price_consign_sum",
							"order_refund_sett_price"
						  };
		
		String[] headerName = {
								"렌트카명",
								"예약 시작일",
								"예약 종료일",
								"찜카 실판매가격",
								"차량대여료",
								"보험대여료",
								"취소차량 대여료",
								"취소차량 보험료",
								"할증금액",
								"취소 할증금액",
								"대여/반납 탁송비",
								"환불 금액"
							  };
		
		for(int num = 0; num < headerName.length; num++) {
			excelTitle.add(header[num]);
			excelTitleName.add(headerName[num]);
		}
		
		List<Map<String, Object>> excelList= providerService.selectSettlementDetailListExcel(param);
		
		ModelMap.put("fileName", sdf.format(today).toString()+"_정산상세리스트");
		ModelMap.put("sheetName", "정산상세리스트");
		ModelMap.put("excelTitle", excelTitle);
		ModelMap.put("excelTitleName", excelTitleName);
        ModelMap.put("excelList", excelList); 
        
		return "excelView";
	}
	
	@RequestMapping(value="settleOk")
	public @ResponseBody void settleOkList(HttpServletRequest request,HttpServletResponse response, HttpSession session) throws Exception{
		
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		param.put("settl_mem_pid", session.getAttribute("mem_pid"));
		param.put("settlStatus", "99");
		providerService.updateSettlementTx(param);
	}
	
}