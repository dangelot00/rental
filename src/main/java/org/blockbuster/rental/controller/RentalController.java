package org.blockbuster.rental.controller;

import org.blockbuster.rental.service.RentalService;
import org.blockbuster.rental.web.RentalDTO;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/rentals")
@Tag(name = "Rentals", description = "Operations related to film rentals")
public class RentalController {

  private RentalService rentalService;

  @Operation(summary = "Rent a film")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Rental successful"),
      @ApiResponse(responseCode = "404", description = "User or Film not found")
  })
  @PostMapping("/rent")
  public ResponseEntity<RentalDTO> rentFilm(
      @RequestParam @Parameter(description = "User ID") Long userId,
      @RequestParam @Parameter(description = "Film ID") Long filmId,
      @RequestParam @Parameter(description = "Duration in days") int durationInDays) {

    RentalDTO rentalDTO = rentalService.rentFilm(userId, filmId, durationInDays);
    return ResponseEntity.ok(rentalDTO);
  }

}
