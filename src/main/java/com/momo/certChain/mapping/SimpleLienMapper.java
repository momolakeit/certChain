package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Lien;
import com.momo.certChain.model.dto.LienDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SimpleLienMapper {
    SimpleLienMapper instance = Mappers.getMapper(SimpleLienMapper.class);

    @Mapping(target = "certificateEncKey",ignore = true)
    @Mapping(target = "certification",ignore = true)
    LienDTO toDTO(Lien lien);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "certification",ignore = true)
    Lien toEntity(LienDTO lienDTO);
}
