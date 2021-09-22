package com.zzimcar.admin.push.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class PushItem implements Cloneable{
	@SerializedName("to")
	private String topic;
	
	@SerializedName("registration_ids")
	private ArrayList<String> tokens;
	
	private Map<String, Object> data;
	
	@Expose
	private Map<String, Object> notification;
	
	@Expose
	private String reg_mem_pid;
	
	@Expose
	private String push_error_msg = "";
	
	@Expose
	private String push_mem_pids;
	
	private boolean android_send = true;
	private boolean iOS_send = true;
	
	public PushItem makeAndroid() {
		PushItem clone = null;
		try {
			clone = (PushItem) this.clone();
			if(clone.topic != null) {
				clone.topic += "_android";
			}
			clone.notification =null;
		} catch(Exception e) {
			
		}
		
		return clone;
	}
	
	public PushItem makeIOS() {
		PushItem clone = null;
		try {
			clone = (PushItem) this.clone();
			if(clone.topic != null) {
				clone.topic += "_ios";
			}
		} catch(Exception e) {
			
		}
		
		return clone;
	}
	
	public enum DataColumn {
		TITLE("title"),
		MESSAGE("msg"),
		URL("url"),
		IMAGESRC("imgsrc");
		
		private String name;
		DataColumn(String col){
			this.name = col;
		}
		
		public String getName() {
			return name;
		}
	}
	
	public enum NotificationColumn {
		TITLE("title"),
		BODY("body"),
		URL("url"),
		IMAGESRC("imgsrc");
		
		private String name;
		NotificationColumn(String col){
			this.name = col;
		}
		
		public String getName() {
			return name;
		}
	}
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = "/topics/"+topic;
		this.tokens = null;
	}
	public ArrayList<String> getTokens() {
		return tokens;
	}
	public void setSetTokens(ArrayList<String> tokens) {
		this.tokens = tokens;
		this.topic = null;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
		this.notification = new HashMap<String, Object>();
		this.notification.put(NotificationColumn.TITLE.getName(), data.get(DataColumn.TITLE.getName()));
		this.notification.put(NotificationColumn.BODY.getName(), data.get(DataColumn.MESSAGE.getName()));
		this.notification.put(NotificationColumn.URL.getName(), data.get(DataColumn.URL.getName()));
		this.notification.put(NotificationColumn.IMAGESRC.getName(), data.get(DataColumn.IMAGESRC.getName()));
	}
	
	@SuppressWarnings("unchecked")
	public void setTo(Object object) {
		if(object instanceof String) {
			this.setTopic(object.toString());
		} else if(object instanceof ArrayList) {
			this.setSetTokens((ArrayList<String>) object);
		}
	}
	
	public String getReg_mem_pid() {
		return reg_mem_pid;
	}
	public void setReg_mem_pid(String reg_mem_pid) {
		this.reg_mem_pid = reg_mem_pid;
	}
	public String getPush_error_msg() {
		return (push_error_msg == null ? "" : push_error_msg);
	}
	public void setPush_error_msg(String push_error_msg) {
		this.push_error_msg = push_error_msg;
	}
	public String getPush_mem_pids() {
		return push_mem_pids;
	}
	public void setPush_mem_pids(String push_mem_pids) {
		this.push_mem_pids = push_mem_pids;
	}

	public boolean isAndroid_send() {
		return android_send;
	}

	public void setAndroid_send(boolean android_send) {
		this.android_send = android_send;
	}

	public boolean isiOS_send() {
		return iOS_send;
	}

	public void setiOS_send(boolean iOS_send) {
		this.iOS_send = iOS_send;
	}
}
