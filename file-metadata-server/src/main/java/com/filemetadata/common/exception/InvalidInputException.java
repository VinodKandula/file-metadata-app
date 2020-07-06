package com.filemetadata.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Vinod Kandula
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidInputException extends CommonBaseException {

    public InvalidInputException(CommonErrorCodes errorCodes, Object... arguments) {
        super(errorCodes, arguments);
    }
}
