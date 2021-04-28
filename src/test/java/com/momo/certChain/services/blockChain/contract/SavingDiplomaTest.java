package com.momo.certChain.services.blockChain.contract;

import org.junit.jupiter.api.Test;
import org.web3j.EVMTest;
import org.web3j.protocol.Web3j;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@EVMTest
public class SavingDiplomaTest {

    String certificate = "certificate";
    private final String randString = "randString";

    @Test
    public void savingDiplomaTest (Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) throws Exception {
        SavingDiploma savingDiploma = SavingDiploma.deploy(web3j,transactionManager,gasProvider).send();
        savingDiploma.addCertificate(randString,certificate).send();
        String value =savingDiploma.get(randString).send();
        assertEquals(certificate,value);
    }
    @Test
    public void savingDiplomaTestWrongIdReturnEmptyString (Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) throws Exception {
        SavingDiploma savingDiploma = SavingDiploma.deploy(web3j,transactionManager,gasProvider).send();
        savingDiploma.addCertificate(randString,certificate).send();
        String value =savingDiploma.get("notRandomom").send();
        assertEquals("",value);
    }

}