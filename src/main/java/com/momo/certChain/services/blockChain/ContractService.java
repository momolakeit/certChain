package com.momo.certChain.services.blockChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
import com.momo.certChain.services.security.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.math.BigInteger;
import java.util.UUID;

@Service
public class ContractService {

    @Value("${blockchain.ethereum.inputUrl}")
    private String ethURL;

    private final ObjectMapper objectMapper;

    private final EncryptionService encryptionService;

    public ContractService(ObjectMapper objectMapper, EncryptionService encryptionService) {
        this.objectMapper = objectMapper;
        this.encryptionService = encryptionService;
    }

    Web3j web3j = Web3j.build(new HttpService(ethURL));

    public String uploadContract(String privateKey) throws Exception {
        SavingDiploma savingDiploma = SavingDiploma.deploy(web3j, getCredentialsFromPrivateKey(privateKey), BigInteger.valueOf(4100000000L), Contract.GAS_LIMIT).send();
        return savingDiploma.getContractAddress();
    }

    public Certification getCertificate(String uuid, String address, String privateKey) throws Exception {
        SavingDiploma savingDiploma = getUploadedContract(address, privateKey);
        String certificateString = savingDiploma.get(uuid).send();
        return objectMapper.readValue(certificateString, Certification.class);
    }

    public void uploadCertificate(Certification certification, String address, String privateKey) throws Exception {
        SavingDiploma savingDiploma = getUploadedContract(address, privateKey);
        String certificateJson = objectMapper.writeValueAsString(certification);
        String encryptedJSON = encryptionService.encryptData(privateKey,certificateJson,certification.getSalt());
        savingDiploma.addCertificate(certification.getId(), encryptedJSON).send();

    }

    private SavingDiploma getUploadedContract(String address, String privateKey) {
        return SavingDiploma.load(address, web3j, getCredentialsFromPrivateKey(privateKey), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    private Credentials getCredentialsFromPrivateKey(String privateKey) {
        return Credentials.create(privateKey);
    }
}
