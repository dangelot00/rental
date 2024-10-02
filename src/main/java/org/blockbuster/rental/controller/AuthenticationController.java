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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/auth")
@RestController
@Slf4j
@Tag(name = "Authentication", description = "Operations related to authentication")
public class AuthenticationController {
  private final JwtTokenProvider jwtService;

  private final AuthenticationService authenticationService;

  public AuthenticationController(JwtTokenProvider jwtService, AuthenticationService authenticationService) {
    this.jwtService = jwtService;
    this.authenticationService = authenticationService;
  }

  @Operation(summary = "Register a new user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User registered successfully"),
      @ApiResponse(responseCode = "400", description = "Bad request, username already exists")
  })
  @PostMapping("/signup")
  public ResponseEntity<User> register(@RequestBody RegisterUserDTO registerUserDto) {
    log.debug("Received signup request for username: {}", registerUserDto.getUsername());

    User registeredUser = authenticationService.signup(registerUserDto);

    return ResponseEntity.ok(registeredUser);
  }

  @Operation(summary = "Authenticate a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, bad credentials")
    })
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
