package com.momo.certChain.model.externe.gasStation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GasInfo {
    private int safeLow;
    private int standard;
    private int fast;
    private int fastest;
    private int blockTime;
    private int blockNumber;
}
