package org.blockbuster.rental.web;

import lombok.Data;

@Data
public class RegisterUserDTO {
  private String username;

  private String password;

  private String fullName;
}
