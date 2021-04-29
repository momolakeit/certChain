package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.repositories.SignatureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SignatureService {

    private SignatureRepository signatureRepository;

    private ImageFileService imageFileService;

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

    public Signature getSignature(String uuid){
        return signatureRepository.findById(uuid).orElseThrow(this::signatureNotFound);
    }
    public Signature saveSignature(Signature signature){
        return signatureRepository.save(signature);
    }

    private ObjectNotFoundException signatureNotFound(){
        return new ObjectNotFoundException("Image");
    }
}
