package com.uteq.sgaac.mapping;

import com.uteq.sgaac.dto.RequisitoDTO;
import com.uteq.sgaac.model.Requisito;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequisitoMapper {
    RequisitoDTO toDTO(Requisito requisito);
    Requisito toEntity(RequisitoDTO dto);
}
