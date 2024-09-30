package org.blockbuster.rental.service;

import org.blockbuster.rental.web.FilmDTO;

import java.util.List;

public interface FilmService {

  List<FilmDTO> getAllFilms();
  FilmDTO getFilmByTitle(String title);
  FilmDTO getFilmById(Long id);

}
