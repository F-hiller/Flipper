package com.ovg.flipper.service;

import com.ovg.flipper.entity.User;
import com.ovg.flipper.dto.UserSignupDto;
import com.ovg.flipper.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(UserSignupDto userInfo) {
        String userRole = "ROLE_USER";
        // TEST : make admin user.
        if (userInfo.getUsername().startsWith("admin_")){
            userRole = "ROLE_ADMIN";
        }
        User user = User.builder()
                .username(userInfo.getUsername())
                .password(passwordEncoder.encode(userInfo.getPassword()))
                .email(userInfo.getEmail())
                .role(userRole)
                .build();

        userRepository.save(user);
    }
}
