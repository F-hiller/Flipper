package com.ovg.flipper.security;

import com.ovg.flipper.service.AuthService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final AuthService authService;

  @Autowired
  public CustomOAuth2UserService(AuthService authService) {
    this.authService = authService;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    String userNameAttributeName = userRequest
        .getClientRegistration()
        .getProviderDetails()
        .getUserInfoEndpoint()
        .getUserNameAttributeName();

    Map<String, Object> attributes = oAuth2User.getAttributes();

    CustomOAuth2User customOAuth2User = new CustomOAuth2User(attributes, userNameAttributeName);

    authService.registerOAuth2User(customOAuth2User.getEmail(), customOAuth2User.getName());

    return customOAuth2User;
  }
}