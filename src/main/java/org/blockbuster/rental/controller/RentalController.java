package org.blockbuster.rental.controller;

import org.blockbuster.rental.service.RentalService;
import org.blockbuster.rental.web.RentalDTO;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rentals")
@Tag(name = "Rentals", description = "Operations related to film rentals")
public class RentalController {

  @Autowired
  private RentalService rentalService;

  @Operation(summary = "Rent a film")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Rental successful"),
      @ApiResponse(responseCode = "403", description = "Access denied"),
      @ApiResponse(responseCode = "404", description = "User or Film not found"),
      @ApiResponse(responseCode = "422", description = "User does not have enough credit")
  })
  @PostMapping("/rent")
  public ResponseEntity<RentalDTO> rentFilm(
      @RequestParam @Parameter(description = "Film Title") String filmTitle,
      @RequestParam @Parameter(description = "Duration in days") int durationInDays,
      Authentication authentication) {

    String username = authentication.getName();
    log.debug("User {} is renting film {}", username, filmTitle);
    RentalDTO rentalDTO = rentalService.rentFilm(username, filmTitle, durationInDays);

    return ResponseEntity.ok(rentalDTO);
  }

  @Operation(summary = "Return a film")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Film returned successfully"),
      @ApiResponse(responseCode = "403", description = "Access denied"),
      @ApiResponse(responseCode = "404", description = "User or Rental not found")
  })
  @PostMapping("/return")
  public ResponseEntity<String> returnFilm(
      @RequestParam @Parameter(description = "Rental ID") Long rentalId,
      Authentication authentication) {

    String username = authentication.getName();
    log.debug("User {} is returning film with rental ID {}", username, rentalId);
    rentalService.returnFilm(username, rentalId);

    return ResponseEntity.ok("Film returned successfully");
  }

}
