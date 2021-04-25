package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.dto.InstitutionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = AddressMapper.class)
public interface InstitutionMapper {
    InstitutionMapper instance = Mappers.getMapper(InstitutionMapper.class);

    InstitutionDTO toDTO (Institution institution);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "certifications",ignore = true)
    Institution toEntity (InstitutionDTO institution);

}
