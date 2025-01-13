package com.careerconnect.security;

import com.careerconnect.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Data
public class CustomUserDetails implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;
    Long userId;
    String userName;
    String password = null;
    String role = null;
    Boolean active;
    Set<SimpleGrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        userId = user.getUserId();
        userName = user.getUsername();
        password = user.getPassword();
        role = user.getRole();
        authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_"+role));
        active = user.getActive();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
//        return true;
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}