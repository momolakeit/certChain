package com.momo.certChain.services;

import com.momo.certChain.TestUtils;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.mapping.AddressMapper;
import com.momo.certChain.mapping.InstitutionMapper;
import com.momo.certChain.model.data.*;
import com.momo.certChain.repositories.InstitutionRepository;
import com.momo.certChain.services.blockChain.ContractService;
import com.momo.certChain.services.excel.ExcelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceTest {
    @InjectMocks
    private InstitutionService institutionService;

    @Mock
    private AddressService addressService;

    @Mock
    private InstitutionRepository institutionRepository;

    @Mock
    private ExcelService excelService;

    @Mock
    private ContractService contractService;

    @Mock
    private HumanUserService userService;

    @Mock
    private CertificationService certificationService;

    @Captor
    private ArgumentCaptor<Certification> studentCertificateArgumentCaptor;

    @Captor
    private ArgumentCaptor<Certification> institutionTemplateCertificateArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> addressArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> privateKeyArgumentCaptor;

    @Test
    public void createInstitutionTest() {
        Address address = TestUtils.createAddress();
        Institution institution = TestUtils.createInstitution();
        when(addressService.createAddress(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(TestUtils.createAddress());
        when(institutionRepository.save(any(Institution.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Institution returnVal = institutionService.createInstitution(AddressMapper.instance.toDTO(address), InstitutionMapper.instance.toDTO(institution));
        TestUtils.assertAddress(returnVal.getAddress());
        assertInstitution(institution, returnVal);

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
        String contractAddress ="contractAddress";
        when(contractService.uploadContract(anyString())).thenReturn(contractAddress);
        when(institutionRepository.save(any(Institution.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(institutionRepository.findById(anyString())).thenReturn(Optional.of(TestUtils.createInstitution()));

        Institution returnVal = institutionService.uploadCertificateContract("123456","privateKey");

        assertEquals(contractAddress,returnVal.getContractAddress());

    }

    @Test
    public void uploadCertification() throws Exception {
        int nbDeStudents = 100;
        List<HumanUser> listeOfStudents = initStudentsList(nbDeStudents);
        Institution institution = TestUtils.createInstitution();
        institution.setCertificationTemplate(TestUtils.createCertificationTemplate());
        when(institutionRepository.findById(anyString())).thenReturn(Optional.of(institution));
        when(userService.saveMultipleUser(any(List.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(excelService.readStudentsFromExcel(any(byte[].class))).thenReturn(listeOfStudents);

        institutionService.uploadCertificationsToBlockChain(TestUtils.getExcelByteArray(), "123456");
        verify(certificationService, times(nbDeStudents)).uploadCertificationToBlockChain(  studentCertificateArgumentCaptor.capture(),
                                                                                            institutionTemplateCertificateArgumentCaptor.capture(),
                                                                                            addressArgumentCaptor.capture(),
                                                                                            privateKeyArgumentCaptor.capture());

        List<Certification> studentsCertifications = studentCertificateArgumentCaptor.getAllValues();

        List<Certification> institutionCertificationTemplates = institutionTemplateCertificateArgumentCaptor.getAllValues();

        for(Certification cert: studentsCertifications){
            TestUtils.assertCertification(cert);
        }
        for(Certification cert: institutionCertificationTemplates){
            TestUtils.assertCertificationInstitution(cert);
        }
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