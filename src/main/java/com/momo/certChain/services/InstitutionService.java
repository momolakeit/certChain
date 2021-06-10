package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.PasswordNotMatchingException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.mapping.InstitutionMapper;
import com.momo.certChain.model.data.*;
import com.momo.certChain.model.dto.InstitutionDTO;
import com.momo.certChain.repositories.InstitutionRepository;
import com.momo.certChain.services.blockChain.ContractService;
import com.momo.certChain.services.excel.ExcelService;
import com.momo.certChain.services.messaging.MessageService;
import com.momo.certChain.services.security.EncryptionService;
import com.momo.certChain.services.security.KeyPairService;
import com.momo.certChain.utils.ListUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;

import javax.mail.MessagingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
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

    private final MessageService messageService;

    private final CertificationService certificationService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public InstitutionService(InstitutionRepository institutionRepository,
                              AddressService addressService,
                              ContractService contractService,
                              ExcelService excelService,
                              WalletService walletService,
                              CampagneService campagneService,
                              KeyPairService keyPairService,
                              MessageService messageService,
                              CertificationService certificationService,
                              UserService userService,
                              PasswordEncoder passwordEncoder) {
        this.institutionRepository = institutionRepository;
        this.addressService = addressService;
        this.contractService = contractService;
        this.excelService = excelService;
        this.walletService = walletService;
        this.campagneService = campagneService;
        this.keyPairService = keyPairService;
        this.messageService = messageService;
        this.certificationService = certificationService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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
                                         String passwordConfirmation) throws NoSuchProviderException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, CipherException, MessagingException {
        Address address = addressService.createAddress(street, city, province, postalCode, country);

        checkIfPasswordMatching(password, passwordConfirmation);

        Institution institution = createInstitution(name, walletPassword, username, password, address);

        messageService.sendApprouvalEmail(institution);

        return (Institution) userService.createUser(institution);
    }

    public Institution uploadCertificateContract(String uuid,String walletPassword) throws Exception {
        Institution institution = getInstitution(uuid);

        checkIfInstitutionApproved(institution);

        ECKeyPair keyPair = keyPairService.createKeyPair(institution.getInstitutionWallet().getPrivateKey(),
                                                            institution.getInstitutionWallet().getPublicKey(),
                                                            institution.getInstitutionWallet().getSalt(),
                                                            walletPassword);
        institution.setContractAddress(contractService.uploadContract(keyPair));
        return saveInstitution(institution);
    }

    public Certification createInstitutionCertificateTemplate(String institutionId,Certification certification, byte[] universityLogoBytes, byte[] universityStampBytes){
        Institution institution = getInstitution(institutionId);

        checkIfInstitutionApproved(institution);

        institution.setCertificationTemplate(certificationService.createCertificationTemplate(certification,universityLogoBytes,universityStampBytes,institution));

        return saveInstitution(institution).getCertificationTemplate();
    }

    public Campagne prepareCampagne(byte[] bytes, String uuid, String campagneName, Date date) throws Exception {
        List<HumanUser> studentList = excelService.readStudentsFromExcel(bytes);

        Institution institution = getInstitution(uuid);

        checkIfInstitutionApproved(institution);

        linkInstitutionAndStudents(institution, studentList);

        Campagne campagne = campagneService.createCampagne(studentList,campagneName,institution,date);

        institution.setCampagnes(ListUtils.ajouterObjectAListe(campagne,institution.getCampagnes()));

        saveInstitution(institution);

        return campagne;
    }

    public InstitutionDTO toDTO(Institution institution){
        return InstitutionMapper.instance.toDTO(institution);
    }

    public Institution getInstitution(String uuid) {
        return institutionRepository.findById(uuid).orElseThrow(this::institutionNotFound);
    }

    public Institution approveInstitution(String uuid){
        Institution institution = getInstitution(uuid);
        institution.setApprouved(true);
        return saveInstitution(institution);
    }

    public List<Institution> findNonApprouvedInstitutions(){
        return institutionRepository.findNonApprouvedInstitution();
    }

    private void linkInstitutionAndStudents(Institution institution, List<HumanUser> students) {
        for (HumanUser student : students) {
            student.setInstitution(institution);
        }
    }

    private ObjectNotFoundException institutionNotFound() {
        return new ObjectNotFoundException("Institution");
    }

    private Institution createInstitution(String name, String walletPassword, String username, String password, Address address) throws NoSuchAlgorithmException, CipherException, InvalidAlgorithmParameterException, NoSuchProviderException {
        Institution institution = new Institution();
        institution.setAddress(address);
        institution.setName(name);
        institution.setPassword(passwordEncoder.encode(password));
        institution.setUsername(username);
        institution.setApprouved(false);
        institution.setInstitutionWallet(walletService.createWallet(walletPassword));
        return institution;
    }

    private void checkIfInstitutionApproved(Institution institution){
        if(!institution.isApprouved()){
            throw new ValidationException("L'institution n'est pas encore approuvé par l'admin");
        }
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
