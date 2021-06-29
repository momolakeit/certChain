package com.momo.certChain.services.blockChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
import com.momo.certChain.services.security.EncryptionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {

    private ContractServiceImpl contractServiceImpl;

    private String privateKey = "privateKey";

    private String contractAddress = "contractAddress";

    @Mock
    private SavingDiploma savingDiploma;

    @Mock
    private RemoteCall remoteCall;

    @Mock
    private RemoteFunctionCall remoteFunctionCall;

    @Mock
    private Credentials credentials;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private Web3j web3j;

    @Captor
    private ArgumentCaptor<String> certificateJsonCaptor;

    @Captor
    private ArgumentCaptor<String> certificateIdCaptor;

    MockedStatic<Credentials> credentialsMockedStatic;

    MockedStatic<SavingDiploma> savingDiplomaMockedStatic;

    ECKeyPair ecKeyPair;

    @BeforeEach
    public void init() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        contractServiceImpl = new ContractServiceImpl(new ObjectMapper(), encryptionService,web3j);

        credentialsMockedStatic = mockStatic(Credentials.class);
        credentialsMockedStatic.when(() -> Credentials.create(any(ECKeyPair.class))).thenReturn(credentials);

        ecKeyPair = Keys.createEcKeyPair();
    }

    @AfterEach
    public void close() {
        credentialsMockedStatic.closeOnDemand();
        savingDiplomaMockedStatic.closeOnDemand();
    }

    @Test
    public void uploadContractTest() throws Exception {
        when(savingDiploma.getContractAddress()).thenReturn(contractAddress);
        when(remoteCall.send()).thenReturn(savingDiploma);

        savingDiplomaMockedStatic = mockStatic(SavingDiploma.class);
        savingDiplomaMockedStatic.when(() -> SavingDiploma.deploy(any(), any(Credentials.class), any(), any())).thenReturn(remoteCall);

        String returncontractAdress = contractServiceImpl.uploadContract(ecKeyPair);
        assertEquals(contractAddress, returncontractAdress);
    }

    @Test
    public void getCertificate() throws Exception {
        Certification certification = TestUtils.createCertification();
        savingDiplomaMockedStatic = mockStatic(SavingDiploma.class);

        when(savingDiploma.get(anyString())).thenReturn(remoteFunctionCall);
        when(remoteFunctionCall.send()).thenReturn(new ObjectMapper().writeValueAsString(certification));
        when(encryptionService.decryptDataForCertificate(anyString(),anyString(),anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(1));
        savingDiplomaMockedStatic.when(() -> SavingDiploma.load(any(), any(), any(Credentials.class), any(), any())).thenReturn(savingDiploma);


        Certification returnValueCertification = contractServiceImpl.getCertificate("uuid", "address", ecKeyPair,"privateKey","salt");

        assertNotNull(returnValueCertification);
        TestUtils.assertCertification(certification);
        TestUtils.assertInstitution(returnValueCertification.getInstitution());
    }


    @Test
    public void uploadCertificateTest() throws Exception {
        Certification certification = TestUtils.createCertification();
        savingDiplomaMockedStatic = mockStatic(SavingDiploma.class);

        when(savingDiploma.addCertificate(anyString(), anyString())).thenReturn(remoteFunctionCall);
        when(encryptionService.encryptData(anyString(), anyString(), anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(1));
        savingDiplomaMockedStatic.when(() -> SavingDiploma.load(any(), any(), any(Credentials.class), any(), any())).thenReturn(savingDiploma);


        contractServiceImpl.uploadCertificate(certification, "address", ecKeyPair,"");
        verify(savingDiploma).addCertificate(certificateIdCaptor.capture(), certificateJsonCaptor.capture());
        String returnId = certificateIdCaptor.getValue();

        Certification returnValueCertification = new ObjectMapper().readValue(certificateJsonCaptor.getValue(), Certification.class);

        Student student = returnValueCertification.getStudent();

        assertNotNull(returnValueCertification);
        assertEquals(returnId, certification.getId());
        TestUtils.assertCertification(returnValueCertification);
        assertNull(returnValueCertification.getInstitution());
        assertNull(student.getInstitution());
        assertNull(student.getAddress());
        assertNull(student.getUsername());
        assertNull(student.getPassword());
        assertNull(student.getCertifications());
    }


}