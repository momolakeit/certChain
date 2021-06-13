package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.mapping.ImageFileMapper;
import com.momo.certChain.model.data.ImageFile;
import com.momo.certChain.model.dto.ImageFileDTO;
import com.momo.certChain.repositories.ImageFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ImageFileService {

    private final ImageFileRepository imageFileRepository;

    public ImageFileService(ImageFileRepository imageFileRepository) {
        this.imageFileRepository = imageFileRepository;
    }
    public ImageFile createImageFile(byte[] bytes){
        ImageFile imageFile = new ImageFile();
        imageFile.setBytes(bytes);

        return saveImageFile(imageFile);
    }

    public ImageFile saveImageFile(ImageFile imageFile){
        return imageFileRepository.save(imageFile);
    }

    public ImageFile findImageFile(String uuid){
        return imageFileRepository.findById(uuid).orElseThrow(this::imageFileNotFound);
    }

    private ObjectNotFoundException imageFileNotFound(){
        return new ObjectNotFoundException("Image");
    }
}
