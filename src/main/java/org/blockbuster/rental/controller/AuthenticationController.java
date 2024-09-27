package org.blockbuster.rental.controller;

import org.blockbuster.rental.entity.User;
import org.blockbuster.rental.service.AuthenticationService;
import org.blockbuster.rental.web.response.LoginResponse;
import org.blockbuster.rental.web.LoginUserDTO;
import org.blockbuster.rental.web.RegisterUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.blockbuster.rental.config.jwt.JwtTokenProvider;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
  private final JwtTokenProvider jwtService;

  private final AuthenticationService authenticationService;

  public AuthenticationController(JwtTokenProvider jwtService, AuthenticationService authenticationService) {
    this.jwtService = jwtService;
    this.authenticationService = authenticationService;
  }

  @PostMapping("/signup")
  public ResponseEntity<User> register(@RequestBody RegisterUserDTO registerUserDto) {
    User registeredUser = authenticationService.signup(registerUserDto);

    return ResponseEntity.ok(registeredUser);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDTO LoginUserDTO) {
    User authenticatedUser = authenticationService.authenticate(LoginUserDTO);

    String jwtToken = jwtService.generateToken(authenticatedUser);

    LoginResponse loginResponse = new LoginResponse();
    loginResponse.setToken(jwtToken);
    loginResponse.setExpiresIn(jwtService.getExpirationTime());

    return ResponseEntity.ok(loginResponse);
  }
}
