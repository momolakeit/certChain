package com.momo.certChain.services;

import com.momo.certChain.exception.AuthorizationException;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.mapping.CampagneMapper;
import com.momo.certChain.model.data.*;
import com.momo.certChain.model.dto.CampagneDTO;
import com.momo.certChain.repositories.CampagneRepository;
import com.momo.certChain.services.messaging.MessageService;
import com.momo.certChain.services.request.HeaderCatcherService;
import com.momo.certChain.services.security.KeyPairService;
import com.momo.certChain.utils.ListUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CampagneService {

    private final CertificationService certificationService;

    private final CampagneRepository campagneRepository;

    private final HumanUserService userService;

    private final KeyPairService keyPairService;

    private final HeaderCatcherService headerCatcherService;

    private final MessageService messageService;

    public CampagneService(CertificationService certificationService,
                           CampagneRepository campagneRepository,
                           HumanUserService userService,
                           KeyPairService keyPairService,
                           HeaderCatcherService headerCatcherService,
                           MessageService messageService) {
        this.certificationService = certificationService;
        this.campagneRepository = campagneRepository;
        this.userService = userService;
        this.keyPairService = keyPairService;
        this.headerCatcherService = headerCatcherService;
        this.messageService = messageService;
    }

    public void runCampagne(String campagneId, String walletPassword) throws Exception {
        Campagne campagne = getCampagne(campagneId);

        isUserAllowedToRunCampagne(campagne);

        uploadCertificatesToBlockChain(campagne.getStudentList(), campagne.getInstitution(), walletPassword);

        campagne.setRunned(true);

        saveCampagne(campagne);
    }

    public Campagne createCampagne(List<HumanUser> studentList, String name, Institution institution, Date dateExpiration) {
        Campagne campagne = createCampagneWithFields(name, dateExpiration);

        studentList.forEach(student -> setCertificationValues(student, dateExpiration));

        campagne.setStudentList(ListUtils.ajouterListAListe(saveUsers(studentList), campagne.getStudentList()));
        campagne.setInstitution(institution);

        return saveCampagne(campagne);
    }

    public Campagne getCampagne(String campagneId) {
        return campagneRepository.findById(campagneId).orElseThrow(() -> new ObjectNotFoundException("Campagne"));
    }

    public CampagneDTO toDTO(Campagne campagne) {
        return CampagneMapper.instance.toDTO(campagne);
    }

    private Campagne saveCampagne(Campagne campagne) {
        return campagneRepository.save(campagne);
    }

    private void uploadCertificatesToBlockChain(List<HumanUser> studentList, Institution institution, String walletPassword) throws Exception {
        ECKeyPair keyPair = keyPairService.createKeyPair(institution.getInstitutionWallet().getPrivateKey(),
                institution.getInstitutionWallet().getPublicKey(),
                institution.getInstitutionWallet().getSalt(),
                walletPassword);

        for (HumanUser humanUser : studentList) {
            String generatedString = RandomStringUtils.randomAlphanumeric(11);
            Student student = (Student) humanUser;

            messageService.sendCertificatePrivateKey(humanUser, generatedString);

            certificationService.uploadCertificationToBlockChain(student.getCertifications().get(0),
                    institution.getCertificationTemplate(),
                    institution.getContractAddress(),
                    keyPair,
                    generatedString);
        }
    }

    private void isUserAllowedToRunCampagne(Campagne campagne) {
        isUserOwnerOfCampagne(campagne);
        doesUserHasCertificateTemplate(campagne);
        doesUserHasUploadedContract(campagne);
    }

    private void setCertificationValues(HumanUser humanUser, Date dateOfIssuing) {
        ((Student) humanUser).getCertifications().get(0).setDateOfIssuing(dateOfIssuing);
        ((Student) humanUser).getCertifications().get(0).setStudent(((Student) humanUser));
    }

    private Campagne createCampagneWithFields(String name, Date dateExpiration) {
        Campagne campagne = new Campagne();
        campagne.setName(name);
        campagne.setDate(dateExpiration);
        return campagne;
    }

    private List<HumanUser> saveUsers(List<HumanUser> studentList) {
        return studentList.stream()
                .map(userService::createHumanUser)
                .collect(Collectors.toList());
    }

    private void doesUserHasUploadedContract(Campagne campagne) {
        if (StringUtils.isBlank(campagne.getInstitution().getContractAddress()) || Objects.isNull(campagne.getInstitution().getContractAddress())) {
            throw new ValidationException("Vous devez deployer le votre contrat d'abord.Veuillez le faire dans votre dashboard");
        }
    }

    private void doesUserHasCertificateTemplate(Campagne campagne) {
        if (Objects.isNull(campagne.getInstitution().getCertificationTemplate())) {
            throw new ValidationException("Vous devez avoir un modèle de certificat");
        }
    }

    private void isUserOwnerOfCampagne(Campagne campagne) {
        if (!campagne.getInstitution().getId().equals(headerCatcherService.getUserId())) {
            throw new AuthorizationException("Vous ne pouvez pas rouler cette campagne");
        }
    }
}
