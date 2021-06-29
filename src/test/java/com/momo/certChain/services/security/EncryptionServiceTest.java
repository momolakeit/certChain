package com.momo.certChain.services.security;

import com.momo.certChain.exception.WrongPasswordException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.keygen.KeyGenerators;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EncryptionServiceTest {

    @InjectMocks
    private EncryptionService encryptionService;

    private final String valueToEncrypt = "encryptMe";

    private final String privateKey = "superSecure";


    @Test
    public void encryptDataTest() {
        String salt = KeyGenerators.string().generateKey();

        String returnValue = encryptionService.encryptData(privateKey, valueToEncrypt,salt);

        assertNotEquals(valueToEncrypt,returnValue);
    }

    @Test
    public void decryptDataTest() {
        String salt = KeyGenerators.string().generateKey();

        String encryptData = encryptionService.encryptData(privateKey, valueToEncrypt,salt);
        String decryptedData = encryptionService.decryptData(privateKey,encryptData,salt);

        assertEquals(valueToEncrypt,decryptedData);
    }

    @Test
    public void decryptWrongSaltThrowsExceptionDataTest() {
        String encryptData = encryptionService.encryptData(privateKey, valueToEncrypt,KeyGenerators.string().generateKey());

        Assertions.assertThrows(WrongPasswordException.class,()->{
            encryptionService.decryptData("mauvaise Enc Key",encryptData,KeyGenerators.string().generateKey());
        });
    }

    @Test
    public void decryptWrongEncKeyThrowsExceptionDataTest() {
        String salt = KeyGenerators.string().generateKey();

        String encryptData = encryptionService.encryptData(privateKey, valueToEncrypt,salt);

        Assertions.assertThrows(WrongPasswordException.class,()->{
            encryptionService.decryptData("mauvaise Enc Key",encryptData,salt);
        });
    }



    @Test
    public void decryptDataForCertificateTest() {
        String salt = KeyGenerators.string().generateKey();

        String encryptData = encryptionService.encryptData(privateKey, valueToEncrypt,salt);
        String decryptedData = encryptionService.decryptDataForCertificate(privateKey,encryptData,salt);

        assertEquals(valueToEncrypt,decryptedData);
    }

    @Test
    public void decryptWrongEncKeyThrowsExceptionDataForCertificateTest() {
        String salt = KeyGenerators.string().generateKey();

        String encryptData = encryptionService.encryptData(privateKey, valueToEncrypt,salt);

        Assertions.assertThrows(WrongPasswordException.class,()->{
            encryptionService.decryptDataForCertificate("mauvaise Enc Key",encryptData,salt);
        });
    }

    @Test
    public void decryptWrongSaltThrowsExceptionDataForCertificateTest() {
        String encryptData = encryptionService.encryptData(privateKey, valueToEncrypt,KeyGenerators.string().generateKey());

        Assertions.assertThrows(WrongPasswordException.class,()->{
            encryptionService.decryptDataForCertificate("mauvaise Enc Key",encryptData,KeyGenerators.string().generateKey());
        });
    }


    @Test
    public void generateSalt(){
        String salt1 = encryptionService.generateSalt();
        String salt2 = encryptionService.generateSalt();

        assertNotEquals(salt1,salt2);
    }

}