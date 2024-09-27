package org.blockbuster.rental.web;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class RentalDTO {
  private Long id;
  private Long userId;
  private Long filmId;
  private LocalDate rentalDate;
  private LocalDate dueDate;
  private LocalDate returnDate;
  private BigDecimal cost;
  private BigDecimal deposit;
  private boolean isLate;
}
