package com.twohundredone.taskonserver.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String profileImageUrl;

    // 추후 OAuth2용
    @Column(length = 50)
    private String provider; // "kakao", "google" 등

    @Column(length = 100)
    private String providerUserId;

    @Builder
    private User(
            String email,
            String password,
            String name,
            String profileImageUrl,
            String provider,
            String providerUserId
    ) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.provider = provider;
        this.providerUserId = providerUserId;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateProfileImage(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
