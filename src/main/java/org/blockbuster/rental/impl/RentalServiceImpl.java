package org.blockbuster.rental.impl;

import org.blockbuster.rental.entity.Rental;
import org.blockbuster.rental.entity.User;
import org.blockbuster.rental.entity.Film;
import org.blockbuster.rental.enums.FilmGenre;
import org.blockbuster.rental.mapper.RentalMapper;
import org.blockbuster.rental.repository.FilmRepository;
import org.blockbuster.rental.repository.RentalRepository;
import org.blockbuster.rental.repository.UserRepository;
import org.blockbuster.rental.service.RentalService;
import org.blockbuster.rental.web.RentalDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {

  private final UserRepository userRepository;
  private final RentalRepository rentalRepository;
  private final FilmRepository filmRepository;

  private final RentalMapper rentalMapper;

  @Override
  public RentalDTO rentFilm(String username, String filmTitle, int durationInDays) {

    User user = userRepository.findByUsername(username)
                              .orElseThrow(() -> new RuntimeException("User not found"));

    Film film = filmRepository.findByTitle(filmTitle)
                              .orElseThrow(() -> new RuntimeException("Film not found"));

    Rental rental = new Rental();
    rental.setUser(user);
    rental.setFilm(film);
    rental.setRentalDate(LocalDate.now());
    rental.setDueDate(LocalDate.now().plusDays(durationInDays));

    // Calculate cost and deposit based on constraints
    BigDecimal cost = calculateCost(film.getGenre(), durationInDays);
    BigDecimal deposit = calculateDeposit(user, film.getGenre());

    rental.setCost(cost);
    rental.setDeposit(deposit);

    rentalRepository.save(rental);

    // Update user's total rentals
    user.setTotalRentals(user.getTotalRentals() + 1);
    userRepository.save(user);

    return rentalMapper.toDTO(rental);

  }

  private BigDecimal calculateCost(FilmGenre genre, int durationInDays) {

    return switch (genre) {

      case STANDARD -> BigDecimal.valueOf(5L * durationInDays);

      case LAST_EXIT -> BigDecimal.valueOf(7L * durationInDays);

      case CHILDREN -> {
        int weeks = (int) Math.ceil(durationInDays / 7.0);
        yield BigDecimal.valueOf(10L * weeks);
      }

      default -> throw new IllegalArgumentException("Unknown genre in cost calculation");
    };

  }

  private BigDecimal calculateDeposit(User user, FilmGenre genre) {

    boolean noDepositRequired = user.getTotalRentals() >= 2 && !user.isHasMissedFilm();

    return switch (genre) {

      case STANDARD -> noDepositRequired ? BigDecimal.ZERO : BigDecimal.valueOf(3);

      case LAST_EXIT -> BigDecimal.valueOf(4);

      case CHILDREN -> BigDecimal.valueOf(1);

      default -> throw new IllegalArgumentException("Unknown genre in deposit calculation");
    };

  }

}
