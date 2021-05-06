package com.momo.certChain.services.blockChain;

import com.momo.certChain.model.data.Certification;
import org.web3j.crypto.ECKeyPair;

public interface ContractService {
    String uploadContract(ECKeyPair ecKeyPair) throws Exception;

    Certification getCertificate(String uuid, String address, ECKeyPair ecKeyPair) throws Exception;

    void uploadCertificate(Certification certification, String address, ECKeyPair ecKeyPair,String encryptionKey) throws Exception;
}
