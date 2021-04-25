package com.momo.certChain;

import com.momo.certChain.services.blockChain.ContractService;
import com.momo.certChain.services.blockChain.contract.SavingDiploma;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CertChainApplication {

	public static void main(String[] args) throws Exception {
		ContractService contractService = new ContractService();
		contractService.uploadContract("wesh");
		SpringApplication.run(CertChainApplication.class, args);
	}

}
