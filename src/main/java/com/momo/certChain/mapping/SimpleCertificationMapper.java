package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.dto.CertificationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = SimpleStudentMapper.class)
public interface SimpleCertificationMapper {

    SimpleCertificationMapper instance = Mappers.getMapper(SimpleCertificationMapper.class);

    @Mapping(target = "institution",ignore = true)
    @Mapping(target = "campagne",ignore = true)
    Certification toSimple (Certification certification);

    @Mapping(target = "institution",ignore = true)
    @Mapping(target = "liens",ignore = true)
    @Mapping(target = "campagne",ignore = true)
    CertificationDTO toDTO (Certification certification);

}
