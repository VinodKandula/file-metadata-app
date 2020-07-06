package com.filemetadata.common.exception;

import java.text.MessageFormat;

/**
 * Interface, provides API client consumers with meaningful error codes + descriptions.
 *
 * @author Vinod Kandula
 */
public interface CommonErrorCodes {

    /**
     * Short code which represents a business issue.
     *
     * @return String business error code.
     */
    String getCode();

    default String getErrorDescription(String code, Object...arguments) {
        return MessageFormat.format(CommonExceptionConfig.getFusionExceptionProperty().getValue(code), arguments);
    }

    public enum ErrorCodes implements CommonErrorCodes {

        MISSING_PARAMETER("MISSING_PARAMETER"),
        MISSING_ANNOTATION("MISSING_ANNOTATION");

        private String errorCode;

        ErrorCodes(String errorCode) {
            this.errorCode = errorCode;

        }

        @Override
        public String getCode() {
            return this.errorCode;
        }

    }

}


