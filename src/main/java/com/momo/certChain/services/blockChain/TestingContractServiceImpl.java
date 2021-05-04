package com.momo.certChain.services.blockChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
import com.momo.certChain.services.security.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.evm.EmbeddedWeb3jService;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Service("ContractService")
@Profile("test")
public class TestingContractServiceImpl implements ContractService {

    private final ObjectMapper objectMapper;

    private final EncryptionService encryptionService;

    private final Web3j web3j;

    private final Credentials credentials;

    public TestingContractServiceImpl(ObjectMapper objectMapper, EncryptionService encryptionService) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        this.objectMapper = objectMapper;
        this.encryptionService = encryptionService;
        credentials = Credentials.create(Keys.createEcKeyPair());
        org.web3j.evm.Configuration configuration = new org.web3j.evm.Configuration(new Address(credentials.getAddress()), 10);
        this.web3j = Web3j.build(new EmbeddedWeb3jService(configuration));
    }

    public String uploadContract(ECKeyPair ecKeyPair) throws Exception {
        SavingDiploma savingDiploma = SavingDiploma.deploy(web3j, credentials, BigInteger.valueOf(4100000000L), Contract.GAS_LIMIT).send();
        return savingDiploma.getContractAddress();
    }

    public Certification getCertificate(String uuid, String address, ECKeyPair ecKeyPair) throws Exception {
        SavingDiploma savingDiploma = getUploadedContract(address, ecKeyPair);
        String certificateString = savingDiploma.get(uuid).send();
        return objectMapper.readValue(certificateString, Certification.class);
    }

    public void uploadCertificate(Certification certification, String address, ECKeyPair ecKeyPair) throws Exception {

        SavingDiploma savingDiploma = getUploadedContract(address, ecKeyPair);
        String certificateJson = objectMapper.writeValueAsString(certification);
        String encryptedJSON = encryptionService.encryptData(ecKeyPair.getPrivateKey().toString(),certificateJson,certification.getSalt());
        savingDiploma.addCertificate(certification.getId(), encryptedJSON).send();

    }

    private SavingDiploma getUploadedContract(String address, ECKeyPair ecKeyPair) {
        return SavingDiploma.load(address, web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    private Credentials getCredentialsFromPrivateKey(ECKeyPair ecKeyPair) {
        return Credentials.create(ecKeyPair);
    }
}
