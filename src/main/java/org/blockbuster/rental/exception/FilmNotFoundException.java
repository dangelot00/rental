package org.blockbuster.rental.exception;

public class FilmNotFoundException extends RuntimeException {

  public FilmNotFoundException(String filmTitle) {
    super("The film '" + filmTitle + "' does not exist");
  }

    public FilmNotFoundException(Long id) {
        super("The film id '" + id + "' does not exist");
    }
}
