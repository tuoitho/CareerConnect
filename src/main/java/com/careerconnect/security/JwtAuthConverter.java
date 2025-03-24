package com.careerconnect.security;

import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final CustomUserDetailsService customUserDetailsService;

//    @Override
//    public AbstractAuthenticationToken convert(Jwt jwt) {
//        String username = jwt.getSubject();
//        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);
//        return new UsernamePasswordAuthenticationToken(
//                userDetails,
//                jwt.getTokenValue(),
//                userDetails.getAuthorities()
//        );
//    }
//
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Kiểm tra blacklist (tùy chọn, vì CustomJwtDecoder đã kiểm tra)
        // Trích xuất authorities từ claim 'roles' (hoặc tùy chỉnh claim khác)
        List<String> roles = jwt.getClaimAsStringList("roles");
        Collection<GrantedAuthority> authorities = (roles != null)
                ? roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList())
                : Collections.emptyList();

        // Trả về JwtAuthenticationToken với principal là Jwt và authorities
        return new JwtAuthenticationToken(jwt, authorities);
    }
}