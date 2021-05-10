package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Student;
import com.momo.certChain.model.dto.StudentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = AddressMapper.class)
public interface SimpleStudentMapper {

    SimpleStudentMapper instance = Mappers.getMapper(SimpleStudentMapper.class);

    @Mapping(target = "certifications",ignore = true)
    @Mapping(target = "institution",ignore = true)
    @Mapping(target = "address",ignore = true)
    StudentDTO toDTO (Student institution);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "certifications",ignore = true)
    @Mapping(target = "institution",ignore = true)
    @Mapping(target = "address",ignore = true)
    Student toEntity (StudentDTO institution);

    @Mapping(target = "institution",ignore = true)
    @Mapping(target = "address",ignore = true)
    @Mapping(target = "certifications",ignore = true)
    @Mapping(target = "password",ignore = true)
    @Mapping(target = "username",ignore = true)
    Student toSimple (Student student);

}
