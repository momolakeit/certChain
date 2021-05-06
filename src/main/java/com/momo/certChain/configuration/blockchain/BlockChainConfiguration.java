package com.momo.certChain.configuration.blockchain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.evm.EmbeddedWeb3jService;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Configuration
public class BlockChainConfiguration {

    @Value("${blockchain.ethereum.inputUrl}")
    private String ethURL;


    @Bean
    @Profile("!test")
    public Web3j web3j(){
        return Web3j.build(new HttpService(ethURL));
    }

    @Bean("web3j")
    @Profile("test")
    public Web3j testWeb3j() throws  InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        org.web3j.evm.Configuration configuration = new org.web3j.evm.Configuration(new Address(credentials().getAddress()), 30);
        return Web3j.build(new EmbeddedWeb3jService(configuration));
    }

    @Bean
    @Profile("test")
    public Credentials credentials() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return Credentials.create(Keys.createEcKeyPair());
    }
}
