package com.momo.certChain.services.blockChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.math.BigInteger;

@Service
public class ContractService {

    @Value("${blockchain.ethereum.inputUrl}")
    private String ethURL ;

    private ObjectMapper objectMapper;

    public ContractService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    Web3j web3j = Web3j.build(new HttpService(ethURL));

    public void uploadContract(String certificateJson) throws Exception {
        SavingDiploma.deploy(web3j, getCredentialsFromPrivateKey(), BigInteger.valueOf(4100000000L), Contract.GAS_LIMIT).send();
    }

    public String getCertificate(String uuid, String address) throws Exception {
        SavingDiploma savingDiploma = getUploadedContract(address);
        return savingDiploma.get(uuid).send();
    }
    public void uploadCertificate(Student student,String address) throws Exception {
        Certification certification = student.getCertifications().get(student.getCertifications().size()-1);
        String certificateJson = objectMapper.writeValueAsString(student.getCertifications());
        SavingDiploma savingDiploma = getUploadedContract(address);
        savingDiploma.addCertificate(certification.getId(),certificateJson).send();

    }


    private SavingDiploma getUploadedContract(String address){
        return SavingDiploma.load(address,web3j,getCredentialsFromPrivateKey(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }
    private Credentials getCredentialsFromPrivateKey() {
        return Credentials.create("eb9c0408f00aa45ef323f847bd293c62d3c1d789e76c8e2575b206dc95ca020a");
    }
}
