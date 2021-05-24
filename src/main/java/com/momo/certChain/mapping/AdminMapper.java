package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Admin;
import com.momo.certChain.model.dto.request.AdminDTO;
import org.mapstruct.Mapper;

@Mapper
public interface AdminMapper {

    AdminDTO toDTO(Admin admin);

    Admin toEntity (AdminDTO adminDTO);
}
