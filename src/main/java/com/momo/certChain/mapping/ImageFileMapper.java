package com.momo.certChain.mapping;

import com.momo.certChain.model.data.Address;
import com.momo.certChain.model.data.ImageFile;
import com.momo.certChain.model.dto.AddressDTO;
import com.momo.certChain.model.dto.ImageFileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ImageFileMapper {
    ImageFileMapper instance = Mappers.getMapper(ImageFileMapper.class);

    @Mapping(target = "bytes",ignore = true)
    ImageFileDTO toDTO (ImageFile imageFile);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "bytes",ignore = true)
    ImageFile toEntity (ImageFileDTO imageFileDTO);
}
