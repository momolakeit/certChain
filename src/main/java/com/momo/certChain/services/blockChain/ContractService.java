package com.momo.certChain.services.blockChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
import com.momo.certChain.services.security.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletUtils;
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

    public String uploadContract(ECKeyPair ecKeyPair) throws Exception {
        SavingDiploma savingDiploma = SavingDiploma.deploy(web3j, getCredentialsFromPrivateKey(ecKeyPair), BigInteger.valueOf(4100000000L), Contract.GAS_LIMIT).send();
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
        return SavingDiploma.load(address, web3j, getCredentialsFromPrivateKey(ecKeyPair), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    private Credentials getCredentialsFromPrivateKey(ECKeyPair ecKeyPair) {
        return Credentials.create(ecKeyPair);
    }
}
