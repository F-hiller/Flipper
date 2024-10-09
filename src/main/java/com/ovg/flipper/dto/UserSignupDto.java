package com.ovg.flipper.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserSignupDto {
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(min = 3, max = 20, message = "사용자 이름을 3자 이상, 20자 이하로 설정해주세요.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호를 8자 이상으로 설정해주세요.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @Builder
    public UserSignupDto(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
