package com.momo.certChain.services.feign;

import com.momo.certChain.model.externe.gasStation.GasInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "gasStation", url = "https://gasstation-mumbai.matic.today/")
public interface GasStationClient {

    @RequestMapping(method = RequestMethod.GET)
    GasInfo getGasInfo();
}
