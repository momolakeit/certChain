package com.momo.certChain.services.security;

import com.momo.certChain.TestUtils;
import com.momo.certChain.model.data.InstitutionWallet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeyPairServiceTest {

    @InjectMocks
    private KeyPairService keyPairService;

    @Mock
    private EncryptionService encryptionService;

    @Test
    public void createKeyPair() throws NoSuchAlgorithmException, CipherException, InvalidAlgorithmParameterException, NoSuchProviderException {
        when(encryptionService.decryptData(anyString(),anyString(),anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(1));

        InstitutionWallet institutionWallet = TestUtils.createInstitutionWallet();

        ECKeyPair ecKeyPair = keyPairService.createKeyPair(institutionWallet.getPrivateKey(),
                                                           institutionWallet.getPublicKey(),
                                                           institutionWallet.getSalt(),
                                                          "walletPassword");

        assertEquals(ecKeyPair.getPrivateKey().toString(),institutionWallet.getPrivateKey());
        assertEquals(ecKeyPair.getPublicKey().toString(),institutionWallet.getPublicKey());
    }

}