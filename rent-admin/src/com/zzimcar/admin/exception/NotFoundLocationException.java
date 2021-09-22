package com.zzimcar.admin.exception;

@SuppressWarnings("serial")
public class NotFoundLocationException extends Exception {
	public NotFoundLocationException(String message) {
        super(message);
    }
	
	public NotFoundLocationException() {
        super("위치를 찾을수 없습니다.");
    }
}