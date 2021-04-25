package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.model.dto.InstitutionDTO;
import com.momo.certChain.model.dto.StudentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = AddressMapper.class)
public interface SimpleStudentMapper {

    SimpleStudentMapper instance = Mappers.getMapper(SimpleStudentMapper.class);

    StudentDTO toDTO (Student institution);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "certifications",ignore = true)
    Student toEntity (StudentDTO institution);

}