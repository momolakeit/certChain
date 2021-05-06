package com.momo.certChain.services.blockChain.contract;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.web3j.EVMTest;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;


import static org.junit.jupiter.api.Assertions.*;

@EVMTest
class SavingDiplomaTest {

    String certificate = "certificate";
    private final String randString = "randString";

    @Test
    public void savingDiplomaTest(Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) throws Exception {
        SavingDiploma savingDiploma = SavingDiploma.deploy(web3j, transactionManager, gasProvider).send();
        savingDiploma.addCertificate(randString, certificate).send();

        String value = savingDiploma.get(randString).send();

        assertEquals(certificate, value);
    }

    @Test
    public void savingDiplomaTestWrongIdReturnEmptyString(Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) throws Exception {
        SavingDiploma savingDiploma = SavingDiploma.deploy(web3j, transactionManager, gasProvider).send();
        savingDiploma.addCertificate(randString, certificate).send();

        String value = savingDiploma.get("notRandomom").send();

        assertEquals("", value);
    }

    @Test
    public void savingDiplomaTestDifferentAdressLanceException(Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) throws Exception {
        SavingDiploma savingDiploma = SavingDiploma.deploy(web3j, transactionManager, gasProvider).send();
        SavingDiploma savingDiploma1 = SavingDiploma.load(savingDiploma.getContractAddress(), web3j, getCredentialsFromPrivateKey(), gasProvider);

        Assertions.assertThrows(Exception.class, () -> {
            savingDiploma1.addCertificate(randString, certificate).send();
        });
    }

    @Test
    public void savingDiplomaTestAddSameCertIdTwice(Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) throws Exception {
        SavingDiploma savingDiploma = SavingDiploma.deploy(web3j, transactionManager, gasProvider).send();
        savingDiploma.addCertificate(randString, certificate).send();

        Assertions.assertThrows(Exception.class, () -> {
            savingDiploma.addCertificate(randString, certificate).send();
        });
    }

    private Credentials getCredentialsFromPrivateKey() {
        return Credentials.create("eb9c0408f00aa45ef323f847bd293c62d3c1d789e76c8e2575b206dc95ca020a");
    }
}