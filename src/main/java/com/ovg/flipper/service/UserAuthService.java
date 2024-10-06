package com.ovg.flipper.service;

import com.ovg.flipper.dto.UserAuthDto;
import com.ovg.flipper.dto.UserLoginDto;
import com.ovg.flipper.entity.User;
import com.ovg.flipper.dto.UserSignupDto;
import com.ovg.flipper.repository.RedisJwtRepository;
import com.ovg.flipper.repository.UserRepository;
import com.ovg.flipper.util.JwtProvider;
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
    private final JwtProvider jwtProvider;
    private final RedisJwtRepository redisJwtRepository;

    public UserAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, RedisJwtRepository redisJwtRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.redisJwtRepository = redisJwtRepository;
    }

    @Transactional
    public void registerUser(UserSignupDto userInfo) {
        // TODO : check if user already exists
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
        UserAuthDto userAuthDto = jwtProvider.generateTokens(u.getUsername());
        redisJwtRepository.save(String.valueOf(u.getUserId()), userAuthDto.getRefreshToken());
        return userAuthDto;
    }

    public boolean checkRefreshToken(String token){
        return redisJwtRepository.exists(token);
    }
}
