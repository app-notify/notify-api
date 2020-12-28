package com.github.lbovolini.notify.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Component
@Order(HIGHEST_PRECEDENCE)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    static class ApiError {
        // !todo padronizar pattern para todas datetime
        //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        private final ZonedDateTime timestamp;
        private final int status;
        private final String error;
        private String message;
        private final String path;
        private List<Error> errors;

        public ApiError(ZonedDateTime timestamp, HttpStatus httpStatus, String message, String path, List<Error> errors) {
            this.timestamp = timestamp;
            this.status = httpStatus.value();
            this.error = httpStatus.getReasonPhrase();
            this.message = message;
            this.path = path;
            this.errors = errors;
        }

        public ApiError(HttpStatus httpStatus, String path) {
            this(ZonedDateTime.now(), httpStatus, "", path, List.of());
        }

        public ApiError(HttpStatus httpStatus, String path, List<Error> errors) {
            this(ZonedDateTime.now(), httpStatus, "", path, errors);
        }

        public ApiError(HttpStatus httpStatus, String path, String message) {
            this(ZonedDateTime.now(), httpStatus, message, path, List.of());
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
        private final String value;

        public Error(String field, String message, String value) {
            this.field = field;
            this.message = message;
            this.value = value;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::getErrorResponse);
    }

    private Mono<ServerResponse> getErrorResponse(ServerRequest request) {

        final Map<String, Object> errorAttributes = getErrorAttributes(request, ErrorAttributeOptions.defaults());

        // !todo to check
        ZonedDateTime timestamp = ((Date)errorAttributes.get("timestamp")).toInstant().atZone(ZoneId.systemDefault());
        HttpStatus httpStatus = Objects.requireNonNullElse(HttpStatus.resolve((Integer)errorAttributes.get("status")), HttpStatus.INTERNAL_SERVER_ERROR);
        String message = (String)errorAttributes.get("message");
        String path = (String)errorAttributes.get("path");

        List<Error> errors = new LinkedList<>();

        Throwable throwable = getError(request);

        if (throwable instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> constraintViolationSet = ((ConstraintViolationException)throwable).getConstraintViolations();

            for (ConstraintViolation<?> constraintViolation : constraintViolationSet) {
                String field = constraintViolation.getPropertyPath().toString();
                String errorMessage = constraintViolation.getMessage();
                String value = constraintViolation.getInvalidValue().toString();

                Error error = new Error(field, errorMessage, value);
                errors.add(error);
            }

            httpStatus = HttpStatus.BAD_REQUEST;
        }

        ApiError apiError = new ApiError(timestamp, httpStatus, message, path, errors);

        return ServerResponse.status(httpStatus).contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(apiError));
    }
}