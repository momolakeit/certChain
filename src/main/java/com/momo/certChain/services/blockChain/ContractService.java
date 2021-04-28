package com.momo.certChain.services.blockChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
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

    private ObjectMapper objectMapper;

    public ContractService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    Web3j web3j = Web3j.build(new HttpService("http://localhost:7545"));

    public SavingDiploma uploadContract(String privateKey) throws Exception {
        return SavingDiploma.deploy(web3j, getCredentialsFromPrivateKey(privateKey), BigInteger.valueOf(4100000000L), Contract.GAS_LIMIT).send();
    }

    public String getCertificate(String uuid, String address, String privateKey) throws Exception {
        SavingDiploma savingDiploma = getUploadedContract(address, privateKey);
        return savingDiploma.get(uuid).send();
    }

    public String uploadCertificate(Certification certification, String address, String privateKey) throws Exception {
        String certificateJson = objectMapper.writeValueAsString(certification);
        SavingDiploma savingDiploma = getUploadedContract(address, privateKey);
        return uploadDiploma(certificateJson, savingDiploma).toString();

    }

    private UUID uploadDiploma(String certificateJson, SavingDiploma savingDiploma) throws Exception {
        UUID uuid;
        while (true) {
            try {
                uuid = UUID.randomUUID();
                savingDiploma.addCertificate(uuid.toString(), certificateJson).send();
                break;
            } catch (Exception e) {
                if (!e.getMessage().contains("revert")) {
                    throw e;
                }
            }
        }
        return uuid;
    }


    private SavingDiploma getUploadedContract(String address, String privateKey) {
        return SavingDiploma.load(address, web3j, getCredentialsFromPrivateKey(privateKey), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }

    private Credentials getCredentialsFromPrivateKey(String privateKey) {
        return Credentials.create(privateKey);
    }
}
