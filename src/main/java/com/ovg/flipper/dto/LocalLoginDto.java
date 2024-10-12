package com.ovg.flipper.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalLoginDto {

  @NotBlank(message = "이메일은 필수 입력 항목입니다.")
  @Email(message = "유효한 이메일 주소를 입력해주세요.")
  private String email;

  @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
  @Size(min = 8, message = "비밀번호는 8자 이상입니다.")
  private String password;
}