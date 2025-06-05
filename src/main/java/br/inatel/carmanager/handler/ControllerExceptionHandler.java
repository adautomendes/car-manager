package br.inatel.carmanager.handler;

import java.net.URI;
import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

import br.inatel.carmanager.exception.BrandManagerConnectionException;
import br.inatel.carmanager.exception.BrandNotFoundException;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@ControllerAdvice
@ResponseBody
@Slf4j
public class ControllerExceptionHandler
{
    @Value("${server.host}")
    private String serverHost;

    @Value("${server.port}")
    private String serverPort;

    @Value("${car-manager.error.show-stacktrace}")
    private Boolean showStackTrace;

    @ExceptionHandler(BrandNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ProblemDetail brandNotFoundException(
        BrandNotFoundException brandNotFoundException, HandlerMethod handlerMethod,
        HttpServletRequest request)
    {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                                                                       getErrorMessage(brandNotFoundException));
        problemDetail.setTitle("Brand not found");
        problemDetail.setType(URI.create(getErrorType()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        setStackTrace(problemDetail, brandNotFoundException);
        return problemDetail;
    }

    @ExceptionHandler(BrandManagerConnectionException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public ProblemDetail brandManagerConnectionException(
        BrandManagerConnectionException brandManagerConnectionException,
        HandlerMethod handlerMethod, HttpServletRequest request)
    {
        ProblemDetail problemDetail =
            ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                                             getErrorMessage(brandManagerConnectionException));
        problemDetail.setTitle("Brand manager connection error");
        problemDetail.setType(URI.create(getErrorType()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        setStackTrace(problemDetail, brandManagerConnectionException);
        return problemDetail;
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ProblemDetail invalidFormatException(InvalidFormatException invalidFormatException,
                                                HandlerMethod handlerMethod,
                                                HttpServletRequest request)
    {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                                                                       getErrorMessage(invalidFormatException));
        problemDetail.setTitle("Invalid information format");
        problemDetail.setType(URI.create(getErrorType()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        setStackTrace(problemDetail, invalidFormatException);
        return problemDetail;
    }

    @ExceptionHandler(JsonMappingException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ProblemDetail jsonMappingException(JsonMappingException jsonMappingException,
                                              HandlerMethod handlerMethod,
                                              HttpServletRequest request)
    {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                                                                       getErrorMessage(jsonMappingException));
        problemDetail.setTitle("Malformed JSON error");
        problemDetail.setType(URI.create(getErrorType()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        setStackTrace(problemDetail, jsonMappingException);
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ProblemDetail methodArgumentNotValidException(
        MethodArgumentNotValidException methodArgumentNotValidException,
        HandlerMethod handlerMethod, HttpServletRequest request)
    {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                                                                       getErrorMessage(methodArgumentNotValidException));
        problemDetail.setTitle("Validation error");
        problemDetail.setType(URI.create(getErrorType()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        setStackTrace(problemDetail, methodArgumentNotValidException);
        return problemDetail;
    }

    @ExceptionHandler(JDBCConnectionException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public ProblemDetail jDBCConnectionException(JDBCConnectionException jDBCConnectionException,
                                                 HandlerMethod handlerMethod,
                                                 HttpServletRequest request)
    {
        ProblemDetail problemDetail =
            ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                                             getErrorMessage(jDBCConnectionException));
        problemDetail.setTitle("Database connection issue");
        problemDetail.setType(URI.create(getErrorType()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        setStackTrace(problemDetail, jDBCConnectionException);
        return problemDetail;
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail nullPointerException(NullPointerException nullPointerException,
                                              HandlerMethod handlerMethod,
                                              HttpServletRequest request)
    {
        ProblemDetail problemDetail =
            ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                             getErrorMessage(nullPointerException));
        problemDetail.setTitle("If you got this error, please contact adauto.mendes@inatel.br");
        problemDetail.setType(URI.create(getErrorType()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        setStackTrace(problemDetail, nullPointerException);
        return problemDetail;
    }

    private String getErrorMessage(BindException e)
    {
        return e.getFieldErrors().stream()
                .map(o -> o.toString())
                .reduce((o1, o2) -> String.format("%s, %s", o1, o2))
                .get();
    }

    private String getErrorMessage(Exception e)
    {
        return e.getMessage();
    }

    private void setStackTrace(ProblemDetail problemDetail, Exception e)
    {
        if (showStackTrace)
        {
            problemDetail.setProperty("stackTrace", Arrays.stream(e.getStackTrace())
                                                          .map(o -> o.toString())
                                                          .reduce(
                                                              (o1, o2) -> String.format("%s, %s",
                                                                                        o1, o2))
                                                          .get());
        }
    }

    private String getErrorType()
    {
        return String.format("http://%s:%s/error/types", this.serverHost, this.serverPort);
    }
}
