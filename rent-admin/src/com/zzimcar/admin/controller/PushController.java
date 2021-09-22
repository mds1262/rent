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
import com.zzimcar.admin.config.ZzimcarConstants.PUSH_LOG_TYPE;
import com.zzimcar.admin.push.vo.PushItem;
import com.zzimcar.admin.service.PushService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.RequestParameterMapUtils;

@Controller
@RequestMapping("/push")
public class PushController extends ZzimcarController{
	@Resource(name = "PushService")
	private PushService pushService;
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/list.do")
	public ModelAndView list(HttpServletRequest request, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(VIEW_ROOT_PC + "/push/list");

		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			String cPage = ParamUtils.getString(param, "pages", "1");

			int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);

			param.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
			param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
			
			if(param.get("scType") !=null && param.get("scType").equals("push_mem_names")) {
				Map<String, Object> newMap = new HashMap<>();
				newMap.put("searchWord", param.get("scValue"));
				List<Map<String, Object>> list = pushService.getMember(newMap);
				
				String ids = "";
				for(int i=0; i<list.size(); i++) {
					if(i != 0) {
						ids += ", ";
					}
					ids += list.get(i).get("memPid");
				}
				if(!ids.equals("")) {
					param.put("scValue", ids);
				}
			}

			Map<String, Object> list = pushService.getList(param);
			List<Map<String, Object>> list_after = new ArrayList<Map<String, Object>>();
			
			for (Map<String, Object> map : (List<Map<String, Object>>) list.get(PAGING.RES_KEY_ROWS)) {
				Map<String, Object> newmap = new HashMap<>();
				for (String key : map.keySet()) {
 					if (key.equals("pushMemPids") && map.get(key) !=null) {
						Map<String, Object> mp = new HashMap<>();
						mp.put("ids", map.get(key));
						String username = pushService.getPushNames(mp);
						newmap.put("push_mem_names", username);
					} else if (key.equals("pushMemPids") && map.get(key) == null){
						newmap.put("push_mem_names", "");
					}
					newmap.put(key, map.get(key));
				}
				list_after.add(newmap);
			}
			Map<String, Object> map = new HashMap<>();
			
			List<Map<String, Object>> scTypeArr = new ArrayList<>();
			for(PUSH_LOG_TYPE code : PUSH_LOG_TYPE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				scTypeArr.add(map);
			}
			mav.addObject("scTypeArr", scTypeArr);
			
			List<Map<String, Object>> resultSendArr = new ArrayList<>();
			
			map = new HashMap<>();
			map.put("value", true);
			map.put("name", "성공");
			resultSendArr.add(map);
			
			map = new HashMap<>();
			map.put("value", false);
			map.put("name", "실패");

			resultSendArr.add(map);
			
			mav.addObject("resultSendArr", resultSendArr);

			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);
			paging.setNumberOfRecords((int) list.get(PAGING.RES_KEY_TOTAL_COUNT));
			paging.pagingUtil(cPage);     

			mav.addObject("list", list_after);
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
	
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			
			int pid = 0;
			if(param.get("selected_id") != null  && !param.get("selected_id").equals("")) {
				pid = Integer.parseInt((String) param.get("selected_id"));
			}
			Map<String, Object> item = pushService.selectByPk(pid);
			if(item != null) {
				String jsCode = new String();
				
				jsCode = "<script>\n$(document).ready(function(){";
				if(item.get("pushImgUrl") != null && !item.get("pushImgUrl").equals("")) {
					jsCode += "\n\t $('#img_p').css('display', 'block');";
				}
				jsCode += "\n})\n</script>";
				mav.addObject("jsCode", jsCode);
			}
			mav.addObject("pid", pid);
			
			mav.addObject("item", item);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mav.setViewName(VIEW_ROOT_PC + "/push/write");
        return mav;
	}
	
	@ResponseBody
	@RequestMapping(value="/pushfileUpload.do", method=RequestMethod.POST)
	public void pushfileUpload(MultipartHttpServletRequest request,HttpServletResponse response) throws Exception {
		response.setContentType("text/plain; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> fileresult = null;
		String result ="";
		// 파일업로드
		String fileName = request.getFiles("imgList").get(0).getOriginalFilename();
		if (fileName != null && !(fileName.equals(""))) {
  			fileresult = pushService.fileupload(request, "push");
			result =  gson.toJson(fileresult);
 		}
		else {
			result = gson.toJson("N");
		}
		print.append(result);
	}
	
	@ResponseBody
	@RequestMapping(value="/retryPush.json", method=RequestMethod.POST)
	public Map<String, Object> retryPush(HttpServletRequest request,HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		
		int pid = 0;
		if(param.get("pid") != null) {
			pid = Integer.parseInt((String) param.get("pid"));
		}
		Map<String, Object> item = pushService.selectByPk(pid);
		Map<String, Object> sendItem = new HashMap<>();
		
		if(item.get("pushMemPids") == null &&  item.get("pushTopic") == null) {
			result.put("result", false);
			return result;
		}
		
		sendItem.put("push_mem_pids", item.get("pushMemPids"));
		sendItem.put("push_topic", item.get("pushTopic"));
		sendItem.put(PushItem.DataColumn.TITLE.getName(), item.get("pushTitle"));
		sendItem.put(PushItem.DataColumn.MESSAGE.getName(), item.get("pushMsg"));
		sendItem.put(PushItem.DataColumn.URL.getName(), item.get("pushUrl"));
		sendItem.put(PushItem.DataColumn.IMAGESRC.getName(), item.get("pushImgUrl"));
		sendItem.put("push_android", item.get("pushAndroid").equals("Y") ? "on" : "off");
		sendItem.put("push_ios", item.get("pushIos").equals("Y") ? "on" : "off");
		sendItem.put("mem_pid", session.getAttribute("mem_pid"));
		
		boolean pushResult= pushService.sendPushMessage(sendItem);
		
		result.put("result", pushResult);
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/sendPush.do", method=RequestMethod.POST)
	public Map<String, Object> sendPush(HttpServletRequest request,HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		param.put("mem_pid", session.getAttribute("mem_pid"));
		
		boolean pushResult= pushService.sendPushMessage(param);
		
		result.put("result", pushResult);
		
		return result;
	}
	
	@RequestMapping(value = "/getMember.json")
	public @ResponseBody Map<String, Object> getMember(HttpServletRequest request, HttpSession session)
			throws Exception {
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = pushService.getMember(param);
		result.put("list", list);
		return result;
	}
}
