package com.momo.certChain.services.security;

import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;

import java.math.BigInteger;
@Service
public class KeyPairService {

    private final EncryptionService encryptionService;

    public KeyPairService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public ECKeyPair createKeyPair(String privateKeyValue, String publicKeyValue, String salt, String walletPassword){
        String privateKey = encryptionService.decryptData(walletPassword,privateKeyValue,salt);
        String publicKey = encryptionService.decryptData(walletPassword,publicKeyValue,salt);

        return createKeyPair(privateKey,publicKey);
    }

    private ECKeyPair createKeyPair(String privateKey, String publicKey){
        return new ECKeyPair(new BigInteger(privateKey),new BigInteger(publicKey));
    }
}
