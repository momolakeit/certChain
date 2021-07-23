package com.momo.certChain.services.blockChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.mapping.SimpleCertificationMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
import com.momo.certChain.services.security.EncryptionService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

@Service
@Profile({"!test & !local"})
public class ContractServiceImpl implements ContractService {

    private final ObjectMapper objectMapper;

    private final EncryptionService encryptionService;

    private final Web3j web3j;

    private final BigInteger gasPrice;

    private final BigInteger gasLimit;

    private final Long chainID;

    public ContractServiceImpl(ObjectMapper objectMapper,
                               EncryptionService encryptionService,
                               Web3j web3j,
                               @Value("${blockchain.gasPrice}") BigInteger gasPrice,
                               @Value("${blockchain.gasLimit}") BigInteger gasLimit,
                               @Value("${blockchain.chainID}") Long chainID) {
        this.objectMapper = objectMapper;
        this.encryptionService = encryptionService;
        this.web3j = web3j;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.chainID = chainID;
    }

    public String uploadContract(ECKeyPair ecKeyPair) throws Exception {
        SavingDiploma savingDiploma = SavingDiploma.deploy(web3j, getFastRawTransactionManager(ecKeyPair), getStaticGasProvider()).send();
        return savingDiploma.getContractAddress();
    }

    public Certification getCertificate(String uuid, String address, ECKeyPair ecKeyPair,String privateKey,String salt) throws Exception {
        SavingDiploma savingDiploma = getUploadedContract(address, ecKeyPair);

        String certificateString = savingDiploma.get(uuid).send();

        String decryptedCertificateString = encryptionService.decryptDataForCertificate(privateKey,certificateString,salt);

        return objectMapper.readValue(decryptedCertificateString, Certification.class);
    }

    public void uploadCertificate(Certification certification, String address, ECKeyPair ecKeyPair,String encryptionKey) throws Exception {
        SavingDiploma savingDiploma = getUploadedContract(address, ecKeyPair);

        String certificateJson = objectMapper.writeValueAsString(SimpleCertificationMapper.instance.toSimple(certification));

        String encryptedJSON = encryptionService.encryptData(encryptionKey,certificateJson,certification.getSalt());

        savingDiploma.addCertificate(certification.getId(), encryptedJSON).send();

    }

    private SavingDiploma getUploadedContract(String address, ECKeyPair ecKeyPair) {
        return SavingDiploma.load(address, web3j, getFastRawTransactionManager(ecKeyPair),getStaticGasProvider());
    }

    private Credentials getCredentialsFromPrivateKey(ECKeyPair ecKeyPair) {
        return Credentials.create(ecKeyPair);
    }

    public FastRawTransactionManager getFastRawTransactionManager(ECKeyPair ecKeyPair) {
        return new FastRawTransactionManager(web3j, getCredentialsFromPrivateKey(ecKeyPair), chainID);
    }

    public StaticGasProvider getStaticGasProvider() {
        return new StaticGasProvider(gasPrice,gasLimit);
    }
}
