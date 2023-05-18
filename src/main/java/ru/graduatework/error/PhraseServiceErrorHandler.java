package ru.graduatework.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.springframework.http.HttpStatus.*;

//Временно, вернуть реактивщину позже
@Slf4j
@RestControllerAdvice
public class PhraseServiceErrorHandler {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException ex) {
        log.error("CommonException: {}", ex.toString());
        return new ResponseEntity<>(ErrorResponse.builder().error(Error.builder()
                .code(ex.getCode())
                .userMessage(ex.getUserMessage())
                .techMessage(ex.getTechMessage())
                .build()).build(), ex.getHttpStatus());
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("MethodArgumentTypeMismatchException: {}", ex.toString());
        return new ResponseEntity<>(ErrorResponse.builder().error(Error.builder().code(Code.ARGUMENT_TYPE_MISMATCH).techMessage(ex.getMessage()).build()).build(), BAD_REQUEST);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error("HttpRequestMethodNotSupportedException: {}", ex.toString());
        return new ResponseEntity<>(ErrorResponse.builder().error(Error.builder().code(Code.NOT_SUPPORTED).techMessage(ex.getMessage()).build()).build(), BAD_REQUEST);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException: {}", ex.toString());
        return new ResponseEntity<>(ErrorResponse.builder().error(Error.builder().code(Code.NOT_READABLE).techMessage(ex.getMessage()).build()).build(), BAD_REQUEST);
    }


    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.error("MissingRequestHeaderException: {}", ex.toString());
        return new ResponseEntity<>(ErrorResponse.builder().error(Error.builder().code(Code.MISSING_REQUEST_HEADER).techMessage(ex.getMessage()).build()).build(), BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedErrorException(Exception ex) {
        log.error("Exception: {}", ex.toString());
        return new ResponseEntity<>(ErrorResponse.builder().error(Error.builder().code(Code.INTERNAL_SERVER_ERROR).userMessage("Внутренняя ошибка сервиса").build()).build(), INTERNAL_SERVER_ERROR);
    }
}