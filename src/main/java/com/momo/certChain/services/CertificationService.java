package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.mapping.CertificationMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.model.dto.CertificationDTO;
import com.momo.certChain.repositories.CertificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Sign;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class CertificationService {

    private final CertificationRepository certificationRepository;

    private final ImageFileService imageFileService;

    private final SignatureService signatureService;

    public CertificationService(CertificationRepository certificationRepository, ImageFileService imageFileService, SignatureService signatureService) {
        this.certificationRepository = certificationRepository;
        this.imageFileService = imageFileService;
        this.signatureService = signatureService;
    }

    public Certification createCertificationTemplate(CertificationDTO certificationDTO){
        Certification certification = CertificationMapper.instance.toEntity(certificationDTO);
        List<Signature> signatures = new ArrayList<>();
        for(Signature signature : certification.getSignatures()){
            signatures.add(signatureService.createSignature(signature.getAuthorName()));
        }
        certification.setSignatures(signatures);
        return saveCertification(certification);
    }

    public Certification addCertificationUniversityLogo(String uuid,byte[]bytes){
        Certification certification = findCertification(uuid);
        certification.setUniversityLogo(imageFileService.createImageFile(bytes));
        return saveCertification(certification);
    }

    public Certification addCertificationUniversityStamp(String uuid,byte[]bytes){
        Certification certification = findCertification(uuid);
        certification.setUniversityStamp(imageFileService.createImageFile(bytes));
        return saveCertification(certification);
    }


    public Certification saveCertification(Certification certification){
        return certificationRepository.save(certification);
    }

    private Certification findCertification(String uuid){
        return certificationRepository.findById(uuid).orElseThrow(this::certificationNotFound);
    }
    private ObjectNotFoundException certificationNotFound(){
        return new ObjectNotFoundException("Certification");
    }
}
