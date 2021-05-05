package com.momo.certChain.services.blockChain;

import com.momo.certChain.model.data.Certification;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.math.BigInteger;

public interface ContractService {
    String uploadContract(ECKeyPair ecKeyPair) throws Exception;

    Certification getCertificate(String uuid, String address, ECKeyPair ecKeyPair) throws Exception;

    void uploadCertificate(Certification certification, String address, ECKeyPair ecKeyPair) throws Exception;
}
