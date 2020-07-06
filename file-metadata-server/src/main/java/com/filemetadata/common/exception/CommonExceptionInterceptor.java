package com.filemetadata.common.exception;

/**
 * Interface that should be implemented based on the requirement to do something with the handled exception
 *
 * @author Vinod Kandula
 */
public interface CommonExceptionInterceptor {

    void handle(CommonErrorResponse errorResponse, Exception exception);
}
