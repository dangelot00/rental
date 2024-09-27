package org.blockbuster.rental.impl;

import org.blockbuster.rental.service.RentalService;
import org.blockbuster.rental.web.RentalDTO;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {

  @Override
  public RentalDTO rentFilm(Long userId, Long filmId, int durationInDays) {
    return new RentalDTO();
  }

}
