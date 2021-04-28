package com.momo.certChain.mapping;

import com.momo.certChain.model.data.ImageFile;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.model.dto.ImageFileDTO;
import com.momo.certChain.model.dto.SignatureDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SignatureMapper {
    SignatureMapper instance = Mappers.getMapper(SignatureMapper.class);

    SignatureDTO toDTO (Signature signature);

    @Mapping(target = "id",ignore = true)
    Signature toEntity (SignatureDTO signatureDTO);
}
