package com.momo.certChain;

import com.momo.certChain.services.blockChain.ContractService;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CertChainApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CertChainApplication.class, args);
	}

}
