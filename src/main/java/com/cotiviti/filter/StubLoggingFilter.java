package com.cotiviti.filter;

import com.cotiviti.constants.LoggingConstant;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class StubLoggingFilter extends OncePerRequestFilter {

    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(MediaType.valueOf("text/*"), MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"), MediaType.MULTIPART_FORM_DATA);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
            printLog("", String.format(LoggingConstant.ASYNC_REQUEST_EXECUTION, request.getRequestURL(), System.currentTimeMillis() - start));
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
            printLog("", String.format(LoggingConstant.REQUEST_EXECUTION, request.getRequestURL(), System.currentTimeMillis() - start));
        }
    }

    protected void doFilterWrapped(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            beforeRequest(request, response);
            filterChain.doFilter(request, response);
        } finally {
            afterRequest(request, response);
            response.copyBodyToResponse();
        }
    }

    protected void beforeRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        if (log.isInfoEnabled()) {
            logRequestHeader(request, request.getRemoteAddr() + "|>");
        }
    }

    protected void afterRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        if (log.isInfoEnabled()) {
            logRequestBody(request, request.getRemoteAddr() + "|>");
            logResponse(response, request.getRemoteAddr() + "|>");
        }
    }

    private static void logRequestHeader(ContentCachingRequestWrapper request, String prefix) {
        val queryString = request.getQueryString();
        if (queryString == null) {
            printLog(prefix, String.format("%s %s %s", LoggingConstant.API_REQUEST, request.getMethod(), request.getRequestURI()));
        } else {
            printLog(prefix, String.format("%s %s %s?%s", LoggingConstant.API_REQUEST, request.getMethod(), request.getRequestURI(), queryString));
        }
    }

    private static void logRequestBody(ContentCachingRequestWrapper request, String prefix) {
        val content = request.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, request.getContentType(), request.getCharacterEncoding(), prefix, LoggingConstant.REQUEST_BODY);
        }
    }

    private static void logResponse(ContentCachingResponseWrapper response, String prefix) {
        val status = response.getStatus();
        String message = String.format("%s %s %s ,", LoggingConstant.RESPONSE, status, HttpStatus.valueOf(status).getReasonPhrase());

        val content = response.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, response.getContentType(), response.getCharacterEncoding(), prefix, message);
        }

    }

    private static void logContent(byte[] content, String contentType, String contentEncoding, String prefix, String message) {
        val mediaType = MediaType.valueOf(contentType);
        val visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
        if (visible) {
            try {
                val contentString = new String(content, contentEncoding);
                printLog(prefix, String.format("%s %s", message, contentString));
            } catch (UnsupportedEncodingException e) {
                printLog(prefix, String.format("[%s bytes content]", content.length));
            }
        } else {
            printLog(prefix, String.format("[%s bytes content]", content.length));
        }
    }

    protected static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    protected static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }

    private static void printLog(String prefix, String message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        } else {
            log.info("{} {}", prefix, message);
        }
    }

}
