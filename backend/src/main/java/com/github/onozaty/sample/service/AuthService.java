package com.github.onozaty.sample.service;

import com.github.onozaty.sample.mapper.UserCredentialMapper;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final UserCredentialMapper credentialMapper;
  private final PasswordEncoder passwordEncoder;

  public AuthService(UserCredentialMapper credentialMapper, PasswordEncoder passwordEncoder) {
    this.credentialMapper = credentialMapper;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public void changePassword(Long userId, String currentPassword, String newPassword) {
    String currentHash =
        credentialMapper
            .findPasswordHashByUserId(userId)
            .orElseThrow(
                () -> new AuthenticationCredentialsNotFoundException("Credential not found"));

    if (!passwordEncoder.matches(currentPassword, currentHash)) {
      throw new BadCredentialsException("Current password is incorrect");
    }

    credentialMapper.updatePassword(userId, passwordEncoder.encode(newPassword));
  }
}
