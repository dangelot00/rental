package org.blockbuster.rental.service;

import org.blockbuster.rental.web.RentalDTO;

public interface RentalService {
  RentalDTO rentFilm(Long userId, Long filmId, int durationInDays);
}
