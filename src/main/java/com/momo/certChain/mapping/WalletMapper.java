package com.momo.certChain.mapping;

import com.momo.certChain.model.data.InstitutionWallet;
import com.momo.certChain.model.dto.InstitutionWalletDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WalletMapper {
    WalletMapper instance = Mappers.getMapper(WalletMapper.class);

    InstitutionWalletDTO toDTO(InstitutionWallet institutionWallet);

    @Mapping(target = "id",ignore = true)
    InstitutionWallet toEntity(InstitutionWalletDTO institutionWalletDTO);
}
