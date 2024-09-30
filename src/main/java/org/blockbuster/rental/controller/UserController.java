package org.blockbuster.rental.controller;

import org.blockbuster.rental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/users")
@Tag(name = "Rentals", description = "Operations related to user account management")
public class UserController {

  @Autowired
  private UserService userService;

  @Operation(summary = "Load balance to user account")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Payment successful"),
      @ApiResponse(responseCode = "403", description = "Access denied"),
      @ApiResponse(responseCode = "404", description = "User not found"),
  })
  @PostMapping("/load-balance")
  public ResponseEntity<String> loadBalance(@RequestParam @Parameter(description = "Amount to load") double amount,
                                            Authentication authentication) {

    String username = authentication.getName();
    userService.addBalance(username, amount);

    return ResponseEntity.ok("Payment successful");
  }

}
