package com.momo.certChain.services.security;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    public String encryptData(String encryptionKey,String valueToEncrypt,String salt){
        TextEncryptor encryptor = Encryptors.delux(encryptionKey,salt);
        return encryptor.encrypt(valueToEncrypt);
    }

    public String decryptData(String encryptionKey,String valueToEncrypt,String salt){
        TextEncryptor encryptor = Encryptors.delux(encryptionKey,salt);
        return encryptor.decrypt(valueToEncrypt);
    }

    public String generateSalt() {
        return KeyGenerators.string().generateKey();
    }
}
