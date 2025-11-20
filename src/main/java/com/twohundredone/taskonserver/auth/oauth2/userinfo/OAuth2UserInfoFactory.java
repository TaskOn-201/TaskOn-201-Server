package com.twohundredone.taskonserver.auth.oauth2.userinfo;

import com.twohundredone.taskonserver.auth.oauth2.OAuth2UserInfo;
import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {

        return switch (provider) {
            case "kakao" -> new KakaoUserInfo(attributes);
            case "google" -> new GoogleUserInfo(attributes);
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }
}
