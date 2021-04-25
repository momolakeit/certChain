package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Address;
import com.momo.certChain.model.dto.AddressDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapper {
    AddressMapper instance = Mappers.getMapper(AddressMapper.class);

    AddressDTO toDTO (Address address);

    @Mapping(target = "id",ignore = true)
    Address toEntity (AddressDTO address);
}
