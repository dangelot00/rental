package org.blockbuster.rental.web;

import org.blockbuster.rental.enums.FilmGenre;

import lombok.Data;

@Data
public class FilmDTO {
  private Long id;
  private String title;
  private FilmGenre genre;
}
