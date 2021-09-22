package com.zzimcar.admin.base;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.zzimcar.admin.config.ZzimcarConstants;
import com.zzimcar.admin.dao.PushDao;
import com.zzimcar.admin.push.vo.PushItem;
import com.zzimcar.admin.service.ZzimcarSampleServiceImpl;
import com.zzimcar.admin.dao.MemberPointDao;

public abstract class ZzimcarService {
	
	protected final Logger logger = LoggerFactory.getLogger(ZzimcarSampleServiceImpl.class);

	private ZzimcarDao dao;
	
	@Resource(name = "PushDao")
	private PushDao pushDao;
	
	@Resource(name = "MemberPointDao")
	private MemberPointDao pointDao;

	public ZzimcarService() {}
    public ZzimcarService(ZzimcarDao dao) {
        this.dao = dao;
    }
    
	public void insert(Map<String, Object> entity) {
		dao.insert(entity);
	}
	public void insert(String queryId, Map<String, Object> entity) {
		dao.insert(queryId, entity);
	}

	public void update(Map<String, Object> entity) {
		dao.updateByPk(entity);
	}
	public void update(String queryId, Map<String, Object> entity) {
		dao.updateByPk(queryId, entity);
	}

	public void delete(Map<String, Object> entity) {
		dao.deleteByPk(entity);
	}
	public void delete(String queryId, Map<String, Object> entity) {
		dao.deleteByPk(queryId, entity);
	}

	public Map<String, Object> selectByPk(int pid) {
		return dao.selectByPk(pid);
	}
	public Map<String, Object> selectByPk(String queryId, int pid) {
		return dao.selectByPk(queryId, pid);
	}

	public List<Map<String, Object>> selectAll() {
		return dao.selectAll();
	}

	public Map<String, Object> selectOne(String queryId, Map<String, Object> mp) {
		return dao.selectOne(queryId, mp);
	}
	
	public Map<String, Object> search(String queryId, Map<String, Object> mp) {
		return dao.search(queryId, mp);
	}

	public Map<String, Object> fileupload(MultipartHttpServletRequest request, String folderName)throws Exception {
		//리턴 맵
		Map<String, Object> map = new HashMap<>();
		//맵 이름
		String filepath = request.getSession().getServletContext().getRealPath("/resources/images/");
		
		//String backupPath = "C:/fileBackUp";
		// 파일이 없는경우 이쪽경로 아이디 필수
		String userPath = filepath + "/upload/" + (folderName.equals("") || folderName == null ? "" : folderName + "/");
		// 파일이 있을경우 파일 이동
		//String fileMovePath = backupPath + "/B/";
		//실제사용할 경로
		File dir = new File(userPath);
		//임시 보관소
		//File subDir = new File(fileMovePath);
		try {
			// Base64 인코더 
			Encoder base64Encoder = Base64.getEncoder();
			
			//폴더 없을경우 생성
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
			//임시폴더 없을경우 생성
			/*if (!subDir.isDirectory()) {
				subDir.mkdirs();
			}*/
			
			//다중 파일 List로 형변환
			List<MultipartFile> files = (List<MultipartFile>) request.getFiles("imgList");
			//파일 있을경우에만 처리
			if (files != null && !files.get(0).getOriginalFilename().equals("")) {
				//파일 있는대로처리
				for (int i = 0; i < files.size(); i++) {
					//오리지날 이름
					String orgName = files.get(i).getOriginalFilename();
					String exc = orgName.substring(orgName.lastIndexOf(".")+1, orgName.length());
					
					String fileName = orgName.replace("."+exc,"");
					byte[] fileNameByte = fileName.getBytes("UTF-8");
					
					fileName = base64Encoder.encodeToString(fileNameByte);
					
					// Base64 인코딩 후 URL에 지장되는 +, = 제거
					orgName = fileName.replace("+", "").replace("=", "") + "."+exc;
					
					Date date = new Date();
					SimpleDateFormat transDate = new SimpleDateFormat("yyyyMMddHHmmss",Locale.KOREA);
					String today = transDate.format(date);
					
					//저장할 이름
					String saveName = today +"_"+ request.getSession().getAttribute("mem_pid")+"_" + orgName;
					System.out.println(saveName + " : " + saveName.length());
					
					//파일이 존재하면 이동시킴
					/*if (new File(userPath + saveName).exists()) {

						FileInputStream fis = new FileInputStream(userPath + saveName);
						FileOutputStream fos = new FileOutputStream(new File(fileMovePath+"_" + System.currentTimeMillis()+saveName));
						int filedata = 0;
						while ((filedata = fis.read()) != -1) {
							fos.write(filedata);
						}
						fis.close();
						fos.close();
						
						 File deleteFile = new File(userPath + saveName);
						  
						 deleteFile.delete();
					}*/
					//파일 저장
					String imgPath = "/images/upload/" + (folderName.equals("") || folderName == null ? "" : folderName + "/");
					files.get(i).transferTo(new File(userPath + saveName));
					//백업 이미지
					
					//fileMovePath = fileMovePath +today+request.getSession().getAttribute("mem_id");
					//files.get(i).transferTo(new File(fileMovePath + saveName));
					//리턴 맵 설정
					filepath = "filepath"+i;
					map.put(filepath, imgPath + saveName);	

				} // for
		
			} // if

		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
			e.printStackTrace();
			map.put("filepath", "fail");
			
		}

		return map;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean sendPushMessageToGoogle(PushItem item) {
		boolean result_send = false;
		String json = new Gson().toJson(item).toString();
		System.out.println("push : " + json);
		
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Authorization","key="+ ZzimcarConstants.APIKEY.FIREBASE_CLOUD_MESSAGE_API_KEY);
			
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			
			HttpEntity request;
			request = new HttpEntity(json, headers);
			ResponseEntity<String> result_http = restTemplate.exchange("https://fcm.googleapis.com/fcm/send", HttpMethod.POST,request,String.class);
			
			Type type = new TypeToken<Map<String, Object>>(){}.getType();
			System.out.println("result : " + result_http.getBody());
			Map<String, Object> map = new Gson().fromJson(result_http.getBody(), type);
			if(map.get("success") != null) {
				Double success = (Double) map.get("success");
 				if(Integer.parseInt(String.valueOf(Math.round(success))) == 1) {
					result_send = true;
				}
			} else if(map.get("message_id") != null) {
				result_send = true;
			}
			if(map.get("results") !=null) {
				ArrayList<LinkedTreeMap> results = (ArrayList<LinkedTreeMap>) map.get("results");
				if(results != null) {
					String error = item.getPush_error_msg();
					
					for(int i=0; i<results.size(); i++) {
						if(results.get(i).get("error") !=null) {
							if(!error.equals("")) {
								error += ", ";
							}
							
							String code = String.valueOf(results.get(i).get("error")); 
							switch (code) {
							case "InvalidRegistration":
								error += "알수없는 토큰값";
								break;

							default:
								error += code;
								break;
							}
						}
					}
					
					item.setPush_error_msg(error);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("send : " + String.valueOf(result_send));
				
		return result_send;
	}
	
	@SuppressWarnings("unchecked")
	public boolean sendPushMessage(Map<String, Object> data) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean result = false;
		PushItem item = new PushItem();
		
		item.setData(data);
		
		if(data.get("push_android") != null && data.get("push_android").equals("on")) {
			item.setAndroid_send(true);
		} else {
			item.setAndroid_send(false);
		}
		
		if(data.get("push_ios") != null && data.get("push_ios").equals("on")) {
			item.setiOS_send(true);
		} else {
			item.setiOS_send(false);
		}
		
		if(data.get("push_topic")!= null && !data.get("push_topic").equals("") && data.get("push_topic") instanceof String) {
			item.setTopic(String.valueOf(data.get("push_topic")));
			if(item.isAndroid_send()) {
				PushItem Android = item.makeAndroid();
				boolean androidSend = sendPushMessageToGoogle(Android);
				
				result = (result == false) ? androidSend : true;
			}
			
			if(item.isiOS_send()) {
				PushItem IOS = item.makeIOS();
				boolean iosSend = sendPushMessageToGoogle(IOS);
				
				result = (result == false) ? iosSend : true;
			}
		} else if(data.get("push_mem_pids")!= null && data.get("push_mem_pids") instanceof List) {
			String ids = "";
			for(Integer num : (List<Integer>)data.get("push_mem_pids")) {
				if(!ids.equals("")) {
					ids += ", ";
				}
				ids += String.valueOf(num);
			}
			item.setPush_mem_pids(ids);
			try {
				if(item.isAndroid_send()) {
					ArrayList<String> tokensAndroid = pushDao.searchMemberTokensAndroid(ids);
					if(tokensAndroid.size() > 0) {
						PushItem Android = item.makeAndroid();
						Android.setSetTokens(tokensAndroid);
						
						boolean androidSend = sendPushMessageToGoogle(Android);
						result = (result == false) ? androidSend : true;
						
						item.setPush_error_msg(Android.getPush_error_msg());
					}
				}
				
				if(item.isiOS_send()) {
					ArrayList<String> tokensIOS = pushDao.searchMemberTokensIOS(ids);
					if(tokensIOS.size() > 0) {
						PushItem IOS = item.makeIOS();
						IOS.setSetTokens(tokensIOS);
						
						boolean ios_result = sendPushMessageToGoogle(IOS);
						result = (result == false) ? ios_result: true;
						
						item.setPush_error_msg(item.getPush_error_msg());
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else if(data.get("push_mem_pids")!= null && !data.get("push_mem_pids").equals("") && data.get("push_mem_pids") instanceof String) {
			item.setPush_mem_pids(String.valueOf(data.get("push_mem_pids")));
			try {
				if(item.isAndroid_send()) {
					ArrayList<String> tokensAndroid = pushDao.searchMemberTokensAndroid(String.valueOf(data.get("push_mem_pids")));
	
					if(tokensAndroid.size() >0) {
						PushItem Android = item.makeAndroid();
						Android.setSetTokens(tokensAndroid);
						boolean android_result = sendPushMessageToGoogle(Android);
						
						result = (result == false) ? android_result: true;
						
						item.setPush_error_msg(Android.getPush_error_msg());
					}
				}
				
				if(item.isiOS_send()) {
					ArrayList<String> tokensIOS = pushDao.searchMemberTokensIOS(String.valueOf(data.get("push_mem_pids")));
					if(tokensIOS.size()>0) {
						PushItem IOS = item.makeIOS();
						IOS.setSetTokens(tokensIOS);
						boolean ios_result = sendPushMessageToGoogle(IOS);
						result = (result == false) ? ios_result: true;
						
						item.setPush_error_msg(IOS.getPush_error_msg());
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
				 
		map = new HashMap<String, Object>();
		map.put("reg_mem_pid", String.valueOf(data.get("mem_pid")));
		map.put("push_topic", item.getTopic());
		map.put("push_mem_pids", item.getPush_mem_pids());
		
		map.put("push_title", item.getData().get(PushItem.DataColumn.TITLE.getName()));
		map.put("push_msg", item.getData().get(PushItem.DataColumn.MESSAGE.getName()));
		map.put("push_url", item.getData().get(PushItem.DataColumn.URL.getName()));
		map.put("push_img_url", item.getData().get(PushItem.DataColumn.IMAGESRC.getName()));
		map.put("push_result", result);
		map.put("push_android", item.isAndroid_send() ? "Y" : "N");
		map.put("push_ios", item.isiOS_send() ? "Y" : "N");
		map.put("push_error_msg", item.getPush_error_msg());
		
		pushDao.insert(map);
		
 		return result;
	}
	
	public boolean plusMemberPoint( int memPid, int rcorderPid, int point, String detail, String pointMethod) {
		String inoutType = "1";
		return setMemberPoint(memPid, rcorderPid, inoutType, point, detail, pointMethod);
	}
	
	public boolean plusDirectPoint( int memPid, int rcorderPid, int rccorpPid, int point, String detail, String pointMethod) {
		String inoutType = "1";
		return setDirectPoint(memPid, rcorderPid, rccorpPid, inoutType, point, detail, pointMethod);
	}
	
	// ########################################################################
	// 포인트 가감
	public boolean setMemberPoint( int memPid, int rcorderPid, String inoutType, int point, String detail , String pointMethod) {

		Map<String, Object> memPointHistory = new HashMap<String, Object>();

		try {
			// 회원 포인트 입출금 이력 기록
			memPointHistory.put( "memPid"			, memPid );
			memPointHistory.put( "rcorderPid"		, rcorderPid );
			memPointHistory.put( "pointInoutType"	, inoutType );	// 포인트 입출금 타입( 0:출금, 1:입금, 2:소멸 )
			memPointHistory.put( "pointAmount"		, point );
			memPointHistory.put( "pointDetail"		, detail );
			memPointHistory.put( "pointMethod"		, pointMethod );
			
			pointDao.insert("insert_member_point_history", memPointHistory);

			
			// 회원 포인트 계산
			Map<String, Object> memPointMap = new HashMap<String, Object>();
			int memPoint = inoutType.equals("1")?point:(-1*point);
			memPointMap.put("memPoint"	, memPoint);
			memPointMap.put("memPid"	, memPid );
			
			pointDao.updateByPk("update_member_point", memPointMap );
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	// ########################################################################
	// 다이렉트 포인트 가감
	public boolean setDirectPoint( int memPid, int rcorderPid, int rccorpPid, String inoutType, int point, String detail , String pointMethod) {

		Map<String, Object> memPointHistory = new HashMap<String, Object>();

		try {
			// 회원 포인트 입출금 이력 기록
			memPointHistory.put( "memPid"			, memPid );
			memPointHistory.put( "rcorderPid"		, rcorderPid );
			memPointHistory.put( "rccorpPid"		, rccorpPid );
			memPointHistory.put( "pointInoutType"	, inoutType );	// 포인트 입출금 타입( 0:출금, 1:입금, 2:소멸 )
			memPointHistory.put( "pointAmount"		, point );
			memPointHistory.put( "pointDetail"		, detail );
			memPointHistory.put( "pointMethod"		, pointMethod );
			
			pointDao.insert("insert_direct_point_history", memPointHistory);

			
			// 회원 포인트 계산
			Map<String, Object> memPointMap = new HashMap<String, Object>();
			int memPoint = inoutType.equals("1")?point:(-1*point);
			memPointMap.put("memPoint"	, memPoint);
			memPointMap.put("memPid"	, memPid );
			memPointMap.put( "rccorpPid", rccorpPid );
			
			pointDao.updateByPk("update_direct_point", memPointMap );
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
