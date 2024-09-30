package org.blockbuster.rental.service;

import org.blockbuster.rental.web.RentalDTO;

public interface RentalService {
  RentalDTO rentFilm(String username, String filmTitle, int durationInDays);
  void returnFilm(String username, Long rentalId);
}
