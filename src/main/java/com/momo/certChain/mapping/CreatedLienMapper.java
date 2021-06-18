package com.momo.certChain.mapping;

import com.momo.certChain.model.CreatedLien;
import com.momo.certChain.model.data.Lien;
import com.momo.certChain.model.dto.LienDTO;
import com.momo.certChain.model.dto.response.CreatedLienDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = LienMapper.class)
public interface CreatedLienMapper {
    CreatedLienMapper instance = Mappers.getMapper(CreatedLienMapper.class);

    CreatedLienDTO toDTO(CreatedLien createdLien);
}
