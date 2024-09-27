package org.blockbuster.rental.service;

import org.blockbuster.rental.web.LoginUserDTO;
import org.blockbuster.rental.web.RegisterUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.blockbuster.rental.repository.UserRepository;
import org.blockbuster.rental.entity.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationService {
  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final AuthenticationManager authenticationManager;

  @Autowired
  public AuthenticationService(
      UserRepository userRepository,
      AuthenticationManager authenticationManager,
      PasswordEncoder passwordEncoder
  ) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User signup(RegisterUserDTO input) {
    log.debug("Signing up user: {}", input.getUsername());

    User user = new User();
    user.setUsername(input.getUsername());
    user.setPassword(passwordEncoder.encode(input.getPassword()));

    return userRepository.save(user);
  }

  public User authenticate(LoginUserDTO input) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            input.getUsername(),
            input.getPassword()
        )
    );

    return userRepository.findByUsername(input.getUsername())
        .orElseThrow();
  }
}
