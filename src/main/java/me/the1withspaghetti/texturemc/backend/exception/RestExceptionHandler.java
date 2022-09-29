package me.the1withspaghetti.texturemc.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import me.the1withspaghetti.texturemc.backend.endpoints.clientbound.ErrorResponse;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		return new ResponseEntity<Object>(new ErrorResponse(ex.getFieldError().getDefaultMessage()), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<Object> handleApiException(ApiException ex) {
		ex.printStackTrace();
		return new ResponseEntity<Object>(new ErrorResponse(ex.getMessage()), ex.status);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleException(Exception ex) {
		ex.printStackTrace();
		return new ResponseEntity<Object>(new ErrorResponse("Internal Server Error, if this repeats contact support"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
