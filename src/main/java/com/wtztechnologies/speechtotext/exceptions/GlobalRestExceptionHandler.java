package com.wtztechnologies.speechtotext.exceptions;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        log.error(ex.getClass().getSimpleName() + ": ", ex);
        ApiResponse error = ApiResponse.builder()
                .timestamp(Instant.now())
                .success(false)
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .details(extractDetails(ex))
                .build();
        return new ResponseEntity<>(error, error.getStatus());
    }

   
  

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        log.error(ex.getClass().getSimpleName() + ": ", ex);
        ApiResponse error = ApiResponse.builder()
                .timestamp(Instant.now())
                .success(false)
                .status(HttpStatus.FORBIDDEN)
                .details(extractDetails(ex))
                .build();
        return new ResponseEntity<>(error, error.getStatus());

    }

    

    @ExceptionHandler({SpeechPlatformException.class})
    public ResponseEntity<ApiResponse> handleHiraPlatformException(SpeechPlatformException ex) {
        log.error(ex.getClass().getSimpleName() + ": ", ex);
        ApiResponse error = ApiResponse.builder()
                .timestamp(Instant.now())
                .success(false)
                .status(ex.getStatus())
                .details(extractDetails(ex))
                .build();
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        log.error(ex.getClass().getSimpleName() + ": ", ex);
        ApiResponse error = ApiResponse.builder()
                .timestamp(Instant.now())
                .success(false)
                .status(HttpStatus.BAD_REQUEST)
                .details(extractDetails(ex))
                .build();
        return new ResponseEntity<>(error, error.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        log.error(ex.getClass().getSimpleName() + ": ", ex);
        List<ObjectError> errors = ex.getBindingResult()
                .getAllErrors();
        List<String> details = errors
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        ApiResponse error = ApiResponse.builder()
                .timestamp(Instant.now())
                .success(false)
                .status(HttpStatus.BAD_REQUEST)
                .details(details)
                .build();
        return new ResponseEntity<>(error, error.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex.getClass().getSimpleName() + ": ", ex);
        ApiResponse error = ApiResponse.builder()
                .timestamp(Instant.now())
                .success(false)
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .details(extractDetails(ex))
                .build();

        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
        }

        return new ResponseEntity<>(error, headers, error.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        return handleException(ex);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        return handleException(ex);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex,
                                                         HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleException(ex);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return handleException(ex);
    }

    private List<String> extractDetails(Exception... exceptions) {
        if (exceptions == null || exceptions.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.stream(exceptions)
                .map(ex -> extractMessage(ex))
                .collect(Collectors.toList());
    }

    private String extractMessage(Exception ex) {
        if (ex.getLocalizedMessage() != null) {
            return ex.getLocalizedMessage();
        }
        return ex.getClass().getSimpleName() + " was thrown!";
    }
}
