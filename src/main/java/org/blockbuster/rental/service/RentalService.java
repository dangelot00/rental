package org.blockbuster.rental.service;

import org.blockbuster.rental.web.RentalDTO;

import java.util.List;

public interface RentalService {
  RentalDTO rentFilm(String username, String filmTitle, int durationInDays);
  List<RentalDTO> getRentalsByUsername(String username);
  void returnFilm(String username, Long rentalId);
}
