package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Wallet;
import com.momo.certChain.model.dto.WalletDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WalletMapper {
    WalletMapper instance = Mappers.getMapper(WalletMapper.class);

    WalletDTO toDTO(Wallet wallet);

    @Mapping(target = "id",ignore = true)
    Wallet toEntity(WalletDTO walletDTO);
}
