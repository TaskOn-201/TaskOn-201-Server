package com.twohundredone.taskonserver.auth.oauth2.service;

import com.twohundredone.taskonserver.auth.oauth2.CustomOAuth2User;
import com.twohundredone.taskonserver.auth.oauth2.OAuth2UserInfo;
import com.twohundredone.taskonserver.auth.oauth2.userinfo.OAuth2UserInfoFactory;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Oauth2CustomUserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(request);

        String provider = request.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(userInfo.getEmail())
                        .name(userInfo.getName())
                        .profileImageUrl(userInfo.getProfileImage())
                        .provider(provider)
                        .providerUserId(String.valueOf(oAuth2User.getAttributes().get("id")))
                        .password("")  // 소셜 로그인은 비밀번호 없음
                        .build()));

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

}
