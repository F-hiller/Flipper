package com.ovg.flipper.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ovg.flipper.dto.LocalLoginDto;
import com.ovg.flipper.dto.UserAuthDto;
import com.ovg.flipper.dto.UserSignupDto;
import com.ovg.flipper.entity.User;
import com.ovg.flipper.repository.UserRepository;
import com.ovg.flipper.util.JwtManager;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtManager jwtManager;

  @InjectMocks
  private AuthService authService;

  @Test
  @DisplayName("사용자 등록 성공")
  public void testRegisterUserSuccess() {
    // given
    UserSignupDto signupDto = UserSignupDto.builder().username("testuser").email("test@example.com")
        .password("password123").build();
    when(userRepository.findByEmail(signupDto.getEmail())).thenReturn(Optional.empty());

    // when
    boolean result = authService.registerUser(signupDto, "ROLE_USER");

    // then
    assertTrue(result);
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("해당 이메일을 사용하는 사용자가 이미 존재함")
  public void testRegisterUserAlreadyExists() {
    // given
    UserSignupDto signupDto = UserSignupDto.builder().username("testuser").email("test@example.com")
        .password("password123").build();
    when(userRepository.findByEmail(signupDto.getEmail()))
        .thenReturn(Optional.of(User.builder().build()));

    // when
    boolean result = authService.registerUser(signupDto, "ROLE_USER");

    // then
    assertFalse(result);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 로그인 성공")
  public void testLoginSuccess() {
    // given
    LocalLoginDto loginDto = LocalLoginDto.builder().email("test@example.com")
        .password("password123").build();
    User user = User.builder().email("test@example.com").password("encodedPassword").build();

    when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(true);
    when(jwtManager.generateTokens(user.getEmail(), user.getUserId()))
        .thenReturn(UserAuthDto.builder().accessToken("access_token").refreshToken("refresh_Token")
            .build());

    // when
    UserAuthDto authDto = authService.login(loginDto);

    // then
    assertNotNull(authDto);
    assertEquals("access_token", authDto.getAccessToken());
    assertEquals("refresh_Token", authDto.getRefreshToken());
    verify(jwtManager).generateTokens(user.getEmail(), user.getUserId());
  }

  @Test
  @DisplayName("로그인하려는 사용자를 찾지 못함")
  public void testLoginUserNotFound() {
    // given
    LocalLoginDto loginDto = LocalLoginDto.builder().email("test@example.com")
        .password("password123").build();
    when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.empty());

    // when
    Exception exception = assertThrows(BadCredentialsException.class,
        () -> authService.login(loginDto));

    // then
    assertEquals("User not found", exception.getMessage());
    verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
  }

  @Test
  @DisplayName("로그인 비밀번호가 틀림")
  public void testLoginPasswordNotMatched() {
    // given
    LocalLoginDto loginDto = LocalLoginDto.builder().email("test@example.com")
        .password("password123").build();
    User user = User.builder().email("test@example.com").password("encodedPassword").build();

    when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(false);

    // when
    Exception exception = assertThrows(BadCredentialsException.class,
        () -> authService.login(loginDto));

    // then
    assertEquals("Password not matched", exception.getMessage());
    verify(userRepository).findByEmail(loginDto.getEmail());
  }


  @Test
  @DisplayName("OAuth2 사용자가 로컬 로그인 방식으로 로그인 진행")
  public void testOAuth2UserLoginToLocal() {
    // given
    LocalLoginDto loginDto = LocalLoginDto.builder().email("test@example.com")
        .password("password123").build();
    User user = User.builder().email("test@example.com").password("").build();

    when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));

    // when
    Exception exception = assertThrows(BadCredentialsException.class,
        () -> authService.login(loginDto));

    // then
    assertEquals("User Login Method invalid. Try to login with oauth2.", exception.getMessage());
    verify(userRepository).findByEmail(loginDto.getEmail());
  }

  @Test
  @DisplayName("OAuth2 사용자 등록 성공")
  public void testRegisterOAuth2UserWhenNotExists() {
    // given
    String email = "oauth2@example.com";
    String username = "oauth2user";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // when
    authService.registerOAuth2User(email, username);

    // then
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("OAuth2 사용자 로그인 성공")
  public void testRegisterOAuth2UserWhenExists() {
    // given
    String email = "oauth2@example.com";
    String username = "oauth2user";
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(User.builder().build()));

    // when
    authService.registerOAuth2User(email, username);

    // then
    verify(userRepository, never()).save(any(User.class));
  }
}
