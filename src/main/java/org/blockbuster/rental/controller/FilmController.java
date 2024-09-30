package org.blockbuster.rental.controller;

import org.blockbuster.rental.service.FilmService;
import org.blockbuster.rental.web.FilmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/films")
@Tag(name = "Films", description = "Operations related to films")
public class FilmController {
  @Autowired
  private FilmService filmService;

  @Operation(summary = "Get all films")
  @GetMapping
  public ResponseEntity<List<FilmDTO>> getAllFilms() {
    List<FilmDTO> films = filmService.getAllFilms();
    return ResponseEntity.ok(films);
  }

  @Operation(summary = "Get film by Title")
  @GetMapping("/{title}")
  public ResponseEntity<FilmDTO> getFilmById(@PathVariable String title) {
    FilmDTO film = filmService.getFilmByTitle(title);
    return ResponseEntity.ok(film);
  }

}
