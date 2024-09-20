package com.afandi.foodapi.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GenericExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
        Exception E, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request
    ) {
        if (E instanceof MissingServletRequestParameterException) {
            ErrorResponse error = new ErrorResponse(String.format(
                "Parameter '%s' is required for this request!",
                ((MissingServletRequestParameterException) E).getParameterName())
            );

            return ResponseEntity.badRequest().body(error);
        }
        return super.handleExceptionInternal(E, body, headers, statusCode, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException E) {
        ErrorResponse error = new ErrorResponse(E.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
