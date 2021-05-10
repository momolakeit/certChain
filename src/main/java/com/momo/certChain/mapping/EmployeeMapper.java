package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Employee;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.model.dto.EmployeesDTO;
import com.momo.certChain.model.dto.StudentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {AddressMapper.class,ImageFileMapper.class,InstitutionMapper.class})
public interface EmployeeMapper {

    EmployeeMapper instance = Mappers.getMapper(EmployeeMapper.class);

    EmployeesDTO toDTO (Employee employee);

    @Mapping(target = "id",ignore = true)
    Employee toEntity (EmployeesDTO employeesDTO);

}
