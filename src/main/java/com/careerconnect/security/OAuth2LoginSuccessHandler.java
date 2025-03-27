package com.careerconnect.security;


import com.careerconnect.dto.response.LoginResponse;
import com.careerconnect.service.AuthService;
import com.careerconnect.util.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtService jwtService;

//	@Autowired
//	@Lazy
//	public OAuth2LoginSuccessHandler(PasswordEncoder bCryptPasswordEncoder, AuthService authService, JwtService jwtService) {
//		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//		this.authService = authService;
//		this.jwtService = jwtService;
//	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {
//		super.onAuthenticationSuccess(request, response, authentication);

		CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

		String accessToken = jwtService.generateAccessToken(oAuth2User); // Tạo accessToken dựa trên thông tin user
		String refreshToken = jwtService.generateRefreshToken(oAuth2User); // Tạo refreshToken dựa trên email

		// Thiết lập cookie cho refreshToken
		Cookie cookie = new Cookie("refreshToken", refreshToken);
		cookie.setPath("/");
		cookie.setMaxAge(365 * 24 * 60 * 60); // 1 năm
		cookie.setHttpOnly(true); // Ngăn JavaScript truy cập
		cookie.setSecure(true); // Chỉ gửi qua HTTPS (đặt false nếu dùng localhost không có HTTPS)
		cookie.setAttribute("SameSite", "None"); // Cho phép gửi với yêu cầu cross-site
		response.addCookie(cookie);

		// Tạo response JSON
		LoginResponse loginResponse = new LoginResponse();
		loginResponse.setAccessToken(accessToken);
		loginResponse.setUser(LoginResponse.LoggedInUser.builder()
				.userId(oAuth2User.getUserId())
				.username(oAuth2User.getOauth2User().getName())
				.role(
//						là mảng nhưng chỉ lấy 1 phần tử đầu tiên
						oAuth2User.getOauth2User().getAuthorities().stream().findFirst().get().getAuthority()
				)
				.build());



		// Thiết lập response
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_OK);
		new ObjectMapper().writeValue(response.getWriter(), loginResponse);

//		ghi log
		Logger.log("OAuth2LoginSuccessHandler", loginResponse);
	}
	
}
