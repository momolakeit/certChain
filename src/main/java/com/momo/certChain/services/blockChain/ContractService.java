package com.momo.certChain.services.blockChain;

import com.momo.certChain.SavingDiploma_sol_SavingDiploma;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.math.BigInteger;

@Service
public class ContractService {
    Web3j web3j = Web3j.build(new HttpService("http://localhost:7545"));

    public void uploadContract(String certificateJson) throws Exception {
        SavingDiploma savingDiploma = SavingDiploma.deploy(web3j, getCredentialsFromPrivateKey(), ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT).send();
        savingDiploma.addCertificate(BigInteger.valueOf(665556565565611565L),certificateJson).send();
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
        return Credentials.create("039b0a9d87bbe82c4fc0e234615d6ddda5e24f44c129267ef67906557e53a4bd");
    }
}
