package com.ovg.flipper.service;

import com.ovg.flipper.dto.UserAuthDto;
import com.ovg.flipper.dto.UserLoginDto;
import com.ovg.flipper.entity.User;
import com.ovg.flipper.dto.UserSignupDto;
import com.ovg.flipper.repository.UserRepository;
import com.ovg.flipper.util.JwtManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class UserAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtManager jwtManager;

    public UserAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtManager jwtManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtManager = jwtManager;
    }

    @Transactional
    public boolean registerUser(UserSignupDto userInfo) {
        if(userRepository.findByUsername(userInfo.getUsername()).isPresent() || userRepository.findByEmail(userInfo.getEmail()).isPresent()){
            return false;
        }

        String userRole = "ROLE_USER";
        User user = User.builder()
                .username(userInfo.getUsername())
                .password(passwordEncoder.encode(userInfo.getPassword()))
                .email(userInfo.getEmail())
                .role(userRole)
                .build();

        userRepository.save(user);
        return true;
    }

    @Transactional
    public UserAuthDto login(UserLoginDto userLoginDto) {
        Optional<User> user = userRepository.findByUsername(userLoginDto.getUsername());

        if(user.isEmpty()){
            log.info("User {} not found", userLoginDto.getUsername());
            throw new BadCredentialsException("User not found");
        }
        User u = user.get();

        if(!passwordEncoder.matches(userLoginDto.getPassword(), u.getPassword())){
            log.info("User {} failed to log in", u.getUsername());
            throw new BadCredentialsException("Password not matched");
        }
        log.info("User {} logged in", u.getUsername());
        return createJwt(u);
    }

    private UserAuthDto createJwt(User u) {
        return jwtManager.generateTokens(u.getUserId(), u.getUsername());
    }
}
