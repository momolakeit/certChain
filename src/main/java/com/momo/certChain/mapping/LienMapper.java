package com.momo.certChain.mapping;

import com.momo.certChain.model.data.InstitutionWallet;
import com.momo.certChain.model.data.Lien;
import com.momo.certChain.model.dto.InstitutionWalletDTO;
import com.momo.certChain.model.dto.LienDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LienMapper {
    LienMapper instance = Mappers.getMapper(LienMapper.class);

    @Mapping(target = "certificateEncKey",ignore = true)
    LienDTO toDTO(Lien lien);

    @Mapping(target = "id",ignore = true)
    Lien toEntity(LienDTO lienDTO);
}
