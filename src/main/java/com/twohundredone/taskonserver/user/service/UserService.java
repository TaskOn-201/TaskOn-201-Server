package com.twohundredone.taskonserver.user.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PASSWORD_INCORRECT;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PASSWORD_INCORRECT_MISMATCH;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.USER_NOT_FOUND;

import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.global.s3.S3Uploader;
import com.twohundredone.taskonserver.global.util.FileValidator;
import com.twohundredone.taskonserver.project.entity.ProjectMember;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.user.dto.UserMeResponse;
import com.twohundredone.taskonserver.user.dto.UserPasswordUpdateRequest;
import com.twohundredone.taskonserver.user.dto.UserProfileResponse;
import com.twohundredone.taskonserver.user.dto.UserProfileUpdateRequest;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final PasswordEncoder passwordEncoder;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest request, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.updateName(request.getName());
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            FileValidator.validateImage(profileImage);

            s3Uploader.delete(user.getProfileImageUrl());

            String newImageUrl = s3Uploader.upload(profileImage, "profile");
            user.updateProfileImage(newImageUrl);
        }

        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    @Transactional(readOnly = true)
    public UserMeResponse getMyInfo(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<ProjectMember> memberships =
                projectMemberRepository.findAllByUser_UserId(userId);

        List<UserMeResponse.ProjectInfo> joinedProjects = memberships.stream()
                .map(pm -> UserMeResponse.ProjectInfo.builder()
                        .projectId(pm.getProject().getProjectId())
                        .projectName(pm.getProject().getProjectName())
                        .build()
                ).toList();

        return UserMeResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .joinedProjects(joinedProjects)
                .build();
    }


    @Transactional
    public void updatePassword(Long userId, UserPasswordUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new CustomException(PASSWORD_INCORRECT);
        }

        if (!request.newPassword().equals(request.newPasswordConfirm())) {
            throw new CustomException(PASSWORD_INCORRECT_MISMATCH);
        }

        user.updatePassword(passwordEncoder.encode(request.newPassword()));
    }
}
