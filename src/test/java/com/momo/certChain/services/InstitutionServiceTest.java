package com.momo.certChain.services;

import com.momo.certChain.TestUtils;
import com.momo.certChain.exception.BadPasswordException;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.PasswordNotMatchingException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.model.data.*;
import com.momo.certChain.repositories.InstitutionRepository;
import com.momo.certChain.services.blockChain.ContractServiceImpl;
import com.momo.certChain.services.excel.ExcelService;
import com.momo.certChain.services.messaging.MessageService;
import com.momo.certChain.services.security.KeyPairService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceTest {
    @InjectMocks
    private InstitutionService institutionService;

    @Mock
    private AddressService addressService;

    @Mock
    private InstitutionRepository institutionRepository;

    @Mock
    private ContractServiceImpl contractServiceImpl;

    @Mock
    private CampagneService campagneService;

    @Mock
    private ExcelService excelService;

    @Mock
    private WalletService walletService;

    @Mock
    private CertificationService certificationService;

    @Mock
    private KeyPairService keyPairService;

    @Mock
    private MessageService messageService;

    @Captor
    private ArgumentCaptor<String> campagneNameCaptor;

    @Captor
    private ArgumentCaptor<String> walletPasswordCaptor;

    @Test
    public void createInstitutionTest() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException, CipherException, MessagingException {
        Address address = TestUtils.createAddress();
        Institution institution = TestUtils.createInstitution();
        InstitutionWallet institutionWallet = TestUtils.createInstitutionWallet();
        String username = "username";
        String password = "password";

        when(addressService.createAddress(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(TestUtils.createAddress());
        when(institutionRepository.save(any(Institution.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(walletService.createWallet(anyString())).thenReturn(institutionWallet);

        Institution returnVal = institutionService.createInstitution(address.getStreet(),
                address.getCity(),
                address.getProvince(),
                address.getPostalCode(),
                address.getCountry(),
                institution.getName(),
                "password",
                username,
                password,
                password);

        assertEquals(institutionWallet.getPrivateKey(), returnVal.getInstitutionWallet().getPrivateKey());
        assertEquals(institutionWallet.getPublicAddress(), returnVal.getInstitutionWallet().getPublicAddress());
        assertEquals(institutionWallet.getPublicKey(), returnVal.getInstitutionWallet().getPublicKey());
        assertFalse(returnVal.isApprouved());
        TestUtils.assertAddress(returnVal.getAddress());
        TestUtils.assertInstitution(returnVal);

    }

    @Test
    public void testCreateTemplate() throws IOException {
        when(institutionRepository.findById(anyString())).thenReturn(Optional.of(TestUtils.createInstitution()));
        when(institutionRepository.save(any(Institution.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(certificationService.createCertificationTemplate(any(Certification.class),any(byte[].class),any(byte[].class),any(Institution.class))).thenReturn(TestUtils.createCertificationTemplate());

        Institution institution = institutionService.createInstitutionCertificateTemplate("123456",TestUtils.createCertificationTemplate(),TestUtils.getExcelByteArray(),TestUtils.getExcelByteArray());

        TestUtils.assertCertificationInstitution(institution.getCertificationTemplate());

    }

    @Test
    public void createInstitutionBadPasswordThrowsExceptionTest() {
        Address address = TestUtils.createAddress();
        Institution institution = TestUtils.createInstitution();
        String username = "username";
        String password = "password";

        when(addressService.createAddress(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(TestUtils.createAddress());

        Assertions.assertThrows(PasswordNotMatchingException.class, () -> {
            institutionService.createInstitution(address.getStreet(),
                    address.getCity(),
                    address.getProvince(),
                    address.getPostalCode(),
                    address.getCountry(),
                    institution.getName(),
                    "password",
                    username,
                    password,
                    "BadPassword");
        });


    }

    @Test
    public void fetchInstitution() {
        Institution institution = TestUtils.createInstitution();

        when(institutionRepository.findById(anyString())).thenReturn(Optional.of(institution));

        Institution returnVal = institutionService.getInstitution("dsa48");

        assertInstitution(institution, returnVal);
    }

    @Test
    public void fetchInstitutionNotFoundLanceException() {
        when(institutionRepository.findById(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            institutionService.getInstitution("dsa48");
        });
    }

    @Test
    public void uploadContractToBlockchain() throws Exception {
        String contractAddress = "contractAddress";
        Institution institution = TestUtils.createInstitutionWithWallet();
        String walletPassword = "walletPassword";

        when(keyPairService.createKeyPair(anyString(), anyString(), anyString(), anyString())).thenReturn(TestUtils.createKeyPair(institution.getInstitutionWallet().getPrivateKey(),
                institution.getInstitutionWallet().getPublicKey()));
        when(contractServiceImpl.uploadContract(any(ECKeyPair.class))).thenReturn(contractAddress);
        when(institutionRepository.save(any(Institution.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(institutionRepository.findById(anyString())).thenReturn(Optional.of(institution));

        Institution returnVal = institutionService.uploadCertificateContract("123456", walletPassword);

        assertEquals(contractAddress, returnVal.getContractAddress());

    }

    @Test
    public void uploadContractToBlockchainInstitutionNotApproved() throws Exception {
        String contractAddress = "contractAddress";
        Institution institution = TestUtils.createInstitutionWithWallet();
        institution.setApprouved(false);
        String walletPassword = "walletPassword";

        when(institutionRepository.findById(anyString())).thenReturn(Optional.of(institution));

        Assertions.assertThrows(ValidationException.class,()->{
            
        })
        institutionService.uploadCertificateContract("123456", walletPassword);

    }

    @Test
    public void approveInstitution(){
        Institution institution = TestUtils.createInstitution();
        institution.setApprouved(false);
        when(institutionRepository.findById(anyString())).thenReturn(Optional.of(TestUtils.createInstitution()));
        when(institutionRepository.save(any(Institution.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Institution institutionReturnValue = institutionService.approveInstitution("123456");

        assertTrue(institutionReturnValue.isApprouved());
        TestUtils.assertInstitution(institutionReturnValue);
    }

    @Test
    public void uploadCertification() throws Exception {
        int nbDeStudents = 100;
        List<HumanUser> listeOfStudents = initStudentsList(nbDeStudents);
        String campagneName = "campagneName";
        String walletPassword = "walletPassword";

        when(institutionRepository.findById(anyString())).thenReturn(Optional.of(TestUtils.createInstitutionWithWallet()));
        when(institutionRepository.save(any(Institution.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(campagneService.runCampagne(anyString(), any(List.class), any(Institution.class), anyString())).thenReturn(TestUtils.createCampagne());
        when(excelService.readStudentsFromExcel(any(byte[].class))).thenReturn(listeOfStudents);

        Institution returnInstitution = institutionService.uploadCertificationsToBlockChain(TestUtils.getExcelByteArray(), "123456", walletPassword, campagneName);
        verify(campagneService).runCampagne(campagneNameCaptor.capture(), any(List.class), any(Institution.class), walletPasswordCaptor.capture());

        List<String> walletPasswordCaptorAllValues = walletPasswordCaptor.getAllValues();
        List<String> campagneNameCaptorAllValues = campagneNameCaptor.getAllValues();

        for (String name : campagneNameCaptorAllValues) {
            assertEquals(campagneName, name);
        }

        for (String password : walletPasswordCaptorAllValues) {
            assertEquals(walletPassword, password);
        }

        TestUtils.assertCampagne(returnInstitution.getCampagnes().get(0));
    }

    private List<HumanUser> initStudentsList(int nbDeStudents) {
        List<HumanUser> students = new ArrayList<>();
        for (int i = 0; i < nbDeStudents; i++) {
            Student singleStudent = TestUtils.createStudent();
            singleStudent.setCertifications(Collections.singletonList(TestUtils.createCertification()));
            students.add(singleStudent);
        }
        return students;
    }

    private void assertInstitution(Institution institution, Institution returnVal) {
        TestUtils.assertBaseUser(institution);
        assertEquals(institution.getName(), returnVal.getName());
    }
}