package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.model.dto.InstitutionDTO;
import com.momo.certChain.model.dto.StudentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {AddressMapper.class,ImageFileMapper.class,InstitutionMapper.class,CertificationMapper.class})
public interface StudentMapper {

    StudentMapper instance = Mappers.getMapper(StudentMapper.class);

    StudentDTO toDTO (Student student);

    @Mapping(target = "id",ignore = true)
    Student toEntity (StudentDTO studentDTO);

}
