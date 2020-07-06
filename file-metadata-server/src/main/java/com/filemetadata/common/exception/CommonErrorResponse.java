package com.filemetadata.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CommonErrorResponse class encapsulates enough meta-data to provide the meaningful response to clients.
 *
 * Properties that are set include:
 *
 * - Http status code
 * - Http status desc
 * - ErrorDescription: exception message.
 * - Path: the http resource that was targeted when exception was thrown.
 * - Application name - name of the service
 * - Params: original params
 * - DateTime: date/time when problem occurred.
 * - ExceptionId: unique exception ID, can be used to easily find the exception in logs.
 * - ErrorDetail (collection) : a collection of nested exceptions.
 *      Contains correlation Ids so that external service failures can be traced using this ID.
 *
 * @author Vinod Kandula
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonErrorResponse {
    private String appName;
    private HttpStatus httpStatus;
    private Integer statusCode;
    private LocalDateTime timestamp;
    private String errorDescription, cause;
    private Map<String, Object> params = new HashMap<String, Object>();
    private List<ErrorDetail> errors;
    private String path; // request URI

    private String appErrorCode, exceptionId, correlationId;

    private static final Pattern UUID_PATTERN = Pattern.compile("[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89aAbB][a-f0-9]{3}-[a-f0-9]{12}");

    private CommonErrorResponse() {
        timestamp = LocalDateTime.now();
    }

    public CommonErrorResponse(String appName, HttpStatus httpStatus, String errorDescription, String path) {
        this();
        this.appName = appName;
        this.httpStatus = httpStatus;
        this.statusCode = httpStatus.value();
        this.errorDescription = errorDescription;
        this.path = path;
        this.exceptionId = generateUUID();
    }

    public CommonErrorResponse(String appName, HttpStatus httpStatus, String errorDescription, String cause, String path) {
        this(appName, httpStatus, errorDescription, path);
        this.cause = cause;
        this.exceptionId = generateUUID();
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public void addParams(Map<String, Object> params) {
        this.params = params;
    }

    private void addErrorDetail(ErrorDetail subError) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(subError);
    }

    private void addValidationError(String object, String field, Object rejectedValue, String message) {
        addErrorDetail(new ApiValidationError(object, field, rejectedValue, message));
    }

    private void addValidationError(String object, String message) {
        addErrorDetail(new ApiValidationError(object, message));
    }

    private void addValidationError(FieldError fieldError) {
        this.addValidationError(
                fieldError.getObjectName(),
                fieldError.getField(),
                fieldError.getRejectedValue(),
                fieldError.getDefaultMessage());
    }

    //Usually when a @Valid validation fails.
    void addValidationErrors(List<FieldError> fieldErrors) {
        fieldErrors.forEach(this::addValidationError);
    }

    private void addValidationError(ObjectError objectError) {
        this.addValidationError(
                objectError.getObjectName(),
                objectError.getDefaultMessage());
    }

    void addValidationError(List<ObjectError> globalErrors) {
        globalErrors.forEach(this::addValidationError);
    }

    // nested exceptions
    void addExceptionChain(Throwable throwable) {
        List<ExceptionChain> chain = findExceptionChain(throwable);
        if (!StringUtils.isEmpty(chain) && chain.size() > 0) {
            if(this.errors == null) {
                this.errors = new ArrayList<>();
            }
            this.errors.addAll(chain);
        }
    }

    private List<ExceptionChain> findExceptionChain(Throwable throwable) {
        List<ExceptionChain> chain = new ArrayList<ExceptionChain>();

        while (throwable != null) {
            String correlationId = null;

            if(throwable instanceof CommonBaseException) {
                correlationId = ((CommonBaseException)throwable).getExceptionId();
            }
            else {
                correlationId = findCorrelation(throwable.getMessage());
            }

            chain.add(new ExceptionChain(correlationId, throwable.getMessage()));
            throwable = throwable.getCause();
        }

        return chain;
    }

    private String findCorrelation(String message) {
        if(!StringUtils.isEmpty(message)) {
            Matcher matcher = UUID_PATTERN.matcher(message);

            while (matcher.find()) {
                return matcher.group();
            }
        }

        return null;
    }

    abstract class ErrorDetail {

    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    class ApiValidationError extends ErrorDetail {
        private String object;
        private String field;
        private Object rejectedValue;
        private String message;

        ApiValidationError(String object, String message) {
            this.object = object;
            this.message = message;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class ExceptionChain extends ErrorDetail {
        private String correlationId, message;

        public ExceptionChain(String message) {
            this.message = message;
        }

        public ExceptionChain(String correlationId, String message) {
            this.correlationId = correlationId;
            this.message = message;
        }
    }
}
