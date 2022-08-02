package com.techsoft.api.authentication.response;

import com.techsoft.api.authentication.domain.ApplicationUser;
import lombok.Data;
import java.util.List;

@Data
public class ApplicationUserResponse {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private Boolean enabled;
    private List<String> roles;

    public ApplicationUserResponse(ApplicationUser applicationUser) {
        id = applicationUser.getId();
        fullName = applicationUser.getFullName();
        username = applicationUser.getUsername();
        email = applicationUser.getEmail();
        enabled = applicationUser.getEnabled();

        roles = applicationUser.getRolesString();
    }
}
