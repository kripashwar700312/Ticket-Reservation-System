package com.cotiviti.filter;

import com.cotiviti.constants.APIConstant;
import com.cotiviti.constants.JwtTokenConstants;
import com.cotiviti.dto.ApiError;
import com.cotiviti.exception.UnauthorizedException;
import com.cotiviti.service.JwtTokenService;
import com.cotiviti.service.impl.CustomerDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private CustomerDetailsServiceImpl customerDetailsServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException,
            IOException {

        if (request.getRequestURI().contains("/" + APIConstant.AUTH)) {
            chain.doFilter(request, response);
            return;
        }

        String authToken = null;
        String username = null;
        final String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (requestHeader != null && requestHeader.startsWith(JwtTokenConstants.TOKEN_PREFIX.getName())) {
            authToken = requestHeader.substring(JwtTokenConstants.TOKEN_PREFIX.getName().length());
            if (authToken != null && !authToken.isEmpty() && !authToken.equalsIgnoreCase("null")) {
                try {
                    username = jwtTokenService.getUsername(authToken);
                } catch (IllegalArgumentException e) {
                    log.error("an error occured during getting username from token", e.getMessage());
                    throw new UnauthorizedException("JWT token is not valid");
                }
            } else {
                log.warn("JWT token in request headers is Null.");
            }
        } else {
            log.warn("couldn't find bearer string, will ignore the header");
        }

        try {
            UserDetails userDetails = customerDetailsServiceImpl.loadUserByUsername(username);
            if (jwtTokenService.validateToken(authToken, userDetails)) {
                String refreshedToken = jwtTokenService.refreshToken(authToken);
                if (refreshedToken != null) {
                    authToken = refreshedToken;
                }
                UsernamePasswordAuthenticationToken authentication = jwtTokenService.getAuthentication(authToken, userDetails);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                response.setHeader(HttpHeaders.AUTHORIZATION, authToken);
                chain.doFilter(request, response);
            }
        } catch (UnauthorizedException ex) {
            setErrorMessage(response, ex);
        }
    }

    private void setErrorMessage(HttpServletResponse response, UnauthorizedException ex) throws IOException {
        ApiError apiError = new ApiError();
        apiError.setMessage(ex.getMessage());
        apiError.setTitle(ex.getTitle());
        apiError.setHttpStatus((HttpStatus.UNAUTHORIZED));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        PrintWriter writer = response.getWriter();
        writer.write(new ObjectMapper().writeValueAsString(apiError));
        writer.flush();
    }
}
