package com.momo.certChain.configuration.blockchain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.evm.EmbeddedWeb3jService;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

@Configuration
public class BlockChainConfiguration {

    @Value("${blockchain.ethereum.inputUrl}")
    private String ethURL;

    @Bean
    @Profile("!test")
    public Web3j web3j(){
        return Web3j.build(new HttpService(ethURL));
    }

    @Bean
    @Profile("test")
    public Web3j testWeb3j() throws IOException, CipherException {
        Credentials credentials = WalletUtils.loadCredentials("Wallet password", "wallet.json");
        org.web3j.evm.Configuration configuration = new org.web3j.evm.Configuration(new Address(credentials.getAddress()), 10);
        return Web3j.build(new EmbeddedWeb3jService(configuration));
    }

}
