package com.example.dietcommunity.member.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpGeneralRequest {


    @NotBlank(message = "이메일을 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;


    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 5, max = 15, message = "아이디는 최소 5자이상 이어야하며, 최대 15자까지 허용됩니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]{5,15}$", message = "아이디는 영어 대소문자, 숫자, 점(.), 언더스코어(_), 빼기 기호(-)만 허용됩니다.")
    private String accountId;



    @NotBlank(message = "닉네임을 입력해주세요.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
    private String nickname;



    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}", message = "비밀번호는 8~20자 영문 대 소문자, 숫자, 특수문자를 사용해야 합니다.")
    private String password;


    // (?=.*[0-9])  ->  최소 하나의 숫자
    // (?=.*[a-zA-Z]) -> 최소 하나의 영문자(대문자 소문자 상관X) :
    // (?=.*\\W) -> 문자나 숫자가 아닌 문자가 최소한 하나
    // (?=\\S+$) -> 공백문자 허용X

}
