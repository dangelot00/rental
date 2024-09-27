package org.blockbuster.rental.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;
  private String message;

  public ErrorResponse(String message) {
    this.timestamp = LocalDateTime.now();
    this.message = message;
  }

}
