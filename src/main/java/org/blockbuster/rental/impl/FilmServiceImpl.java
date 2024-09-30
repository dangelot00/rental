package org.blockbuster.rental.impl;

import org.blockbuster.rental.entity.Film;
import org.blockbuster.rental.exception.FilmNotFoundException;
import org.blockbuster.rental.mapper.FilmMapper;
import org.blockbuster.rental.repository.FilmRepository;
import org.blockbuster.rental.service.FilmService;
import org.blockbuster.rental.web.FilmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class FilmServiceImpl implements FilmService {

  private FilmRepository filmRepository;
  private FilmMapper filmMapper;

  public List<FilmDTO> getAllFilms() {
    return filmRepository.findAll().stream()
        .map(filmMapper::toDTO)
        .collect(Collectors.toList());
  }

  @Override
  public FilmDTO getFilmByTitle(String title) {
    Film film = filmRepository.findByTitle(title)
        .orElseThrow(() -> new FilmNotFoundException(title));
    return filmMapper.toDTO(film);
  }

  public FilmDTO getFilmById(Long id) {
    Film film = filmRepository.findById(id)
        .orElseThrow(() -> new FilmNotFoundException(id));
    return filmMapper.toDTO(film);
  }

}
