package org.blockbuster.rental.mapper;

import org.blockbuster.rental.entity.Rental;
import org.blockbuster.rental.web.RentalDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RentalMapper {

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "film.id", target = "filmId")
  RentalDTO toDTO(Rental rental);

  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "filmId", target = "film.id")
  Rental toEntity(RentalDTO rentalDTO);
}
