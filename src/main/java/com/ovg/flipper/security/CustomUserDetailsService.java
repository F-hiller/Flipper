package com.ovg.flipper.security;

import com.ovg.flipper.entity.User;
import com.ovg.flipper.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    //username으로 email 정보가 들어온다.
    Optional<User> user = userRepository.findByEmail(username);
    if (user.isEmpty()) {
      throw new UsernameNotFoundException("User not found");
    }

    String nullablePwd = user.get().getPassword();
    String password = nullablePwd == null ? "" : nullablePwd;
    String role = user.get().getRole();

    return new org.springframework.security.core.userdetails.User(username, password,
        List.of(new SimpleGrantedAuthority(role)));
  }
}