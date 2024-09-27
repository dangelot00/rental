package org.blockbuster.rental.mapper;

import org.blockbuster.rental.entity.Film;
import org.blockbuster.rental.web.FilmDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FilmMapper {

  FilmDTO toDTO(Film film);

  Film toEntity(FilmDTO filmDTO);

}
