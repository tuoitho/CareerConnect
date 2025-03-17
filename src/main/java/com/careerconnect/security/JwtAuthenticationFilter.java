package com.careerconnect.security;

import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.exception.CustomJwtException;
import com.careerconnect.util.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    //    private final UserDetailsService userDetailsService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final Set<String> AUTH_WHITELIST = Set.of(SecurityEndpoint.AUTH_WHITELIST);
        final String path = request.getServletPath();
        if (AUTH_WHITELIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = getJwtFromRequest(request);
            // Xac thuc token
            tokenProvider.validateToken(token);

            String username = tokenProvider.getUsernameFromToken(token);
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Logger.log("current user: " + userDetails);
            filterChain.doFilter(request, response);
        } catch (CustomJwtException e) {
            // ⚠️ Ghi trực tiếp phản hồi, không để Spring Security xử lý
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(
                    ApiResponse.builder()
                            .message(e.getMessage())
                            .build()
            ));
        }
    }


    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new CustomJwtException("Authorization header is required");
    }
}