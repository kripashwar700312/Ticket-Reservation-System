package com.cotiviti.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class WebSecurityCorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        res.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        res.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET");
        res.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
        res.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Authorization, Content-Type, Accept, x-requested-with, Cache-Control");
        res.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization, Content-Type, Accept, x-requested-with, Cache-Control");
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }

}
