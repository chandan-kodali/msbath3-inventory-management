package com.sl.ms.inventorymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.sl.ms.inventorymanagement.model.ErrorMessage;


@ControllerAdvice
public class GLobalExceptionHandler extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3800906887534526562L;

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> handleRunTimeException(RuntimeException exception, WebRequest request) {
		ErrorMessage errorDetails = new ErrorMessage(exception.getMessage(),
				HttpStatus.NOT_FOUND.toString().substring(0, 3));
		return new ResponseEntity(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleAllException(Exception exception, WebRequest request) {
		ErrorMessage errorDetails = new ErrorMessage(exception.getMessage(),
				HttpStatus.BAD_REQUEST.toString().substring(0, 3));
		return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
	}

}
