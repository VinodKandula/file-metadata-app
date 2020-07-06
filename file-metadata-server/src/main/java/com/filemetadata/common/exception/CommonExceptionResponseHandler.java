package com.filemetadata.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Common Exception Response Handler is responsible for handling all exceptions from down-stream apps by responding with a {@link CommonErrorResponse}
 *
 * @author Vinod Kandula
 */
@ControllerAdvice
@RequestMapping(produces = "application/json")
@Slf4j
public class CommonExceptionResponseHandler {

    @Autowired
    private CommonExceptionConfig exConfig;

    ObjectMapper mapper = new ObjectMapper();

    // 400 -
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(),
                HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), request.getDescription(false));
        errorResponse.addValidationErrors(ex.getBindingResult().getFieldErrors());
        errorResponse.addValidationError(ex.getBindingResult().getGlobalErrors());

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // 400 -
    @ExceptionHandler(ServletRequestBindingException.class)
    public final ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException  ex, WebRequest request) {
        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(),
                HttpStatus.BAD_REQUEST,
                ex.getMessage(), request.getDescription(false));

        errorResponse.addParams(mapParamsFromRequest(request.getParameterMap()));
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ BindException.class })
    protected ResponseEntity<Object> handleBindException(final BindException ex, final WebRequest request) {
        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(),
                HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), request.getDescription(false));
        errorResponse.addValidationErrors(ex.getBindingResult().getFieldErrors());
        errorResponse.addValidationError(ex.getBindingResult().getGlobalErrors());
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ WebExchangeBindException.class })
    protected ResponseEntity<Object> handleWebExchangeBindExceptionException(final WebExchangeBindException ex,
                                                                             final WebRequest request) {
        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(),
                HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), request.getDescription(false));
        errorResponse.addValidationErrors(ex.getBindingResult().getFieldErrors());
        errorResponse.addValidationError(ex.getBindingResult().getGlobalErrors());
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ TypeMismatchException.class })
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, WebRequest request) {
        String message = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type "
                + ex.getRequiredType();

        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(),
                HttpStatus.BAD_REQUEST, message, ex.getLocalizedMessage(), request.getDescription(false));

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex,
                                                                   final WebRequest request) {
        String message = ex.getName() + " should be of type " + ex.getRequiredType().getName();

        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(),
                HttpStatus.BAD_REQUEST, message, ex.getLocalizedMessage(), request.getDescription(false));
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ MissingServletRequestPartException.class })
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex,
                                                                     final WebRequest request) {

        final String message = ex.getRequestPartName() + " part is missing";
        final CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(),
                HttpStatus.BAD_REQUEST, message, ex.getLocalizedMessage(), request.getDescription(false));
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ MissingServletRequestParameterException.class })
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            final MissingServletRequestParameterException ex, final WebRequest request) {

        final String message = ex.getParameterName() + " parameter is missing";
        final CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(),
                HttpStatus.BAD_REQUEST, message, ex.getLocalizedMessage(), request.getDescription(false));

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public final ResponseEntity<Object> handleServerWebInputException(ServerWebInputException ex, WebRequest request) {
        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(), ex.getStatus(),
                ex.getLocalizedMessage(), request.getDescription(false));
        errorResponse.addParams(mapParamsFromRequest(request.getParameterMap()));
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // 405 -
    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            final HttpRequestMethodNotSupportedException ex, final WebRequest request) {
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(),
                HttpStatus.METHOD_NOT_ALLOWED, builder.toString(), ex.getLocalizedMessage(),
                request.getDescription(false));
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 405 -
    @ExceptionHandler({ MethodNotAllowedException.class })
    protected ResponseEntity<Object> handleMethodNotAllowedException(final MethodNotAllowedException ex,
                                                                     final WebRequest request) {
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getHttpMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedMethods().forEach(t -> builder.append(t + " "));

        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(),
                HttpStatus.METHOD_NOT_ALLOWED, builder.toString(), ex.getLocalizedMessage(),
                request.getDescription(false));
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 415
    @ExceptionHandler({ HttpMediaTypeNotSupportedException.class })
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex,
                                                                     final WebRequest request) {
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));

        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex.getLocalizedMessage(),
                request.getDescription(false));
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // 415
    @ExceptionHandler({ UnsupportedMediaTypeStatusException.class })
    protected ResponseEntity<Object> handleUnsupportedMediaTypeStatusException(
            final UnsupportedMediaTypeStatusException ex, final WebRequest request) {
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));

        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex.getLocalizedMessage(),
                request.getDescription(false));
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<Object> handleConflict(IllegalArgumentException ex, WebRequest request) {
        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(), HttpStatus.BAD_REQUEST,
                ex.getMessage(), ex.getMessage(), request.getDescription(false));

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    protected ResponseEntity<Object> handleConflict(HttpMessageNotReadableException ex, WebRequest request) {
        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(), HttpStatus.BAD_REQUEST,
                ex.getMessage(), ex.getMessage(), request.getDescription(false));

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchFileException.class)
    public final ResponseEntity<Object> handleNoSuchFileException(NoSuchFileException ex, WebRequest request) {
        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(), HttpStatus.NOT_FOUND, ex.toString(), request.getDescription(false));
        log.error("NoSuchFileException with exceptionId: "+errorResponse.getExceptionId(), ex);
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CommonBaseException.class)
    public final ResponseEntity<CommonErrorResponse> handleAllFusionExceptions(CommonBaseException ex,
                                                                                    WebRequest request) {
        ResponseStatus responseStatus = Optional
                .ofNullable(AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class))
                .orElseThrow(() -> new CommonBaseException(CommonErrorCodes.ErrorCodes.MISSING_ANNOTATION,
                        ex.getClass().getSimpleName()) {
                });
        CommonErrorResponse exceptionResponse = new CommonErrorResponse(exConfig.getAppName(),
                responseStatus.code(), ex.getMessage(), request.getDescription(false));
        exceptionResponse
                .addParams(ex.getParams().isEmpty() ? mapParamsFromRequest(request.getParameterMap()) : ex.getParams());

        if (ex.validationErrors != null) {
            exceptionResponse.addValidationErrors(ex.getValidationErrors().getFieldErrors());
            exceptionResponse.addValidationError(ex.getValidationErrors().getGlobalErrors());
        }

        exceptionResponse.setAppErrorCode(ex.getCommonErrorCode());
        exceptionResponse.setExceptionId(ex.getExceptionId());
        exceptionResponse.setCorrelationId(ex.getCorrelationId());

        if (ex.getCause() != null)
            exceptionResponse.addExceptionChain(ex.getCause());

        log(exceptionResponse, ex);
        intercept(exceptionResponse, ex);

        return new ResponseEntity<>(exceptionResponse, exceptionResponse.getHttpStatus());
    }

    // 500 -
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<CommonErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        log.error(exConfig.getAppName() + "--" + HttpStatus.INTERNAL_SERVER_ERROR + "--" + ex.getLocalizedMessage(), ex);
        CommonErrorResponse errorResponse = new CommonErrorResponse(exConfig.getAppName(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                exConfig.getFusionExceptionProperty().getValue("INTERNAL_SERVER_ERROR"), request.getDescription(false));

        errorResponse.addParams(mapParamsFromRequest(request.getParameterMap()));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> mapParamsFromRequest(Map<String, String[]> requestParams) {
        Map<String, Object> map = new HashMap<>(requestParams.size());

        for (String key : requestParams.keySet()) {
            map.put(key, requestParams.get(key));
        }

        return map;
    }

    public void log(CommonErrorResponse apiError, Exception exception) {
        try {
            log.error(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(apiError), exception);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", e);
        }
    }

    private void intercept(CommonErrorResponse exceptionResponse, Exception exception) {
        if (exConfig.getExceptionInterceptors() != null) {
            for (CommonExceptionInterceptor interceptor : exConfig.getExceptionInterceptors()) {
                interceptor.handle(exceptionResponse, exception);
            }
        }
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status) {
        return new ResponseEntity(body, headers, status);
    }

}

