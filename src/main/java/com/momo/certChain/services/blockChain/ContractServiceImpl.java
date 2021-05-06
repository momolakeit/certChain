package com.momo.certChain.services.blockChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
import com.momo.certChain.services.security.EncryptionService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.math.BigInteger;

@Service
@Profile("!test")
public class ContractServiceImpl implements ContractService {

    private final ObjectMapper objectMapper;

    private final EncryptionService encryptionService;

    private final Web3j web3j;

    public ContractServiceImpl(ObjectMapper objectMapper, EncryptionService encryptionService, Web3j web3j) {
        this.objectMapper = objectMapper;
        this.encryptionService = encryptionService;
        this.web3j = web3j;
    }

    public String uploadContract(ECKeyPair ecKeyPair) throws Exception {
        SavingDiploma savingDiploma = SavingDiploma.deploy(web3j, getCredentialsFromPrivateKey(ecKeyPair), BigInteger.valueOf(4100000000L), Contract.GAS_LIMIT).send();
        return savingDiploma.getContractAddress();
    }

    public Certification getCertificate(String uuid, String address, ECKeyPair ecKeyPair) throws Exception {
        SavingDiploma savingDiploma = getUploadedContract(address, ecKeyPair);
        String certificateString = savingDiploma.get(uuid).send();
        return objectMapper.readValue(certificateString, Certification.class);
    }

    public void uploadCertificate(Certification certification, String address, ECKeyPair ecKeyPair,String encryptionKey) throws Exception {

        SavingDiploma savingDiploma = getUploadedContract(address, ecKeyPair);
        String certificateJson = objectMapper.writeValueAsString(certification);
        String encryptedJSON = encryptionService.encryptData(encryptionKey,certificateJson,certification.getSalt());
        savingDiploma.addCertificate(certification.getId(), encryptedJSON).send();

    }

    private SavingDiploma getUploadedContract(String address, ECKeyPair ecKeyPair) {
        return SavingDiploma.load(address, web3j, getCredentialsFromPrivateKey(ecKeyPair), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    private Credentials getCredentialsFromPrivateKey(ECKeyPair ecKeyPair) {
        return Credentials.create(ecKeyPair);
    }
}
