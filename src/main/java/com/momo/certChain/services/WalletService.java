package com.momo.certChain.services;

import com.momo.certChain.model.data.InstitutionWallet;
import com.momo.certChain.repositories.WalletRepository;
import com.momo.certChain.services.security.EncryptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Service
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;

    private final EncryptionService encryptionService;

    public WalletService(WalletRepository walletRepository, EncryptionService encryptionService) {
        this.walletRepository = walletRepository;
        this.encryptionService = encryptionService;
    }

    public InstitutionWallet createWallet(String walletPassword) throws NoSuchAlgorithmException, CipherException, InvalidAlgorithmParameterException, NoSuchProviderException {
        InstitutionWallet institutionWallet = initWalletKeyPair(walletPassword);

        institutionWallet.setSalt(encryptionService.generateSalt());
        institutionWallet.setPrivateKey(encryptionService.encryptData(walletPassword, institutionWallet.getPrivateKey(), institutionWallet.getSalt()));
        institutionWallet.setPublicKey(encryptionService.encryptData(walletPassword, institutionWallet.getPublicKey(), institutionWallet.getSalt()));

        return saveWallet(institutionWallet);
    }

    private InstitutionWallet saveWallet(InstitutionWallet institutionWallet){
        return walletRepository.save(institutionWallet);
    }
    private InstitutionWallet initWalletKeyPair(String password) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CipherException {
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();

        WalletFile walletFile = Wallet.createStandard(password, ecKeyPair);
        InstitutionWallet institutionWallet = new InstitutionWallet();
        institutionWallet.setPublicAddress(Numeric.prependHexPrefix(walletFile.getAddress()));
        Credentials.create(ecKeyPair);
        institutionWallet.setPrivateKey(ecKeyPair.getPrivateKey().toString());
        institutionWallet.setPublicKey(ecKeyPair.getPublicKey().toString());

        return institutionWallet;
    }



}
