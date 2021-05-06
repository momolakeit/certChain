package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.mapping.InstitutionMapper;
import com.momo.certChain.model.data.*;
import com.momo.certChain.model.dto.InstitutionDTO;
import com.momo.certChain.repositories.InstitutionRepository;
import com.momo.certChain.services.blockChain.ContractService;
import com.momo.certChain.services.blockChain.ContractServiceImpl;
import com.momo.certChain.services.excel.ExcelService;
import com.momo.certChain.services.security.EncryptionService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

@Service
@Transactional
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    private final AddressService addressService;

    private final ContractService contractService;

    private final CertificationService certificationService;

    private final ExcelService excelService;

    private final HumanUserService userService;

    private final WalletService walletService;

    private final EncryptionService encryptionService;

    public InstitutionService(InstitutionRepository institutionRepository,
                              AddressService addressService,
                              ContractService contractService,
                              CertificationService certificationService,
                              ExcelService excelService,
                              HumanUserService userService,
                              WalletService walletService,
                              EncryptionService encryptionService) {
        this.institutionRepository = institutionRepository;
        this.addressService = addressService;
        this.contractService = contractService;
        this.certificationService = certificationService;
        this.excelService = excelService;
        this.userService = userService;
        this.walletService = walletService;
        this.encryptionService = encryptionService;
    }

    public Institution createInstitution(String street, String city, String province, String postalCode, String country, String name, String walletPassword) throws NoSuchProviderException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, CipherException {
        Address address = addressService.createAddress(street, city, province, postalCode, country);
        Institution institution = new Institution();
        institution.setAddress(address);
        institution.setName(name);
        institution.setInstitutionWallet(walletService.createWallet(walletPassword));
        return saveInstitution(institution);
    }

    public Institution uploadCertificateContract(String uuid) throws Exception {
        Institution institution = getInstitution(uuid);
        institution.setContractAddress(contractService.uploadContract(createKeyPair(institution.getInstitutionWallet().getPrivateKey(),
                                                                                    institution.getInstitutionWallet().getPublicKey())));
        return saveInstitution(institution);
    }

    public void uploadCertificationsToBlockChain(byte[] bytes, String uuid,String walletPassword) throws Exception {
        Institution institution = getInstitution(uuid);
        List<HumanUser> studentList = excelService.readStudentsFromExcel(bytes);
        linkInstitutionAndStudents(institution, studentList);
        for(HumanUser humanUser :studentList){
            String generatedString = RandomStringUtils.randomAlphanumeric(10);
            Student student = (Student) userService.createHumanUser(humanUser,generatedString);
            certificationService.uploadCertificationToBlockChain(student.getCertifications().get(0),
                                                                 institution.getCertificationTemplate(),
                                                                 institution.getContractAddress(),
                                                                 createKeyPair( institution.getInstitutionWallet(),walletPassword),
                                                                 generatedString);
        }
    }

    public InstitutionDTO toDTO(Institution institution){
        return InstitutionMapper.instance.toDTO(institution);
    }

    private Institution saveInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }

    public Institution getInstitution(String uuid) {
        return institutionRepository.findById(uuid).orElseThrow(this::institutionNotFound);
    }

    private void linkInstitutionAndStudents(Institution institution, List<HumanUser> students) {
        for (HumanUser student : students) {
            student.setInstitution(institution);
        }
    }

    private ObjectNotFoundException institutionNotFound() {
        return new ObjectNotFoundException("Institution");
    }

    private ECKeyPair createKeyPair(String privateKey, String publicKey){
        return new ECKeyPair(new BigInteger(privateKey),new BigInteger(publicKey));
    }
    private ECKeyPair createKeyPair(InstitutionWallet institutionWallet,String walletPassword){
        String privateKey = encryptionService.decryptData(walletPassword,institutionWallet.getPrivateKey(),institutionWallet.getSalt());
        String publicKey = encryptionService.decryptData(walletPassword,institutionWallet.getPublicKey(),institutionWallet.getSalt());
        return createKeyPair(privateKey,publicKey);
    }
}
