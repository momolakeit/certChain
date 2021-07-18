package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.mapping.SignatureMapper;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.model.dto.SignatureDTO;
import com.momo.certChain.repositories.SignatureRepository;
import org.springframework.stereotype.Service;

@Service
public class SignatureService {

    private final SignatureRepository signatureRepository;

    private final ImageFileService imageFileService;

    public SignatureService(SignatureRepository signatureRepository, ImageFileService imageFileService) {
        this.signatureRepository = signatureRepository;
        this.imageFileService = imageFileService;
    }

    public Signature createSignature(String authorName){
        Signature signature = new Signature();
        signature.setAuthorName(authorName);

        return saveSignature(signature);
    }

    public Signature addSignatureImage(String signatureID, byte[] imageBytes){
        Signature signature = getSignature(signatureID);

        signature.setSignatureImage(imageFileService.createImageFile(imageBytes));

        return saveSignature(signature);
    }

    public SignatureDTO toDto(Signature signature){
        return SignatureMapper.instance.toDTO(signature);
    }

    public Signature getSignature(String uuid){
        return signatureRepository.findById(uuid).orElseThrow(this::signatureNotFound);
    }
    public Signature saveSignature(Signature signature){
        return signatureRepository.save(signature);
    }

    private ObjectNotFoundException signatureNotFound(){
        return new ObjectNotFoundException("Image non trouvée");
    }
}
