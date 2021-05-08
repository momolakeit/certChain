package com.momo.certChain.services;

import com.momo.certChain.model.data.*;
import com.momo.certChain.repositories.CampagneRepository;
import com.momo.certChain.services.blockChain.ContractService;
import com.momo.certChain.services.security.EncryptionService;
import com.momo.certChain.utils.ListUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;

import javax.mail.MessagingException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Service
public class CampagneService {

    private final CertificationService certificationService;

    private final CampagneRepository campagneRepository;

    private final HumanUserService userService;

    private final EncryptionService encryptionService;

    public CampagneService(CertificationService certificationService,
                           CampagneRepository campagneRepository,
                           HumanUserService userService,
                           EncryptionService encryptionService) {
        this.certificationService = certificationService;
        this.campagneRepository = campagneRepository;
        this.userService = userService;
        this.encryptionService = encryptionService;
    }

    public Campagne runCampagne(String name, List<HumanUser> studentList, Institution institution, String walletPassword) throws Exception {
        uploadCertificatesToBlockChain(studentList, institution, walletPassword);
        return createCampagne(studentList,name,institution);
    }

    private Campagne createCampagne(List<HumanUser> studentList,String name,Institution institution) {
        Campagne campagne = new Campagne();
        campagne.setName(name);
        campagne.setDate(new Date(System.currentTimeMillis()));
        campagne.setStudentList(ListUtils.ajouterListAListe(studentList,campagne.getStudentList()));
        campagne.setInstitution(institution);
        return saveCampagne(campagne);
    }

    private Campagne saveCampagne(Campagne campagne) {
        return campagneRepository.save(campagne);
    }

    private void uploadCertificatesToBlockChain(List<HumanUser> studentList, Institution institution, String walletPassword) throws Exception {
        ECKeyPair keyPair = encryptionService.createKeyPair(institution.getInstitutionWallet().getPrivateKey(),
                                                            institution.getInstitutionWallet().getPublicKey(),
                                                            institution.getInstitutionWallet().getSalt(),
                                                            walletPassword);

        for(HumanUser humanUser : studentList){
            String generatedString = RandomStringUtils.randomAlphanumeric(10);
            Student student = (Student) userService.createHumanUser(humanUser,generatedString);
            certificationService.uploadCertificationToBlockChain(student.getCertifications().get(0),
                                                                 institution.getCertificationTemplate(),
                                                                 institution.getContractAddress(),
                                                                 keyPair,
                                                                 generatedString);
        }
    }
}
