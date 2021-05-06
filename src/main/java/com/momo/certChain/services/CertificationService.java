package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.mapping.CertificationMapper;
import com.momo.certChain.mapping.SignatureMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.ImageFile;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.model.dto.CertificationDTO;
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

    public Certification createCertificationTemplate(Certification certification, byte[] universityLogoBytes, byte[] universityStampBytes) {
        List<Signature> signatures = new ArrayList<>();
        for (Signature signature : certification.getSignatures()) {
            signatures.add(signatureService.createSignature(signature.getAuthorName()));
        }
        initCertificationFields(certification,
                                imageFileService.createImageFile(universityLogoBytes),
                                imageFileService.createImageFile(universityStampBytes), signatures);
        return saveCertification(certification);
    }

    //todo test that

    public void uploadCertificationToBlockChain(Certification studentCertification, Certification certificationTemplate, String contractAdress, ECKeyPair ecKeyPair, String encryptionKey) throws Exception {
        studentCertification.setInstitution(certificationTemplate.getInstitution());
        studentCertification = saveCertification(studentCertification);
        certificationTemplate = CertificationMapper.instance.toSimple(certificationTemplate);

        mapCertificateTemplateToStudentCertification(studentCertification, certificationTemplate);

        contractService.uploadCertificate(studentCertification, contractAdress, ecKeyPair, encryptionKey);
    }

    public CertificationDTO toDTO(Certification certification) {
        return CertificationMapper.instance.toDTO(certification);
    }


    public Certification saveCertification(Certification certification) {
        if (Objects.isNull(certification.getSalt())) {
            certification.setSalt(encryptionService.generateSalt());
        }
        return certificationRepository.save(certification);
    }

    public void forgetCertificate(String uuid){
        Certification certification = findCertification(uuid);
        certification.setSalt(null);
        saveCertification(certification);
    }

    private Certification findCertification(String uuid) {
        return certificationRepository.findById(uuid).orElseThrow(this::certificationNotFound);
    }

    private ObjectNotFoundException certificationNotFound() {
        return new ObjectNotFoundException("Certification");
    }

    private void mapCertificateTemplateToStudentCertification(Certification studentCertification, Certification certificationTemplate) {
        List<Signature> signatures = certificationTemplate.getSignatures().stream()
                                                          .map(SignatureMapper.instance::toSimpleSignature)
                                                          .collect(Collectors.toList());
        initCertificationFields(studentCertification,
                                certificationTemplate.getUniversityLogo(),
                                certificationTemplate.getUniversityStamp(),
                                signatures);
        studentCertification.setCertificateText(certificationTemplate.getCertificateText());
    }

    private void initCertificationFields(Certification certification, ImageFile imageFileUniversityLogo, ImageFile imageFileUniversityStamp, List<Signature> signatures) {
        certification.setSignatures(signatures);
        certification.setUniversityLogo(imageFileUniversityLogo);
        certification.setUniversityStamp(imageFileUniversityStamp);
    }
}
