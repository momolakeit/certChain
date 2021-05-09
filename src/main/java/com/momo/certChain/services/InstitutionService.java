package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.PasswordNotMatchingException;
import com.momo.certChain.mapping.InstitutionMapper;
import com.momo.certChain.model.data.*;
import com.momo.certChain.model.dto.InstitutionDTO;
import com.momo.certChain.repositories.InstitutionRepository;
import com.momo.certChain.services.blockChain.ContractService;
import com.momo.certChain.services.excel.ExcelService;
import com.momo.certChain.services.security.EncryptionService;
import com.momo.certChain.services.security.KeyPairService;
import com.momo.certChain.utils.ListUtils;
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

    private final ExcelService excelService;

    private final WalletService walletService;

    private final CampagneService campagneService;

    private final KeyPairService keyPairService;

    public InstitutionService(InstitutionRepository institutionRepository,
                              AddressService addressService,
                              ContractService contractService,
                              ExcelService excelService,
                              WalletService walletService,
                              CampagneService campagneService,
                              KeyPairService keyPairService) {
        this.institutionRepository = institutionRepository;
        this.addressService = addressService;
        this.contractService = contractService;
        this.excelService = excelService;
        this.walletService = walletService;
        this.campagneService = campagneService;
        this.keyPairService = keyPairService;
    }

    public Institution createInstitution(String street,
                                         String city,
                                         String province,
                                         String postalCode,
                                         String country,
                                         String name,
                                         String walletPassword,
                                         String username,
                                         String password,
                                         String passwordConfirmation) throws NoSuchProviderException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, CipherException {
        Address address = addressService.createAddress(street, city, province, postalCode, country);

        checkIfPasswordMatching(password, passwordConfirmation);

        Institution institution = new Institution();
        institution.setAddress(address);
        institution.setName(name);
        institution.setPassword(password);
        institution.setUsername(username);
        institution.setInstitutionWallet(walletService.createWallet(walletPassword));

        return saveInstitution(institution);
    }

    public Institution uploadCertificateContract(String uuid,String walletPassword) throws Exception {
        Institution institution = getInstitution(uuid);

        ECKeyPair keyPair = keyPairService.createKeyPair(institution.getInstitutionWallet().getPrivateKey(),
                                                            institution.getInstitutionWallet().getPublicKey(),
                                                            institution.getInstitutionWallet().getSalt(),
                                                            walletPassword);
        institution.setContractAddress(contractService.uploadContract(keyPair));
        return saveInstitution(institution);
    }

    public Institution uploadCertificationsToBlockChain(byte[] bytes, String uuid,String walletPassword,String campagneName) throws Exception {
        List<HumanUser> studentList = excelService.readStudentsFromExcel(bytes);

        Institution institution = getInstitution(uuid);

        linkInstitutionAndStudents(institution, studentList);

        Campagne campagne = campagneService.runCampagne(campagneName,studentList,institution,walletPassword);

        institution.setCampagnes(ListUtils.ajouterObjectAListe(campagne,institution.getCampagnes()));

        return saveInstitution(institution);
    }

    public InstitutionDTO toDTO(Institution institution){
        return InstitutionMapper.instance.toDTO(institution);
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

    private Institution saveInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }

    private void checkIfPasswordMatching(String password, String passwordConfirmation) {
        if(!password.equals(passwordConfirmation)){
            throw new PasswordNotMatchingException();
        }
    }
}
