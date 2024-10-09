package com.ovg.flipper.security;

import com.ovg.flipper.entity.User;
import com.ovg.flipper.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = username;
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        String nullablePwd = user.get().getPassword();
        String password = nullablePwd == null ? "" : nullablePwd;
        String role = user.get().getRole();

        return new org.springframework.security.core.userdetails.User(email, password, List.of(new SimpleGrantedAuthority(role)));
    }
}
