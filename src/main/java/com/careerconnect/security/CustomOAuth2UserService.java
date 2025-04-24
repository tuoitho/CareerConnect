package com.careerconnect.security;

import com.careerconnect.entity.Candidate;
import com.careerconnect.entity.Role;
import com.careerconnect.entity.User;
import com.careerconnect.enums.RoleEnum;
import com.careerconnect.repository.RoleRepository;
import com.careerconnect.repository.UserRepository;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        Logger.log("loadUser", oauth2User);

        //quan trọng: kết quả role trả về từ google khác với role trong hệ thống (ví dụ OAUTH2_USER, SCOPE_openid,...)
        // nên cần phải thiết lập cứng hoặc ánh xạ lại role thành ROLE_CUSTOMER


//		Set<SimpleGrantedAuthority> mappedAuthorities = new HashSet<>();
//		for (var authority : oauth2User.getAuthorities()) {
//			if (authority instanceof OAuth2UserAuthority) {
//				mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//			}
//		}

        Set<SimpleGrantedAuthority> mappedAuthorities = new HashSet<>();
        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));


        // Lấy thông tin email từ Google
        String email = oauth2User.getAttribute("email");

        // Tìm người dùng trong database
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                            // Nếu chưa tồn tại, tạo mới và lưu vào database, tạm thời chỉ cho candidate đăng nhập băng google
                            Candidate newUser = new Candidate();
                            newUser.setEmail(email);
                            newUser.setFullname(oauth2User.getAttribute("name"));
                            Role role = roleRepository.findByRoleName(RoleEnum.CANDIDATE).orElseGet(() -> {
                                Role newRole = new Role();
                                newRole.setRoleName(RoleEnum.CANDIDATE);
                                return roleRepository.save(newRole);
                            });
                            newUser.setRole(role);
                            return userRepository.save(newUser);
                        }
                );


        // Trả về CustomOAuth2User với userId từ database
        return new CustomOAuth2User(oauth2User, user.getUserId(), mappedAuthorities);
    }

}
