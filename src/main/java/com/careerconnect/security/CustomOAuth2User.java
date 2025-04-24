package com.careerconnect.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Data
public class CustomOAuth2User implements OAuth2User {

	private OAuth2User oauth2User;
	private Long userId;
	private Collection<? extends GrantedAuthority> authorities;

	public CustomOAuth2User(OAuth2User oauth2User, Long userId, Collection<? extends GrantedAuthority> authorities) {
//		super();
		this.oauth2User = oauth2User;
		this.userId = userId;
		this.authorities = authorities;

	}

	@Override
	public Map<String, Object> getAttributes() {
		return oauth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return oauth2User.getAuthorities();
		return authorities;
	}

	@Override
	public String getName() {
		return oauth2User.getAttribute("email");
	}
	public String getNameReal() {
		return oauth2User.getAttribute("name");
	}

}
