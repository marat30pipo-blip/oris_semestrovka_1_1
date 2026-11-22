package ru.itis.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpForm {
    private String lastName;
    private String firstName;
    private String middleName;
    private String email;
    private String password;
}
