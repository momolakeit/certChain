package com.momo.certChain.services.feign;

import com.momo.certChain.model.externe.gasStation.GasInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GasCalculatorServiceTest {

    @Mock
    private GasStationClient gasStationClient;

    private GasCalculatorService gasCalculatorService;

    @Test
    public void testerCalculDuGasPrice(){
        when(gasStationClient.getGasInfo()).thenReturn(createGasInfo());
        gasCalculatorService = new GasCalculatorService(gasStationClient);

        assertEquals(BigInteger.valueOf(3000000000L),gasCalculatorService.getFastGasPrice());
        assertEquals(BigInteger.valueOf(3000000L),gasCalculatorService.getGasLimit());
    }

    private GasInfo createGasInfo(){
        GasInfo gasInfo = new GasInfo();
        gasInfo.setFast(3);
        return gasInfo;
    }

}