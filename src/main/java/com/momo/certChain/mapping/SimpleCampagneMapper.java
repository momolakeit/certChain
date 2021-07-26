package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Campagne;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.dto.CampagneDTO;
import com.momo.certChain.model.dto.CertificationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SimpleCampagneMapper {
    CampagneMapper instance = Mappers.getMapper(CampagneMapper.class);

    @Mapping(target = "certifications",ignore = true)
    @Mapping(target = "institution",ignore = true)
    CampagneDTO toDTO (Campagne campagne);

    Campagne toSimple(Campagne campagne);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "certifications",ignore = true)
    @Mapping(target = "institution",ignore = true)
    Campagne toEntity (CampagneDTO campagne);

}
