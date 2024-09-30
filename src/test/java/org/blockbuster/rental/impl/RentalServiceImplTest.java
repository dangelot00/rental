package org.blockbuster.rental.impl;

import org.blockbuster.rental.entity.Film;
import org.blockbuster.rental.entity.User;
import org.blockbuster.rental.web.RentalDTO;
import org.blockbuster.rental.enums.FilmGenre;
import org.blockbuster.rental.mapper.RentalMapper;
import org.blockbuster.rental.repository.FilmRepository;
import org.blockbuster.rental.repository.RentalRepository;
import org.blockbuster.rental.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RentalServiceImplTest {

  @Mock
  private RentalRepository rentalRepository;

  @Mock
  private FilmRepository filmRepository;

  @Mock
  private UserRepository userRepository;

  private RentalServiceImpl rentalService;

  @BeforeEach
  public void setUp() {
    // Initialize the mapper instance
    RentalMapper rentalMapper = Mappers.getMapper(RentalMapper.class);

    // Initialize the service with mocks and the real mapper
    rentalService = new RentalServiceImpl(userRepository, rentalRepository, filmRepository, rentalMapper);
  }

  @Test
  public void testRentFilm () {

    // Setup mock data
    User user = new User(1L, "mario_rossi", "password", 0, false, BigDecimal.valueOf(20));
    Film film = new Film(1L, "Film Title", FilmGenre.STANDARD);

    when(userRepository.findByUsername("mario_rossi")).thenReturn(Optional.of(user));
    when(filmRepository.findByTitle("Film Title")).thenReturn(Optional.of(film));

    RentalDTO rentalDTO = rentalService.rentFilm("mario_rossi", "Film Title", 3);

    assertNotNull(rentalDTO);
    assertEquals(BigDecimal.valueOf(15), rentalDTO.getCost());

  }

}