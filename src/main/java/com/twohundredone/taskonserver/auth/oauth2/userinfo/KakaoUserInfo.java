package com.twohundredone.taskonserver.auth.oauth2.userinfo;

import com.twohundredone.taskonserver.auth.oauth2.OAuth2UserInfo;
import java.util.Map;

public class KakaoUserInfo extends OAuth2UserInfo {
    public KakaoUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        return (String) ((Map) attributes.get("kakao_account")).get("email");
    }

    @Override
    public String getName() {
        Map profile = (Map) ((Map) attributes.get("kakao_account")).get("profile");
        return (String) profile.get("nickname");
    }

    @Override
    public String getProfileImage() {
        Map profile = (Map) ((Map) attributes.get("kakao_account")).get("profile");
        return (String) profile.get("profile_image_url");
    }

}
