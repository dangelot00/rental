package org.blockbuster.rental.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String username) {
    super("The username '" + username + "' does not exist");
  }

  public UserNotFoundException(Long id) {
    super("The user id '" + id + "' does not exist");
  }

}
