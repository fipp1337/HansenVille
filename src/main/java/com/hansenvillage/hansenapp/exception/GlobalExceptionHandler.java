package com.hansenvillage.hansenapp.exception;

import com.hansenvillage.hansenapp.dto.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleStore(Exception ex) {
        return ResponseEntity
                .status(400)
                .body(new ResponseError(400, ex.getMessage()));
    }

    @ExceptionHandler(FamilyException.class)
    public ResponseEntity<ResponseError> handleStore(FamilyException ex) {
        FamilyErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ResponseError(errorCode.getStatus().value(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        return badRequest(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseError> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return badRequest("Malformed JSON or incompatible request body");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseError> handleMissingParam(MissingServletRequestParameterException ex) {
        return badRequest("Required parameter '%s' is missing".formatted(ex.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String type = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        return badRequest("Parameter '%s' must be of type %s".formatted(ex.getName(), type));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ResponseError(HttpStatus.METHOD_NOT_ALLOWED.value(), ex.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseError> handleNoResource(NoResourceFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseError(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private ResponseEntity<ResponseError> badRequest(String message) {
        return ResponseEntity
                .badRequest()
                .body(new ResponseError(HttpStatus.BAD_REQUEST.value(), message));
    }
}
