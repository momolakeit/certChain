package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.dto.InstitutionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses ={AddressMapper.class,SimpleStudentMapper.class,SimpleInstitutionMapper.class,ImageFileMapper.class,SignatureMapper.class})
public interface CertificationMapper {

    CertificationMapper instance = Mappers.getMapper(CertificationMapper.class);

    InstitutionDTO toDTO (Institution institution);

    @Mapping(target = "id",ignore = true)
    Institution toEntity (InstitutionDTO institution);

}
