package org.blockbuster.rental.impl;

import org.blockbuster.rental.entity.User;
import org.blockbuster.rental.exception.UserNotFoundException;
import org.blockbuster.rental.repository.UserRepository;
import org.blockbuster.rental.service.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public void addBalance(String username, double amount) {
    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new UserNotFoundException(username));

    user.setCredit(user.getCredit().add(BigDecimal.valueOf(amount)));

    userRepository.save(user);
  }
}
