package me.the1withspaghetti.texturemc.backend.exception;

import me.the1withspaghetti.texturemc.backend.objects.ErrorResponse;

public class ExceptionManager {
	
	public static String getMsg(Exception e) {
		//e.printStackTrace();
		if (e instanceof ApiException) 
			return e.getMessage();
		e.printStackTrace();
		return "Internal Server Error";
	}
	
	public static ErrorResponse getRes(Exception e) {
		return new ErrorResponse(getMsg(e));
	}
}
