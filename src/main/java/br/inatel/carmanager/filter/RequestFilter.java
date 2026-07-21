package br.inatel.carmanager.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import lombok.extern.slf4j.Slf4j;

@Component
@WebFilter(filterName = "Request Filter", urlPatterns = "/*")
@Order(-999)
@Slf4j
public class RequestFilter extends OncePerRequestFilter
{
    @Value("${car-manager.filter.include-response}")
    private boolean includeResponse;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        String requestBody = this.getContentAsString(wrappedRequest.getContentAsByteArray(),
                                                     request.getCharacterEncoding());
        if (requestBody.length() > 0)
        {
            log.info("Request body:\n{}", requestBody);
        }

        String responseBody = this.getContentAsString(wrappedResponse.getContentAsByteArray(),
                                                      response.getCharacterEncoding());
        if (responseBody.length() > 0 && includeResponse)
        {
            log.info("Response body:\n{}", responseBody);
        }

        wrappedResponse.copyBodyToResponse();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException
    {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        List<String> excludeUrlPatterns = List.of(
            "/actuator",
            "/actuator/*"
        );

        return excludeUrlPatterns
            .stream()
            .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
    }

    private String getContentAsString(byte[] buf, String charsetName)
        throws UnsupportedEncodingException
    {
        if (buf == null || buf.length == 0)
        {
            return "";
        }
        return new String(buf, 0, buf.length, charsetName);
    }
}
