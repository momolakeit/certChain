package com.momo.certChain.services;

import com.momo.certChain.model.data.InstitutionWallet;
import com.momo.certChain.repositories.WalletRepository;
import com.momo.certChain.services.security.EncryptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {
    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private EncryptionService encryptionService;

    @Test
    public void createWalletTest() throws NoSuchProviderException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, CipherException {
        String salt = "salt";
        String password = "password";
        when(encryptionService.generateSalt()).thenReturn(salt);
        when(encryptionService.encryptData(anyString(),anyString(),anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(1));
        when(walletRepository.save(any(InstitutionWallet.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        InstitutionWallet institutionWallet = walletService.createWallet(password);
        verify(encryptionService,times(3)).encryptData(anyString(),anyString(),anyString());



        WalletFile walletFile = createWalletFile(new BigInteger(institutionWallet.getPrivateKey()),new BigInteger(institutionWallet.getPublicKey()),password);

        assertNotNull(institutionWallet.getPrivateKey());
        assertNotNull(institutionWallet.getPublicAddress());
        assertNotNull(institutionWallet.getPublicKey());
        assertEquals(walletFile.getAddress(),institutionWallet.getPublicAddress());

    }

    private WalletFile createWalletFile(BigInteger privateKey, BigInteger publicKey, String password) throws CipherException {
        return Wallet.createStandard(password,new ECKeyPair(privateKey,publicKey));
    }
}