package com.ovg.flipper.service;

import com.ovg.flipper.dto.LocalLoginDto;
import com.ovg.flipper.dto.UserAuthDto;
import com.ovg.flipper.dto.UserSignupDto;
import com.ovg.flipper.entity.User;
import com.ovg.flipper.repository.UserRepository;
import com.ovg.flipper.util.JwtManager;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtManager jwtManager;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      JwtManager jwtManager) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtManager = jwtManager;
  }

  @Transactional
  public boolean registerUser(UserSignupDto userInfo, String userRole) {
    if (userRepository.findByEmail(userInfo.getEmail()).isPresent()) {
      return false;
    }

    User.UserBuilder userBuilder = User.builder()
        .username(userInfo.getUsername())
        .email(userInfo.getEmail())
        .role(userRole);
    if (!userInfo.getPassword().isBlank()) {
      userBuilder.password(passwordEncoder.encode(userInfo.getPassword()));
    }

    User user = userBuilder.build();
    userRepository.save(user);
    return true;
  }

  @Transactional
  public UserAuthDto login(LocalLoginDto localLoginDto) {
    Optional<User> user = userRepository.findByEmail(localLoginDto.getEmail());

    if (user.isEmpty()) {
      log.info("User {} not found", localLoginDto.getEmail());
      throw new BadCredentialsException("User not found");
    }
    User u = user.get();

    if (u.getPassword().isBlank()) {
      throw new BadCredentialsException("User Login Method invalid. Try to login with oauth2.");
    }

    if (!passwordEncoder.matches(localLoginDto.getPassword(), u.getPassword())) {
      log.info("User {} failed to log in", u.getUsername());
      throw new BadCredentialsException("Password not matched");
    }
    log.info("User {} logged in", u.getUsername());
    return createJwt(u);
  }

  private UserAuthDto createJwt(User u) {
    return jwtManager.generateTokens(u.getEmail(), u.getUserId());
  }

  @Transactional
  public void registerOAuth2User(String email, String userName) {
    Optional<User> searchUser = userRepository.findByEmail(email);
    if (searchUser.isEmpty()) {
      registerUser(UserSignupDto.builder()
          .username(userName)
          .email(email)
          .password("")
          .build(), "ROLE_USER");
    }
  }
}