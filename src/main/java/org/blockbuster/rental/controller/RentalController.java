package org.blockbuster.rental.controller;

import org.blockbuster.rental.service.RentalService;
import org.blockbuster.rental.web.RentalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/rentals")
public class RentalController {

  private RentalService rentalService;

  @PostMapping("/rent")
  public ResponseEntity<RentalDTO> rentFilm(
      @RequestParam Long userId,
      @RequestParam Long filmId,
      @RequestParam int durationInDays) {

    RentalDTO rentalDTO = rentalService.rentFilm(userId, filmId, durationInDays);
    return ResponseEntity.ok(rentalDTO);
  }

}
