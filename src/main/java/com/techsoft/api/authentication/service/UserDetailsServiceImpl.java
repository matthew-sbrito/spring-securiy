package com.techsoft.api.authentication.service;

import com.techsoft.api.authentication.domain.ApplicationUser;
import com.techsoft.api.authentication.form.UserForm;
import com.techsoft.api.authentication.repository.UserRepository;
import com.techsoft.api.authentication.response.ApplicationUserResponse;
import com.techsoft.api.authentication.response.AuthResponse;
import com.techsoft.api.common.service.impl.AbstractCrudServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserDetailsServiceImpl extends AbstractCrudServiceImpl<ApplicationUser, Long, UserRepository, UserForm> implements UserDetailsService {

    @Autowired
    UserDetailsServiceImpl(UserRepository userRepository) {
        super(userRepository, ApplicationUser.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Searching in the DB the user by username '{}'", username);

        ApplicationUser applicationUser = repository.findByUsername(username);
        log.info("ApplicationUser found '{}'", applicationUser);

        if(applicationUser == null) {
            throw new UsernameNotFoundException(String.format("Application user '%s' not found!", username));
        }

        return applicationUser;
    }
}
