package com.techsoft.api.authentication.form;

import lombok.Data;

@Data
public class UserForm {
    private String fullName;
    private String username;
    private String email;
    private String secretPassword;
}
