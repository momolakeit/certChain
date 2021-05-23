package com.momo.certChain.Utils;

import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.data.InstitutionWallet;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.repositories.*;
import com.momo.certChain.services.blockChain.ContractService;
import com.momo.certChain.services.security.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.crypto.ECKeyPair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class InitEnvService {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ImageFileRepository imageFileRepository;

    @Autowired
    private SignatureRepository signatureRepository;

    @Autowired
    private ContractService contractService;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private CertificationRepository certificationRepository;

    public final static String encryptionKey="encrypted";


    public String initEnv() throws Exception {
        Institution institution = getInstitution();

        InstitutionWallet institutionWallet = TestUtils.createInstitutionWallet();

        Certification certification = TestUtils.createCertificationTemplate();
        certification.setInstitution(null);
        certification.setUniversityStamp(imageFileRepository.save(certification.getUniversityStamp()));
        certification.setUniversityLogo(imageFileRepository.save(certification.getUniversityLogo()));
        List<Signature> signatures = certification.getSignatures();
        certification.setSignatures(new ArrayList<>());
        for(Signature signature : signatures){
            signature.setSignatureImage(imageFileRepository.save(signature.getSignatureImage()));
            certification.getSignatures().add(signatureRepository.save(signature));
        }
        certification.setStudent(null);

        certification.setId(null);

        institution.setContractAddress(contractService.uploadContract(new ECKeyPair(new BigInteger(institutionWallet.getPrivateKey()),new BigInteger(institutionWallet.getPublicKey()))));

        institutionWallet.setSalt(encryptionService.generateSalt());
        institutionWallet.setPrivateKey(encryptionService.encryptData(encryptionKey,institutionWallet.getPrivateKey(),institutionWallet.getSalt()));
        institutionWallet.setPublicKey(encryptionService.encryptData(encryptionKey,institutionWallet.getPublicKey(),institutionWallet.getSalt()));

        institution.setCertificationTemplate(certificationRepository.save(certification));
        institution.setInstitutionWallet(walletRepository.save(institutionWallet));
        return institutionRepository.save(institution).getId();
    }

    private Institution getInstitution() {
        Institution institution = TestUtils.createInstitution();
        institution.setAddress(null);
        return institution;
    }
}
