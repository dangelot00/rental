package org.blockbuster.rental.repository;

import org.blockbuster.rental.entity.Film;
import org.blockbuster.rental.enums.FilmGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FilmRepository extends JpaRepository<Film, Long> {
  Optional<Film> findByTitle(String title);

  List<Film> findByGenre(FilmGenre genre);
}