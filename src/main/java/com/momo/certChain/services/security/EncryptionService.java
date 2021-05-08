package com.momo.certChain.services.security;

import com.momo.certChain.exception.WrongKeyException;
import com.momo.certChain.model.data.InstitutionWallet;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;

import java.math.BigInteger;

@Service
public class EncryptionService {

    public String encryptData(String encryptionKey,String valueToEncrypt,String salt){
        TextEncryptor encryptor = Encryptors.delux(encryptionKey,salt);
        return encryptor.encrypt(valueToEncrypt);
    }

    public String decryptData(String encryptionKey,String valueToEncrypt,String salt){
        try{
            TextEncryptor encryptor = Encryptors.delux(encryptionKey,salt);
            return encryptor.decrypt(valueToEncrypt);

        }catch(IllegalStateException exception){
            throw new WrongKeyException();
        }
    }


    public String generateSalt() {
        return KeyGenerators.string().generateKey();
    }
}
