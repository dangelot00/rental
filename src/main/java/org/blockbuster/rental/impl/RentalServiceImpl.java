package org.blockbuster.rental.impl;

import org.blockbuster.rental.entity.Rental;
import org.blockbuster.rental.entity.User;
import org.blockbuster.rental.entity.Film;
import org.blockbuster.rental.enums.FilmGenre;
import org.blockbuster.rental.exception.CalculationCostException;
import org.blockbuster.rental.exception.FilmNotFoundException;
import org.blockbuster.rental.exception.NotEnoughBalanceException;
import org.blockbuster.rental.exception.RentalAlreadyReturnedException;
import org.blockbuster.rental.exception.RentalNotFoundException;
import org.blockbuster.rental.exception.UserNotFoundException;
import org.blockbuster.rental.mapper.RentalMapper;
import org.blockbuster.rental.repository.FilmRepository;
import org.blockbuster.rental.repository.RentalRepository;
import org.blockbuster.rental.repository.UserRepository;
import org.blockbuster.rental.service.RentalService;
import org.blockbuster.rental.web.RentalDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import static java.time.temporal.ChronoUnit.DAYS;

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

    if (durationInDays <= 0) {
      throw new IllegalArgumentException("Duration in days must be greater than 0");
    }

    User user = userRepository.findByUsername(username)
                              .orElseThrow(() -> new UserNotFoundException(username));

    Film film = filmRepository.findByTitle(filmTitle)
                              .orElseThrow(() -> new FilmNotFoundException(filmTitle));

    Rental rental = new Rental();
    rental.setUser(user);
    rental.setFilm(film);
    rental.setRentalDate(LocalDate.now());
    rental.setDueDate(LocalDate.now().plusDays(durationInDays));

    // Calculate cost and deposit based on constraints
    BigDecimal cost = calculateCost(film.getGenre(), durationInDays);
    BigDecimal deposit = calculateDeposit(user, film.getGenre());

    BigDecimal totalCost = cost.add(deposit);
    if (totalCost.compareTo(user.getCredit()) > 0) {
      throw new NotEnoughBalanceException("Insufficient credit to rent film");
    }

    rental.setCost(cost);
    rental.setDeposit(deposit);

    rentalRepository.save(rental);

    // Update user's total rentals
    user.setTotalRentals(user.getTotalRentals() + 1);
    // Update user's credit
    user.setCredit(user.getCredit().subtract(totalCost));

    userRepository.save(user);

    return rentalMapper.toDTO(rental);

  }

  @Override
  public void returnFilm(String username, Long rentalId) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(username));

    Rental rental = rentalRepository.findById(rentalId)
        .orElseThrow(() -> new RentalNotFoundException(rentalId));

    if (!rental.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("You are not authorized to return this rental");
    }

    if (rental.getReturnDate() != null) {
      throw new RentalAlreadyReturnedException(rentalId);
    }

    rental.setReturnDate(LocalDate.now());

    if (rental.getReturnDate().isAfter(rental.getDueDate())) {
      rental.setLate(true);

      long daysLate = DAYS.between(rental.getDueDate(), rental.getReturnDate());
      BigDecimal lateFee = BigDecimal.valueOf(2 * daysLate);

      rental.setCost(rental.getCost().add(lateFee));
      rental.setDeposit(BigDecimal.ZERO); // Loss of deposit

      user.setHasMissedFilm(true);
      // I assume that the user's credit can be negative
      user.setCredit(user.getCredit().subtract(lateFee));
    }

    // Return deposit to user
    user.setCredit(user.getCredit().add(rental.getDeposit()));
    userRepository.save(user);

    rentalRepository.save(rental);
  }

  @Override
  public List<RentalDTO> getRentalsByUsername(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(username));

    List<Rental> rentals = rentalRepository.findByUser(user);

    return rentals.stream()
                  .map(rentalMapper::toDTO)
                  .collect(Collectors.toList());
  }

  private BigDecimal calculateCost(FilmGenre genre, int durationInDays) {

    if (genre == null) {
      throw new CalculationCostException("Unknown genre in cost calculation");
    }

    return switch (genre) {

      case STANDARD -> BigDecimal.valueOf(5L * durationInDays);

      case LAST_EXIT -> BigDecimal.valueOf(7L * durationInDays);

      case CHILDREN -> {
        int weeks = (int) Math.ceil(durationInDays / 7.0);
        yield BigDecimal.valueOf(10L * weeks);
      }

    };

  }

  private BigDecimal calculateDeposit(User user, FilmGenre genre) {

    if (genre == null) {
      throw new CalculationCostException("Unknown genre in deposit calculation");
    }

    boolean noDepositRequired = user.getTotalRentals() >= 2 && !user.isHasMissedFilm();

    return switch (genre) {

      case STANDARD -> noDepositRequired ? BigDecimal.ZERO : BigDecimal.valueOf(3);

      case LAST_EXIT -> BigDecimal.valueOf(4);

      case CHILDREN -> BigDecimal.valueOf(1);

    };

  }

}
