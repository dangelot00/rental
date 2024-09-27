package org.blockbuster.rental.exception.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

import org.blockbuster.rental.exception.CalculationCostException;
import org.blockbuster.rental.exception.FilmNotFoundException;
import org.blockbuster.rental.exception.UserNotFoundException;
import org.blockbuster.rental.web.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler({BadCredentialsException.class, AccountStatusException.class, AccessDeniedException.class, SignatureException.class, ExpiredJwtException.class})
  public ProblemDetail handleSecurityException(Exception exception) {
    ProblemDetail errorDetail = null;

    if (exception instanceof BadCredentialsException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
      errorDetail.setProperty("description", "The username or password is incorrect");

      return errorDetail;
    }

    if (exception instanceof AccountStatusException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      errorDetail.setProperty("description", "The account is locked");
    }

    if (exception instanceof AccessDeniedException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      errorDetail.setProperty("description", "You are not authorized to access this resource");
    }

    if (exception instanceof SignatureException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      errorDetail.setProperty("description", "The JWT signature is invalid");
    }

    if (exception instanceof ExpiredJwtException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      errorDetail.setProperty("description", "The JWT token has expired");
    }

    if (errorDetail == null) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
      errorDetail.setProperty("description", "Unknown internal server error");
    }

    // in a real-world application, you would keep logs of the stack trace with some additional information

    return errorDetail;
  }

  @ExceptionHandler({UserNotFoundException.class, FilmNotFoundException.class})
  public ResponseEntity<Object> handleResourceNotFoundException(RuntimeException ex) {
    ErrorResponse error = new ErrorResponse(ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(CalculationCostException.class)
  public ResponseEntity<Object> handleCalculationCostException(RuntimeException ex) {
    ErrorResponse error = new ErrorResponse(ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
