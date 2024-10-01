package org.blockbuster.rental.impl;

import org.blockbuster.rental.entity.Film;
import org.blockbuster.rental.entity.Rental;
import org.blockbuster.rental.entity.User;
import org.blockbuster.rental.exception.CalculationCostException;
import org.blockbuster.rental.exception.FilmNotFoundException;
import org.blockbuster.rental.exception.NotEnoughBalanceException;
import org.blockbuster.rental.exception.RentalAlreadyReturnedException;
import org.blockbuster.rental.exception.RentalNotFoundException;
import org.blockbuster.rental.exception.UserNotFoundException;
import org.blockbuster.rental.web.RentalDTO;
import org.blockbuster.rental.enums.FilmGenre;
import org.blockbuster.rental.mapper.RentalMapper;
import org.blockbuster.rental.repository.FilmRepository;
import org.blockbuster.rental.repository.RentalRepository;
import org.blockbuster.rental.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import static java.time.temporal.ChronoUnit.DAYS;

@ExtendWith(MockitoExtension.class)
public class RentalServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private RentalRepository rentalRepository;

  @Mock
  private FilmRepository filmRepository;

  @Mock
  private RentalMapper rentalMapper;

  @InjectMocks
  private RentalServiceImpl rentalService;

  private User user;
  private Film film;
  private Rental rental;

  @BeforeEach
  void setUp() {
    // Initialize common objects used in tests
    user = new User();
    user.setId(1L);
    user.setUsername("testUser");
    user.setCredit(BigDecimal.valueOf(100));
    user.setTotalRentals(0);
    user.setHasMissedFilm(false);

    film = new Film();
    film.setId(1L);
    film.setTitle("testFilm");
    film.setGenre(FilmGenre.STANDARD);

    rental = new Rental();
    rental.setId(1L);
    rental.setUser(user);
    rental.setFilm(film);
    rental.setRentalDate(LocalDate.now());
    rental.setDueDate(LocalDate.now().plusDays(5));
    rental.setCost(BigDecimal.valueOf(25));
    rental.setDeposit(BigDecimal.valueOf(3));
  }

  // Tests for rentFilm method

  @Test
  void rentFilm_SuccessfulRental() {
    // Arrange
    String username = "testUser";
    String filmTitle = "testFilm";
    int durationInDays = 5;

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(filmRepository.findByTitle(filmTitle)).thenReturn(Optional.of(film));
    when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(rentalMapper.toDTO(any(Rental.class))).thenReturn(new RentalDTO());

    // Act
    RentalDTO rentalDTO = rentalService.rentFilm(username, filmTitle, durationInDays);

    // Assert
    assertNotNull(rentalDTO);
    verify(userRepository, times(1)).findByUsername(username);
    verify(filmRepository, times(1)).findByTitle(filmTitle);
    verify(rentalRepository, times(1)).save(any(Rental.class));
    verify(userRepository, times(1)).save(user);
    assertEquals(1, user.getTotalRentals());
    assertEquals(BigDecimal.valueOf(72), user.getCredit()); // 100 - (25 + 3)
  }

  @Test
  void rentFilm_UserNotFound() {
    // Arrange
    String username = "nonExistentUser";
    String filmTitle = "testFilm";
    int durationInDays = 5;

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // Act & Assert
    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
      rentalService.rentFilm(username, filmTitle, durationInDays);
    });

    assertEquals("The username '" + username + "' does not exist", exception.getMessage());
    verify(userRepository, times(1)).findByUsername(username);
    verify(filmRepository, times(0)).findByTitle(anyString());
  }

  @Test
  void rentFilm_FilmNotFound() {
    // Arrange
    String username = "testUser";
    String filmTitle = "NonExistentFilm";
    int durationInDays = 5;

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(filmRepository.findByTitle(filmTitle)).thenReturn(Optional.empty());

    // Act & Assert
    FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> {
      rentalService.rentFilm(username, filmTitle, durationInDays);
    });

    assertEquals("The film '" + filmTitle + "' does not exist", exception.getMessage());
    verify(userRepository, times(1)).findByUsername(username);
    verify(filmRepository, times(1)).findByTitle(filmTitle);
  }

  @Test
  void rentFilm_NotEnoughBalance() {
    // Arrange
    String username = "testUser";
    String filmTitle = "testFilm";
    int durationInDays = 5;

    user.setCredit(BigDecimal.valueOf(10)); // Insufficient credit

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(filmRepository.findByTitle(filmTitle)).thenReturn(Optional.of(film));

    // Act & Assert
    NotEnoughBalanceException exception = assertThrows(NotEnoughBalanceException.class, () -> {
      rentalService.rentFilm(username, filmTitle, durationInDays);
    });

    assertEquals("Insufficient credit to rent film", exception.getMessage());
    verify(userRepository, times(1)).findByUsername(username);
    verify(filmRepository, times(1)).findByTitle(filmTitle);
    verify(rentalRepository, times(0)).save(any(Rental.class));
  }

  @Test
  void rentFilm_CalculationCostException() {
    // Arrange
    String username = "testUser";
    String filmTitle = "testFilm";
    int durationInDays = 5;

    film.setGenre(null); // Unknown genre

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(filmRepository.findByTitle(filmTitle)).thenReturn(Optional.of(film));

    // Act & Assert
    CalculationCostException exception = assertThrows(CalculationCostException.class, () -> {
      rentalService.rentFilm(username, filmTitle, durationInDays);
    });

    assertEquals("Unknown genre in cost calculation", exception.getMessage());
    verify(rentalRepository, times(0)).save(any(Rental.class));
  }

  @Test
  void rentFilm_InvalidDuration() {
    // Arrange
    String username = "testUser";
    String filmTitle = "testFilm";
    int durationInDays = -1;

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      rentalService.rentFilm(username, filmTitle, durationInDays);
    });

    assertEquals("Duration in days must be greater than 0", exception.getMessage());

    // Verify no interactions with repositories
    verify(userRepository, times(0)).findByUsername(anyString());
    verify(filmRepository, times(0)).findByTitle(anyString());
  }

  @Test
  void rentFilm_NoDepositRequired() {
    // Arrange
    String username = "testUser";
    String filmTitle = "testFilm";
    int durationInDays = 5;

    user.setTotalRentals(2);
    user.setHasMissedFilm(false);

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(filmRepository.findByTitle(filmTitle)).thenReturn(Optional.of(film));
    when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> {
      Rental savedRental = invocation.getArgument(0);
      assertEquals(BigDecimal.ZERO, savedRental.getDeposit());
      return savedRental;
    });
    when(rentalMapper.toDTO(any(Rental.class))).thenReturn(new RentalDTO());

    // Act
    RentalDTO rentalDTO = rentalService.rentFilm(username, filmTitle, durationInDays);

    // Assert
    assertNotNull(rentalDTO);
    assertEquals(BigDecimal.valueOf(3), rental.getDeposit());
    verify(userRepository, times(1)).save(user);
    assertEquals(BigDecimal.valueOf(75), user.getCredit()); // 100 - 25 (no deposit)
  }

  // Tests for returnFilm method

  @Test
  void returnFilm_SuccessfulReturn() {
    // Arrange
    String username = "testUser";
    Long rentalId = 1L;

    rental.setReturnDate(null); // Rental not yet returned

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
    when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    rentalService.returnFilm(username, rentalId);

    // Assert
    assertNotNull(rental.getReturnDate());
    assertFalse(rental.isLate());
    assertEquals(BigDecimal.valueOf(103), user.getCredit()); // Adjusted expected credit
    verify(rentalRepository, times(1)).save(rental);
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void returnFilm_RentalNotFound() {
    // Arrange
    String username = "testUser";
    Long rentalId = 99L;

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

    // Act & Assert
    RentalNotFoundException exception = assertThrows(RentalNotFoundException.class, () -> {
      rentalService.returnFilm(username, rentalId);
    });

    assertEquals("Rental id '" + rentalId + "' does not exist", exception.getMessage());
    verify(rentalRepository, times(0)).save(any(Rental.class));
  }

  @Test
  void returnFilm_UserNotFound() {
    // Arrange
    String username = "nonExistentUser";
    Long rentalId = 1L;

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // Act & Assert
    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
      rentalService.returnFilm(username, rentalId);
    });

    assertEquals("The username '" + username + "' does not exist", exception.getMessage());
    verify(rentalRepository, times(0)).findById(anyLong());
  }

  @Test
  void returnFilm_AccessDenied() {
    // Arrange
    String username = "testUser";
    Long rentalId = 1L;

    User otherUser = new User();
    otherUser.setId(2L);

    rental.setUser(otherUser);

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

    // Act & Assert
    AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
      rentalService.returnFilm(username, rentalId);
    });

    assertEquals("You are not authorized to return this rental", exception.getMessage());
    verify(rentalRepository, times(0)).save(any(Rental.class));
  }

  @Test
  void returnFilm_RentalAlreadyReturned() {
    // Arrange
    String username = "testUser";
    Long rentalId = 1L;

    rental.setReturnDate(LocalDate.now()); // Already returned

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

    // Act & Assert
    RentalAlreadyReturnedException exception = assertThrows(RentalAlreadyReturnedException.class, () -> {
      rentalService.returnFilm(username, rentalId);
    });

    assertEquals("Rental id '" + rentalId + "' was already returned", exception.getMessage());
    verify(rentalRepository, times(0)).save(any(Rental.class));
  }

  @Test
  void returnFilm_LateReturn() {
    // Arrange
    String username = "testUser";
    Long rentalId = 1L;

    rental.setReturnDate(null); // Not yet returned
    rental.setDueDate(LocalDate.now().minusDays(2)); // Due date passed

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
    when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    rentalService.returnFilm(username, rentalId);

    // Assert
    assertNotNull(rental.getReturnDate());
    assertTrue(rental.isLate());
    assertEquals(BigDecimal.ZERO, rental.getDeposit());
    assertTrue(user.isHasMissedFilm());
    long daysLate = DAYS.between(rental.getDueDate(), rental.getReturnDate());
    BigDecimal expectedLateFee = BigDecimal.valueOf(2 * daysLate);
    assertEquals(user.getCredit(), BigDecimal.valueOf(100).subtract(expectedLateFee)); // Subtract late fee
    verify(rentalRepository, times(1)).save(rental);
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void returnFilm_NullRentalId() {
    // Arrange
    String username = "testUser";
    Long rentalId = null;

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

    // Act & Assert
    RentalNotFoundException exception = assertThrows(RentalNotFoundException.class, () -> {
      rentalService.returnFilm(username, rentalId);
    });

    assertEquals("Rental id '" + rentalId + "' does not exist", exception.getMessage());
    verify(userRepository, times(1)).findByUsername(anyString());
  }

  @Test
  void returnFilm_CreditBecomesNegative() {
    // Arrange
    String username = "testUser";
    Long rentalId = 1L;

    rental.setReturnDate(null);
    rental.setDueDate(LocalDate.now().minusDays(5)); // 5 days late

    user.setCredit(BigDecimal.valueOf(5)); // Low credit

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
    when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    rentalService.returnFilm(username, rentalId);

    // Assert
    long daysLate = DAYS.between(rental.getDueDate(), rental.getReturnDate());
    BigDecimal expectedLateFee = BigDecimal.valueOf(2 * daysLate);
    assertEquals(user.getCredit(), BigDecimal.valueOf(5).subtract(expectedLateFee));
    verify(rentalRepository, times(1)).save(rental);
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void returnFilm_ServiceException() {
    // Arrange
    String username = "testUser";
    Long rentalId = 1L;

    when(userRepository.findByUsername(username)).thenThrow(new RuntimeException("Database error"));

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      rentalService.returnFilm(username, rentalId);
    });

    assertEquals("Database error", exception.getMessage());
  }

  @Test
  void rentFilm_ServiceException() {
    // Arrange
    String username = "testUser";
    String filmTitle = "testFilm";
    int durationInDays = 5;

    when(userRepository.findByUsername(username)).thenThrow(new RuntimeException("Database error"));

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      rentalService.rentFilm(username, filmTitle, durationInDays);
    });

    assertEquals("Database error", exception.getMessage());
  }

  // Additional tests for edge cases

  @Test
  void calculateCost_StandardGenre() throws Throwable {
    // Arrange
    int durationInDays = 5;
    BigDecimal expectedCost = BigDecimal.valueOf(25); // 5 * 5

    // Act
    BigDecimal cost = invokeCalculateCost(FilmGenre.STANDARD, durationInDays);

    // Assert
    assertEquals(expectedCost, cost);
  }

  @Test
  void calculateCost_LastExitGenre() throws Throwable {
    // Arrange
    int durationInDays = 3;
    BigDecimal expectedCost = BigDecimal.valueOf(21); // 7 * 3

    // Act
    BigDecimal cost = invokeCalculateCost(FilmGenre.LAST_EXIT, durationInDays);

    // Assert
    assertEquals(expectedCost, cost);
  }

  @Test
  void calculateCost_ChildrenGenre() throws Throwable {
    // Arrange
    int durationInDays = 10;
    int weeks = (int) Math.ceil(durationInDays / 7.0);
    BigDecimal expectedCost = BigDecimal.valueOf(10L * weeks); // 10 per week

    // Act
    BigDecimal cost = invokeCalculateCost(FilmGenre.CHILDREN, durationInDays);

    // Assert
    assertEquals(expectedCost, cost);
  }

  @Test
  void calculateCost_UnknownGenre() {
    // Arrange
    FilmGenre unknownGenre = null;
    int durationInDays = 5;

    // Act & Assert
    CalculationCostException exception = assertThrows(CalculationCostException.class, () -> {
      invokeCalculateCost(unknownGenre, durationInDays);
    });

    assertEquals("Unknown genre in cost calculation", exception.getMessage());
  }

  // Helper method to invoke private method calculateCost
  private BigDecimal invokeCalculateCost(FilmGenre genre, int durationInDays) throws Throwable {
    try {
      Method method = RentalServiceImpl.class.getDeclaredMethod("calculateCost", FilmGenre.class, int.class);
      method.setAccessible(true);
      return (BigDecimal) method.invoke(rentalService, genre, durationInDays);
    } catch (InvocationTargetException e) {
      // Unwrap the original exception and throw it
      throw e.getCause(); // This will rethrow the actual CalculationCostException
    }
  }

}