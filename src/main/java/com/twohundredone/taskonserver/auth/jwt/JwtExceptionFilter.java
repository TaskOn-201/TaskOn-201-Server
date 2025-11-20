package com.twohundredone.taskonserver.auth.jwt;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.INVALID_TOKEN;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.TOKEN_EXPIRED;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusError;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("[JWT ERROR] Expired token: {}", e.getMessage());
            setErrorResponse(response, TOKEN_EXPIRED);

        } catch (io.jsonwebtoken.security.SignatureException |
                 io.jsonwebtoken.MalformedJwtException e) {
            log.warn("[JWT ERROR] Invalid token: {}", e.getMessage());
            setErrorResponse(response, INVALID_TOKEN);

        } catch (JwtException e) {
            log.warn("[JWT ERROR] JWT exception: {}", e.getMessage());
            setErrorResponse(response, INVALID_TOKEN);

        } catch (Exception e) {
            throw e;
        }
    }

    private void setErrorResponse(HttpServletResponse response, ResponseStatusError status)
            throws IOException {
        response.setStatus(status.getStatusCode());
        response.setContentType("application/json; charset=UTF-8");

        String json = new ObjectMapper().writeValueAsString(ApiResponse.fail(status, null));
        response.getWriter().write(json);
    }

}
