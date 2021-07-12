package com.momo.certChain.services;

import com.momo.certChain.exception.CannotAccessCertificateException;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.UserForgottenException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.mapping.CertificationMapper;
import com.momo.certChain.mapping.SignatureMapper;
import com.momo.certChain.model.CreatedLien;
import com.momo.certChain.model.data.*;
import com.momo.certChain.model.dto.CertificationDTO;
import com.momo.certChain.repositories.CertificationRepository;
import com.momo.certChain.services.blockChain.ContractService;
import com.momo.certChain.services.request.HeaderCatcherService;
import com.momo.certChain.services.security.EncryptionService;
import com.momo.certChain.utils.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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

    private final HeaderCatcherService headerCatcherService;

    private final LienService lienService;

    private final UserService userService;


    public CertificationService(CertificationRepository certificationRepository,
                                ImageFileService imageFileService,
                                SignatureService signatureService,
                                ContractService contractService,
                                EncryptionService encryptionService,
                                HeaderCatcherService headerCatcherService,
                                LienService lienService,
                                UserService userService) {
        this.certificationRepository = certificationRepository;
        this.imageFileService = imageFileService;
        this.signatureService = signatureService;
        this.contractService = contractService;
        this.encryptionService = encryptionService;
        this.headerCatcherService = headerCatcherService;
        this.lienService = lienService;
        this.userService = userService;
    }

    public Certification createCertificationTemplate(Certification certification, byte[] universityLogoBytes, byte[] universityStampBytes, Institution institution) {
        canUserCreateCertificationTemplate(certification);

        List<Signature> signatures = createSignatures(certification);


        initCertificationFields(certification,
                imageFileService.createImageFile(universityLogoBytes),
                imageFileService.createImageFile(universityStampBytes),
                signatures,
                institution);

        return saveCertificationWithSalt(certification);
    }

    private List<Signature> createSignatures(Certification certification) {
        List<Signature> signatures = new ArrayList<>();
        for (Signature signature : certification.getSignatures()) {
            signatures.add(signatureService.createSignature(signature.getAuthorName()));
        }

        return signatures;
    }

    //todo test that

    public void uploadCertificationToBlockChain(Certification studentCertification, Certification certificationTemplate, String contractAdress, ECKeyPair ecKeyPair, String encryptionKey) throws Exception {
        studentCertification = saveCertificationWithSalt(studentCertification);

        certificationTemplate = CertificationMapper.instance.toSimple(certificationTemplate);

        studentCertification = CertificationMapper.instance.clone(studentCertification);

        mapCertificateTemplateToStudentCertification(studentCertification, certificationTemplate, certificationTemplate.getInstitution());

        contractService.uploadCertificate(studentCertification, contractAdress, ecKeyPair, encryptionKey);

        saveCertification(CertificationMapper.instance.stripValuesToSave(studentCertification));
    }
    public CertificationDTO toDTO(Certification certification) {
        return CertificationMapper.instance.toDTO(certification);
    }


    //todo utiliser un wallet pour tout les get
    public Certification getUploadedCertificationWithLien(String uuid, String privateKey, String lienId) throws Exception {
        Certification certification = findCertification(uuid);

        Lien lien = lienService.getLien(lienId, privateKey);

        return getUploadedCertification(certification, lien.getCertificateEncKey());
    }

    public Certification getUploadedCertificationWithPrivateKey(String uuid, String privateKey) throws Exception {
        Certification certification = findCertification(uuid);

        return getUploadedCertification(certification, privateKey);
    }

    public CreatedLien createLien(String certificateId, String certificatePassword, String titre, Date dateExpiration) throws Exception {
        Certification certification = findCertification(certificateId);

        //permet de s'assurer qu'on a le bon password
        getUploadedCertification(certification, certificatePassword);

        CreatedLien createdLien = lienService.createLien(certificatePassword, dateExpiration, titre, certification);

        certification.setLiens(ListUtils.ajouterObjectAListe(createdLien.getLien(), certification.getLiens()));

        saveCertification(certification);

        return createdLien;
    }

    public void createPropriaitaireLien(String certificateId,String userPassword,String certEncKey) throws ParseException {
        Certification certification = findCertification(certificateId);

        doUserHasAccessToCertification(certification);

        creerLienDaccesAuCertificatPourEleve(userPassword,certification,certEncKey);
    }

    public Certification saveCertificationWithSalt(Certification certification) {
        if (Objects.isNull(certification.getSalt())) {
            certification.setSalt(encryptionService.generateSalt());
        }
        return saveCertification(certification);
    }

    public void forgetCertificate(String uuid) {
        Certification certification = findCertification(uuid);
        doUserHasAccessToCertification(certification);
        certification.setSalt(null);
        saveCertification(certification);
    }

    public Certification payCertificate(String certId) {
        Certification certification = findCertificateWithoutSalt(certId);

        doUserHasAccessToCertification(certification);

        certification.setPayed(true);

        return saveCertification(certification);

    }

    private Certification findCertification(String uuid) {
        Certification certification = findCertificateWithoutSalt(uuid);
        if (Objects.isNull(certification.getSalt())) {
            throw new UserForgottenException();
        }
        return certification;
    }

    private Certification findCertificateWithoutSalt(String uuid) {
        return certificationRepository.findById(uuid).orElseThrow(this::certificationNotFound);
    }

    private Certification saveCertification(Certification certification) {
        return certificationRepository.save(certification);
    }

    private ObjectNotFoundException certificationNotFound() {
        return new ObjectNotFoundException("Certification non trouvée");
    }

    private void mapCertificateTemplateToStudentCertification(Certification studentCertification, Certification certificationTemplate, Institution institution) {
        List<Signature> signatures = certificationTemplate.getSignatures().stream()
                .map(SignatureMapper.instance::toSimple)
                .collect(Collectors.toList());

        initCertificationFields(studentCertification,
                certificationTemplate.getUniversityLogo(),
                certificationTemplate.getUniversityStamp(),
                signatures,
                institution);

        studentCertification.setCertificateText(certificationTemplate.getCertificateText());
    }

    private void doUserHasAccessToCertification(Certification certification) {
        Student student = (Student) userService.getUser(headerCatcherService.getUserId());

        student.getCertifications()
                .stream()
                .filter(cer -> cer.getId().equals(certification.getId()))
                .findFirst()
                .orElseThrow(CannotAccessCertificateException::new);

    }

    private void initCertificationFields(Certification certification, ImageFile imageFileUniversityLogo, ImageFile imageFileUniversityStamp, List<Signature> signatures, Institution institution) {
        certification.setSignatures(signatures);
        certification.setUniversityLogo(imageFileUniversityLogo);
        certification.setUniversityStamp(imageFileUniversityStamp);
        certification.setInstitution(institution);
    }

    private Certification getUploadedCertification(Certification certification, String privateKey) throws Exception {
        return contractService.getCertificate(certification.getId(),
                certification.getInstitution().getContractAddress(),
                Keys.createEcKeyPair(),
                privateKey,
                certification.getSalt());

    }

    private void canUserCreateCertificationTemplate(Certification certification) {
        if (Objects.nonNull(certification.getStudent()) || Objects.nonNull(certification.getLiens())) {
            throw new ValidationException("la certification ne peux pas contenir d'élève ou de lien");
        }
    }

    private void creerLienDaccesAuCertificatPourEleve(String password, Certification certification, String certEncKey) throws ParseException {
        lienService.createLienAccesPourPropriataireCertificat(password,certEncKey, certification);
    }
}
