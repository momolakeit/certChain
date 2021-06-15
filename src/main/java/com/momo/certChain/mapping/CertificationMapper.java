package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.ImageFile;
import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.dto.CertificationDTO;
import com.momo.certChain.model.dto.InstitutionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses ={AddressMapper.class,SimpleStudentMapper.class,SignatureMapper.class,SimpleInstitutionMapper.class,ImageFileMapper.class,LienMapper.class})
public interface CertificationMapper {

    CertificationMapper instance = Mappers.getMapper(CertificationMapper.class);

    CertificationDTO toDTO (Certification certification);

    @Mapping(target = "id",ignore = true)
    Certification toEntity (CertificationDTO certificationDTO);

    Certification toSimple (Certification certification);

    Certification clone (Certification certification);

    @Mapping(target = "certificateText",ignore = true)
    @Mapping(target = "student",ignore = true)
    @Mapping(target = "universityLogo",ignore = true)
    @Mapping(target = "universityStamp",ignore = true)
    @Mapping(target = "signatures",ignore = true)
    Certification stripValuesToSave(Certification certification);
}
