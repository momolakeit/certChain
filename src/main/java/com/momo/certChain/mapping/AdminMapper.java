package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Admin;
import com.momo.certChain.model.dto.request.AdminDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminMapper {
    AdminMapper instance = Mappers.getMapper(AdminMapper.class);

    AdminDTO toDTO(Admin admin);

    Admin toEntity (AdminDTO adminDTO);
}
