package org.blockbuster.rental.repository;

import org.blockbuster.rental.entity.Film;
import org.blockbuster.rental.enums.FilmGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Long> {
  List<Film> findByGenre(FilmGenre genre);
}