package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Campagne;
import com.momo.certChain.model.dto.CampagneDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {SimpleStudentMapper.class,SimpleInstitutionMapper.class})
public interface CampagneMapper {
    CampagneMapper instance = Mappers.getMapper(CampagneMapper.class);

    CampagneDTO toDTO (Campagne campagne);

    @Mapping(target = "id",ignore = true)
    Campagne toEntity (CampagneDTO campagne);
}
