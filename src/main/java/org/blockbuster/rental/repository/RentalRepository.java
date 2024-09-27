package org.blockbuster.rental.repository;

import org.blockbuster.rental.entity.Rental;
import org.blockbuster.rental.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
  List<Rental> findByUser(User user);
}
