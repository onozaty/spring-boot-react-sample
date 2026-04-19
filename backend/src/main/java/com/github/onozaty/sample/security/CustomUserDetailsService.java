package com.github.onozaty.sample.security;

import com.github.onozaty.sample.domain.UserWithCredential;
import com.github.onozaty.sample.mapper.UserMapper;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserMapper userMapper;

  public CustomUserDetailsService(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserWithCredential user =
        userMapper
            .findByEmailWithCredential(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

    return new User(
        user.getEmail(), user.getPasswordHash(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
  }
}
