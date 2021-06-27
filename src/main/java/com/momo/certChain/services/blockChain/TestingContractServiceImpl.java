package com.momo.certChain.services.blockChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.mapping.SimpleCertificationMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
import com.momo.certChain.services.security.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.evm.EmbeddedWeb3jService;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Service("ContractService")
@Profile({"test","local"})
public class TestingContractServiceImpl implements ContractService {

    private final ObjectMapper objectMapper;

    private final EncryptionService encryptionService;

    private final Web3j web3j;

    private final Credentials credentials;

    public TestingContractServiceImpl(ObjectMapper objectMapper, EncryptionService encryptionService, Web3j web3j, Credentials credentials) {
        this.objectMapper = objectMapper;
        this.encryptionService = encryptionService;
        this.web3j = web3j;
        this.credentials = credentials;
    }

    public String uploadContract(ECKeyPair ecKeyPair) throws Exception {
        SavingDiploma savingDiploma = SavingDiploma.deploy(web3j, credentials, BigInteger.ZERO, Contract.GAS_LIMIT).send();
        return savingDiploma.getContractAddress();
    }

    public Certification getCertificate(String uuid, String address, ECKeyPair ecKeyPair,String privateKey,String salt) throws Exception {
        SavingDiploma savingDiploma = getUploadedContract(address);

        String certificateString = savingDiploma.get(uuid).send();

        String decryptedCertificateString = encryptionService.decryptData(privateKey,certificateString,salt);

        return objectMapper.readValue(decryptedCertificateString, Certification.class);
    }

    public void uploadCertificate(Certification certification, String address, ECKeyPair ecKeyPair,String encryptionKey) throws Exception {

        SavingDiploma savingDiploma = getUploadedContract(address);

        String certificateJson = objectMapper.writeValueAsString(SimpleCertificationMapper.instance.toSimple(certification));

        String encryptedJSON = encryptionService.encryptData(encryptionKey,certificateJson,certification.getSalt());

        savingDiploma.addCertificate(certification.getId(), encryptedJSON).send();

    }

    private SavingDiploma getUploadedContract(String address) {
        return SavingDiploma.load(address, web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

}
