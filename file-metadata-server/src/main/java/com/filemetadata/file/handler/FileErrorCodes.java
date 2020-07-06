package com.filemetadata.file.handler;

import com.filemetadata.common.exception.CommonErrorCodes;

/**
 * @author Vinod Kandula
 */
public enum FileErrorCodes implements CommonErrorCodes {

    INVALID_DIRECTORY_PATH("INVALID_DIRECTORY_PATH"),
    INVALID_FILE_PATH("INVALID_FILE_PATH"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND");

    private String errorCode;

    FileErrorCodes(String errorCode) {
        this.errorCode = errorCode;

    }

    @Override
    public String getCode() {
        return this.errorCode;
    }

}
