package org.spbstu.aleksandrov.billingsystem.crm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthProvider implements AuthenticationProvider {

    private final UserRepository repository;

    public AuthProvider(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        User user = repository.getUserByLogin(name);
        if (user == null) throw new UsernameNotFoundException("User with login: " + name + " not found");
        if (!user.getPassword().equals(password)) throw new BadCredentialsException("Wrong password");
        return new UsernamePasswordAuthenticationToken(user, password, user.getRoles());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
