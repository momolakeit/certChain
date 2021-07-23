package com.momo.certChain.services.feign;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigInteger;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GasCalculatorService {

    private final BigInteger fastGasPrice;

    private final BigInteger gasLimit;

    private final BigInteger multiplicateur = BigInteger.valueOf(1000000000L);

    public GasCalculatorService(GasStationClient gasStationClient) {
        this.fastGasPrice = calculerGasPrice(gasStationClient);
        this.gasLimit = fastGasPrice.divide(BigInteger.valueOf(1000));
    }

    public BigInteger getFastGasPrice() {
        return fastGasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    private BigInteger calculerGasPrice(GasStationClient gasStationClient) {
        return BigInteger.valueOf(gasStationClient.getGasInfo().getFast()).multiply(multiplicateur);
    }
}
