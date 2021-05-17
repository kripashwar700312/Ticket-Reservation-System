package com.cotiviti.config;

import com.cotiviti.annotation.AllowTemp;
import com.cotiviti.constants.JwtTokenConstants;
import com.cotiviti.exception.UnauthorizedException;
import com.cotiviti.service.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Component
public class RequestIntercepter extends HandlerInterceptorAdapter {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            Class<?> clazz = handlerMethod.getBeanType();
            Method method = handlerMethod.getMethod();

            String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (requestHeader != null && requestHeader.startsWith(JwtTokenConstants.TOKEN_PREFIX.getName())) {
                String token = requestHeader.substring(JwtTokenConstants.TOKEN_PREFIX.getName().length());
                if (token != null && !token.isEmpty() && !token.equalsIgnoreCase("null")) {
                    boolean isTemp = jwtTokenService.isTemp(token);
                    if (isTemp && !clazz.isAnnotationPresent(AllowTemp.class)
                            && !method.isAnnotationPresent(AllowTemp.class)) {
                        log.error("Request Interceptor. Access denied for :: ", request.getRequestURI());
                        throw new UnauthorizedException("Access denied.");
                    }
                }
            }
        }
        return true;
    }

}
