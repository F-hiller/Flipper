package com.ovg.flipper.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginDto {
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(min = 3, max = 20, message = "사용자 이름은 3자 이상, 20자 이하입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상입니다.")
    private String password;
}
