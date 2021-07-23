package com.momo.certChain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CertChainApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CertChainApplication.class, args);
	}

}
