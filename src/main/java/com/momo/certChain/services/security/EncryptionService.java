package com.momo.certChain.services.security;

import com.momo.certChain.exception.WrongPasswordException;
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

    public String decryptDataForCertificate(String encryptionKey, String valueToEncrypt, String salt){
        try{
            return getDecryptedValue(encryptionKey, valueToEncrypt, salt);

        }catch(IllegalStateException exception){
            throw new WrongPasswordException("Le certificat n'a pas pu etre décrypter , veuillez verifier l'URL ou le mot de passe du certificat");
        }
    }

    public String decryptData(String encryptionKey, String valueToEncrypt, String salt){
        try{
            return getDecryptedValue(encryptionKey, valueToEncrypt, salt);

        }catch(IllegalStateException exception){
            throw new WrongPasswordException("Mauvais mot de passe , veuillez en utiliser un autre");
        }
    }


    private String getDecryptedValue(String encryptionKey, String valueToEncrypt, String salt) {
        TextEncryptor encryptor = Encryptors.delux(encryptionKey, salt);
        return encryptor.decrypt(valueToEncrypt);
    }


    public String generateSalt() {
        return KeyGenerators.string().generateKey();
    }
}
