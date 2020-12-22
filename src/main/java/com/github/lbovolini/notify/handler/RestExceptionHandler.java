package com.github.lbovolini.notify.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    static class ApiError {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ'")
        private final ZonedDateTime timestamp;
        private final int status;
        private final String error;
        private String message;
        private final String path;
        private List<Error> errors;

        public ApiError(HttpStatus httpStatus, String path) {
            this.timestamp = ZonedDateTime.now();
            this.status = httpStatus.value();
            this.error = httpStatus.getReasonPhrase();
            this.path = path;
        }

        public ApiError(HttpStatus httpStatus, String path, List<Error> errors) {
            this(httpStatus, path);
            this.errors = errors;
        }

        public ApiError(HttpStatus httpStatus, String path, String message) {
            this(httpStatus, path);
            this.message = message;
        }

        public ZonedDateTime getTimestamp() {
            return timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public String getMessage() {
            return message;
        }

        public String getPath() {
            return path;
        }

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public List<Error> getErrors() {
            return errors;
        }
    }

    static class Error {

        private final String field;
        private final String message;

        public Error(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<Error> errorList = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach((e) -> {
            String field = ((FieldError) e).getField();
            String message = e.getDefaultMessage();
            Error error = new Error(field, message);
            errorList.add(error);
        });

        String path = ((ServletWebRequest)request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(status, path, errorList);

        return new ResponseEntity<Object>(apiError, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = Objects.requireNonNull(ex.getMessage(), "").split(":")[0];

        String path = ((ServletWebRequest)request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(status, path, message);

        return new ResponseEntity<>(apiError, status);

    }
}
