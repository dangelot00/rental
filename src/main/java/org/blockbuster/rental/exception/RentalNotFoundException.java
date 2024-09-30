package org.blockbuster.rental.exception;

public class RentalNotFoundException extends RuntimeException {

  public RentalNotFoundException(Long id) {
    super("Rental id '" + id + "' does not exist");
  }

}
