package com.twohundredone.taskonserver.user.service;

import com.twohundredone.taskonserver.user.dto.UserProfileResponse;
import com.twohundredone.taskonserver.user.dto.UserProfileUpdateRequest;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest request, MultipartFile profileImage) {
        return UserProfileResponse.builder().build();
    }

    }
