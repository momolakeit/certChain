package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.mapping.CertificationMapper;
import com.momo.certChain.mapping.SignatureMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.repositories.CertificationRepository;
import com.momo.certChain.services.blockChain.ContractService;
import com.momo.certChain.services.blockChain.ContractServiceImpl;
import com.momo.certChain.services.security.EncryptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.ECKeyPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Transactional
public class CertificationService {

    private final CertificationRepository certificationRepository;

    private final ImageFileService imageFileService;

    private final SignatureService signatureService;

    private final ContractService contractService;

    private final EncryptionService encryptionService;

    public CertificationService(CertificationRepository certificationRepository,
                                ImageFileService imageFileService,
                                SignatureService signatureService,
                                ContractService contractService,
                                EncryptionService encryptionService) {
        this.certificationRepository = certificationRepository;
        this.imageFileService = imageFileService;
        this.signatureService = signatureService;
        this.contractService = contractService;
        this.encryptionService = encryptionService;
    }

    public Certification createCertificationTemplate(Certification certification) {
        List<Signature> signatures = new ArrayList<>();
        for (Signature signature : certification.getSignatures()) {
            signatures.add(signatureService.createSignature(signature.getAuthorName()));
        }
        certification.setSignatures(signatures);
        return saveCertification(certification);
    }

    public Certification addCertificationUniversityLogo(String uuid, byte[] bytes) {
        Certification certification = findCertification(uuid);
        certification.setUniversityLogo(imageFileService.createImageFile(bytes));
        return saveCertification(certification);
    }

    public Certification addCertificationUniversityStamp(String uuid, byte[] bytes) {
        Certification certification = findCertification(uuid);
        certification.setUniversityStamp(imageFileService.createImageFile(bytes));
        return saveCertification(certification);
    }

    public void uploadCertificationToBlockChain(Certification studentCertification, Certification certificationTemplate, String contractAdress, ECKeyPair ecKeyPair) throws Exception {
        studentCertification = saveCertification(studentCertification);
        certificationTemplate = CertificationMapper.instance.toSimple(certificationTemplate);

        studentCertification.setUniversityLogo(certificationTemplate.getUniversityLogo());
        studentCertification.setUniversityStamp(certificationTemplate.getUniversityStamp());
        studentCertification.setSignatures(certificationTemplate.getSignatures().stream()
                .map(SignatureMapper.instance::toSimpleSignature)
                .collect(Collectors.toList()));
        studentCertification.setCertificateText(certificationTemplate.getCertificateText());

        contractService.uploadCertificate(studentCertification, contractAdress, ecKeyPair);
    }


    public Certification saveCertification(Certification certification) {
        if (Objects.isNull(certification.getSalt())) {
            certification.setSalt(encryptionService.generateSalt());
        }
        return certificationRepository.save(certification);
    }

    private Certification findCertification(String uuid) {
        return certificationRepository.findById(uuid).orElseThrow(this::certificationNotFound);
    }

    private ObjectNotFoundException certificationNotFound() {
        return new ObjectNotFoundException("Certification");
    }
}
