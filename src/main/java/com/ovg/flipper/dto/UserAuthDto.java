package com.ovg.flipper.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDto {
    // access token, refresh token
    private String accessToken;
    private String refreshToken;
}
