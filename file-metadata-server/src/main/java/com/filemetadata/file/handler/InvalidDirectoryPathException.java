package com.filemetadata.file.handler;

import com.filemetadata.common.exception.CommonBaseException;
import com.filemetadata.common.exception.CommonErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Vinod Kandula
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class InvalidDirectoryPathException extends CommonBaseException {

    public InvalidDirectoryPathException(CommonErrorCodes errorCodes, Object... arguments) {
        super(errorCodes, arguments);
    }

    public InvalidDirectoryPathException(CommonErrorCodes errorCodes, Throwable ex, Object... arguments) {
        super(errorCodes, ex, arguments);
    }
}
