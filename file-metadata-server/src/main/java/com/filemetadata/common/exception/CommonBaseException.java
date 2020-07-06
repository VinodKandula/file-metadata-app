package com.filemetadata.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Generic Exception that needs to be extended by other spring-boot services.
 *
 * Exception-Id is set by default to link the API exceptions with the log files
 *
 * Correlation-Id is set to map the local exceptions with the nested or external service exceptions.
 *
 *
 * @author Vinod Kandula
 */
@Getter
@Setter
public class CommonBaseException extends RuntimeException {

    private String commonErrorCode, exceptionId;
    protected String correlationId;


    protected Errors validationErrors;

    private Map<String, Object> params = new HashMap<String, Object>();

    public CommonBaseException(CommonErrorCodes errorCodes, Object...arguments) {
        super(errorCodes.getErrorDescription(errorCodes.getCode(), arguments));
        this.commonErrorCode = errorCodes.getCode();
        this.exceptionId = generateUUID();

    }

    public CommonBaseException(CommonErrorCodes errorCodes, Throwable ex, Object...arguments) {
        super(errorCodes.getErrorDescription(errorCodes.getCode(), arguments),ex);
        this.commonErrorCode = errorCodes.getCode();
        this.exceptionId = generateUUID();

    }

    public CommonBaseException(CommonErrorCodes errorCodes, Errors validationErrors, Object...arguments) {
        super(errorCodes.getErrorDescription(errorCodes.getCode(), arguments));
        this.validationErrors = validationErrors;
        this.commonErrorCode = errorCodes.getCode();
        this.exceptionId = generateUUID();

    }

    public CommonBaseException(CommonErrorCodes errorCodes, Errors validationErrors, Throwable ex, Object...arguments) {
        super(errorCodes.getErrorDescription(errorCodes.getCode(), arguments),ex);
        this.validationErrors = validationErrors;
        this.commonErrorCode = errorCodes.getCode();
        this.exceptionId = generateUUID();
    }

    protected static String updateErrorMessage(Class entity, String errorMessage) {
        return StringUtils.capitalize(entity.getSimpleName()) + " " + errorMessage;
    }

    protected static String updateErrorMessage(Class entity, Map<String, String> searchParams,
                                               String errorDescription) {
        return StringUtils.capitalize(entity.getSimpleName() + " ") + errorDescription + searchParams;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

}
