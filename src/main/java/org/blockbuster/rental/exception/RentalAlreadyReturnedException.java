package org.blockbuster.rental.exception;

public class RentalAlreadyReturnedException extends RuntimeException {

  public RentalAlreadyReturnedException(Long id) {
    super("Rental id '" + id + "' was already returned");
  }

}
