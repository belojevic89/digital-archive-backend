package com.projekat.digital_archive.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateDocumentException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateDocument(@org.jetbrains.annotations.NotNull DuplicateDocumentException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResponseStatusException ex ) {

        ErrorResponse error = new ErrorResponse() ;
        error.setMessage(ex.getReason());
        return  new ResponseEntity<>(error ,ex.getStatusCode()) ;
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation (MethodArgumentNotValidException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getBindingResult().getFieldError().getDefaultMessage());
        return  new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
