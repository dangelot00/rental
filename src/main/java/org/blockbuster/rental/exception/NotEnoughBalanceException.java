package org.blockbuster.rental.exception;

public class NotEnoughBalanceException extends RuntimeException {

  public NotEnoughBalanceException(String message) {
    super(message);
  }

}
