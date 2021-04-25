package com.momo.certChain.services.blockChain;

import com.momo.certChain.SavingDiploma_sol_SavingDiploma;
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
    Web3j web3j = Web3j.build(new HttpService(ethURL));

    public void uploadContract(String certificateJson) throws Exception {
        SavingDiploma.deploy(web3j, getCredentialsFromPrivateKey(), BigInteger.valueOf(4100000000L), Contract.GAS_LIMIT).send();
    }

    public String getCertificate(BigInteger certId, String address) throws Exception {
        SavingDiploma savingDiploma = getUploadedContract(address);
        return savingDiploma.get(certId).send();
    }
    public void uploadCertificate(String certificateJson,String address, BigInteger randomNumber) throws Exception {
        SavingDiploma savingDiploma = getUploadedContract(address);
        savingDiploma.addCertificate(randomNumber,certificateJson).send();

    }


    private SavingDiploma getUploadedContract(String address){
        return SavingDiploma.load(address,web3j,getCredentialsFromPrivateKey(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
    }
    private Credentials getCredentialsFromPrivateKey() {
        return Credentials.create("eb9c0408f00aa45ef323f847bd293c62d3c1d789e76c8e2575b206dc95ca020a");
    }
}
