package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.dto.InstitutionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = AddressMapper.class)
public interface SimpleInstitutionMapper {
    SimpleInstitutionMapper instance = Mappers.getMapper(SimpleInstitutionMapper.class);

    @Mapping(target = "campagnes",ignore = true)
    @Mapping(target = "certificationTemplate",ignore = true)
    @Mapping(target = "institutionWallet",ignore = true)
    InstitutionDTO toDTO (Institution institution);

    @Mapping(target = "campagnes",ignore = true)
    @Mapping(target = "certificationTemplate",ignore = true)
    @Mapping(target = "institutionWallet",ignore = true)
    Institution toEntity (InstitutionDTO institution);

}
