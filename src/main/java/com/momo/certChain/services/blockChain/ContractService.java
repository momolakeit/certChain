package com.momo.certChain.services.blockChain;

import com.momo.certChain.SavingDiploma_sol_SavingDiploma;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.math.BigInteger;

@Service
public class ContractService {
    public void uploadContract(String value) throws Exception {
        Web3j web3j = Web3j.build(new HttpService("http://localhost:7545"));
        SavingDiploma_sol_SavingDiploma.deploy(web3j, getCredentialsFromPrivateKey(), ManagedTransaction.GAS_PRICE.multiply(BigInteger.TEN), Contract.GAS_LIMIT, value).send();
    }

    private Credentials getCredentialsFromPrivateKey() {
        return Credentials.create("8b01dbea8bcd96a3c7a5afd0c6e782812dbdbb9eed9f765358d739dfc66cb99f");
    }
}
