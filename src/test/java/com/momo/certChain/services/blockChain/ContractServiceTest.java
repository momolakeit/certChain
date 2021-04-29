package com.momo.certChain.services.blockChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.TestUtils;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
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
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    private ContractService contractService;

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

    @Captor
    private ArgumentCaptor<String> certificateJsonCaptor;

    @Captor
    private ArgumentCaptor<String> certificateIdCaptor;

    MockedStatic<Credentials> credentialsMockedStatic;

    MockedStatic<SavingDiploma> savingDiplomaMockedStatic;

    @BeforeEach
    public void init() {
        contractService = new ContractService(new ObjectMapper());
        credentialsMockedStatic = mockStatic(Credentials.class);
        credentialsMockedStatic.when(()-> Credentials.create(anyString())).thenReturn(credentials);
    }

    @AfterEach
    public void close(){
        credentialsMockedStatic.closeOnDemand();
        savingDiplomaMockedStatic.closeOnDemand();
    }

    @Test
    public void uploadContractTest() throws Exception {
        when(savingDiploma.getContractAddress()).thenReturn(contractAddress);
        when(remoteCall.send()).thenReturn(savingDiploma);
        savingDiplomaMockedStatic = mockStatic(SavingDiploma.class);
        savingDiplomaMockedStatic.when(() -> SavingDiploma.deploy(any(), any(Credentials.class), any(), any())).thenReturn(remoteCall);

        String returncontractAdress = contractService.uploadContract(privateKey);
        assertEquals(contractAddress,returncontractAdress);
    }

    @Test
    public void getCertificate() throws Exception {
        Certification certification = TestUtils.createCertification();
        when(savingDiploma.get(anyString())).thenReturn(remoteFunctionCall);
        when(remoteFunctionCall.send()).thenReturn(new ObjectMapper().writeValueAsString(certification));
        savingDiplomaMockedStatic = mockStatic(SavingDiploma.class);


        savingDiplomaMockedStatic.when(() -> SavingDiploma.load(any(),any(), any(Credentials.class), any(), any())).thenReturn(savingDiploma);


        Certification returnValueCertification = contractService.getCertificate("uuid","address",privateKey);
        assertNotNull(returnValueCertification);
        TestUtils.assertCertification(certification);
        TestUtils.assertInstitution(returnValueCertification.getInstitution());
    }

    @Test
    public void uploadCertificateTest() throws Exception {
        Certification certification = TestUtils.createCertification();
        savingDiplomaMockedStatic = mockStatic(SavingDiploma.class);
        when(savingDiploma.addCertificate(anyString(),anyString())).thenReturn(remoteFunctionCall);
        savingDiplomaMockedStatic.when(() -> SavingDiploma.load(any(),any(), any(Credentials.class), any(), any())).thenReturn(savingDiploma);


        contractService.uploadCertificate(certification,"address",privateKey);
        verify(savingDiploma).addCertificate(certificateIdCaptor.capture(),certificateJsonCaptor.capture());
        String returnId = certificateIdCaptor.getValue();

        Certification returnValueCertification = new ObjectMapper().readValue(certificateJsonCaptor.getValue(),Certification.class);

        assertNotNull(returnValueCertification);
        assertEquals(returnId,certification.getId());
        TestUtils.assertCertification(returnValueCertification);
        TestUtils.assertInstitution(returnValueCertification.getInstitution());
    }




}