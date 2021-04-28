package com.momo.certChain.services.blockChain.contract;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class SavingDiploma extends Contract {
    public static final String BINARY = "60806040523480156100115760006000fd5b505b60006100236100c860201b60201c565b905080600060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508073ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a3505b6100d5565b60003390506100d2565b90565b610e38806100e46000396000f3fe60806040523480156100115760006000fd5b50600436106100675760003560e01c8063693ec85e1461006d578063715018a61461009d5780638da5cb5b146100a7578063cd11f691146100c5578063e24f2ce7146100f5578063f2fde38b1461011157610067565b60006000fd5b61008760048036038101906100829190610896565b61012d565b6040516100949190610b16565b60405180910390f35b6100a56101e8565b005b6100af610335565b6040516100bc9190610afa565b60405180910390f35b6100df60048036038101906100da9190610896565b610364565b6040516100ec9190610b16565b60405180910390f35b61010f600480360381019061010a91906108da565b61041d565b005b61012b6004803603810190610126919061086b565b61056a565b005b60606001600050826040516101429190610ab4565b9081526020016040518091039020600050805461015e90610c8d565b80601f016020809104026020016040519081016040528092919081815260200182805461018a90610c8d565b80156101d75780601f106101ac576101008083540402835291602001916101d7565b820191906000526020600020905b8154815290600101906020018083116101ba57829003601f168201915b505050505090506101e3565b919050565b6101f661072863ffffffff16565b73ffffffffffffffffffffffffffffffffffffffff1661021a61033563ffffffff16565b73ffffffffffffffffffffffffffffffffffffffff16141515610272576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161026990610b5a565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff16600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a36000600060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b5b565b6000600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050610361565b90565b600160005081805160208101820180518482526020830160208501208183528095505050505050600091509050805461039c90610c8d565b80601f01602080910402602001604051908101604052809291908181526020018280546103c890610c8d565b80156104155780601f106103ea57610100808354040283529160200191610415565b820191906000526020600020905b8154815290600101906020018083116103f857829003601f168201915b505050505081565b61042b61072863ffffffff16565b73ffffffffffffffffffffffffffffffffffffffff1661044f61033563ffffffff16565b73ffffffffffffffffffffffffffffffffffffffff161415156104a7576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161049e90610b5a565b60405180910390fd5b6040516020016104b690610ae4565b60405160208183030381529060405280519060200120600019166001600050836040516104e39190610ab4565b90815260200160405180910390206000506040516020016105049190610acc565b604051602081830303815290604052805190602001206000191614151561052b5760006000fd5b8060016000508360405161053f9190610ab4565b90815260200160405180910390206000509080519060200190610563929190610735565b505b5b5050565b61057861072863ffffffff16565b73ffffffffffffffffffffffffffffffffffffffff1661059c61033563ffffffff16565b73ffffffffffffffffffffffffffffffffffffffff161415156105f4576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016105eb90610b5a565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614151515610666576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161065d90610b39565b60405180910390fd5b8073ffffffffffffffffffffffffffffffffffffffff16600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a380600060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b5b50565b6000339050610732565b90565b82805461074190610c8d565b90600052602060002090601f01602090048101928261076357600085556107af565b82601f1061077c57805160ff19168380011785556107af565b828001600101855582156107af579182015b828111156107ae578251826000509090559160200191906001019061078e565b5b5090506107bc91906107c0565b5090565b6107c5565b808211156107df57600081815060009055506001016107c5565b509056610e01565b60006107fa6107f584610ba2565b610b7b565b9050828152602081018484840111156108135760006000fd5b61081e848285610c48565b505b9392505050565b60008135905061083681610de6565b5b92915050565b600082601f83011215156108515760006000fd5b81356108618482602086016107e7565b9150505b92915050565b60006020828403121561087e5760006000fd5b600061088c84828501610827565b9150505b92915050565b6000602082840312156108a95760006000fd5b600082013567ffffffffffffffff8111156108c45760006000fd5b6108d08482850161083d565b9150505b92915050565b60006000604083850312156108ef5760006000fd5b600083013567ffffffffffffffff81111561090a5760006000fd5b6109168582860161083d565b925050602083013567ffffffffffffffff8111156109345760006000fd5b6109408582860161083d565b9150505b9250929050565b61095481610c14565b825250505b565b600061096682610bea565b6109708185610bf6565b9350610980818560208601610c58565b61098981610d56565b84019150505b92915050565b60006109a082610bea565b6109aa8185610c08565b93506109ba818560208601610c58565b8084019150505b92915050565b600081546109d481610c8d565b6109de8186610c08565b945060018216600081146109f95760018114610a0a57610a3e565b60ff19831686528186019350610a3e565b610a1385610bd4565b60005b83811015610a36578154818901526001820191505b602081019050610a16565b838801955050505b5050505b92915050565b6000610a55602683610bf6565b9150610a6082610d68565b6040820190505b919050565b6000610a79602083610bf6565b9150610a8482610db8565b6020820190505b919050565b6000610a9d600083610c08565b9150610aa882610de2565b6000820190505b919050565b6000610ac08284610995565b91508190505b92915050565b6000610ad882846109c7565b91508190505b92915050565b6000610aef82610a90565b91508190505b919050565b6000602082019050610b0f600083018461094b565b5b92915050565b60006020820190508181036000830152610b30818461095b565b90505b92915050565b60006020820190508181036000830152610b5281610a48565b90505b919050565b60006020820190508181036000830152610b7381610a6c565b90505b919050565b6000610b85610b97565b9050610b918282610cc2565b5b919050565b600060405190505b90565b600067ffffffffffffffff821115610bbd57610bbc610d25565b5b610bc682610d56565b90506020810190505b919050565b600081905081600052602060002090505b919050565b6000815190505b919050565b60008282526020820190505b92915050565b60008190505b92915050565b6000610c1f82610c27565b90505b919050565b600073ffffffffffffffffffffffffffffffffffffffff821690505b919050565b828183376000838301525050505b565b60005b83811015610c775780820151818401525b602081019050610c5b565b83811115610c86576000848401525b505050505b565b600060028204905060018216801515610ca757607f821691505b60208210811415610cbb57610cba610cf4565b5b505b919050565b610ccb82610d56565b810181811067ffffffffffffffff82111715610cea57610ce9610d25565b5b806040525050505b565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b565b6000601f19601f83011690505b919050565b7f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160008201527f64647265737300000000000000000000000000000000000000000000000000006020820152505b565b7f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e65726000820152505b565b505b565b610def81610c14565b81141515610dfd5760006000fd5b505b565bfea264697066735822122066913776279a9d0aba0e3a0eb871b17246399469548ba40244a6e65332d1074f64736f6c63430008040033";

    public static final String FUNC_ADDCERTIFICATE = "addCertificate";

    public static final String FUNC_GET = "get";

    public static final String FUNC_INSTITUTIONCERTIFICATES = "institutionCertificates";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected SavingDiploma(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SavingDiploma(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected SavingDiploma(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected SavingDiploma(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> addCertificate(String _certId, String _certValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDCERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_certId), 
                new org.web3j.abi.datatypes.Utf8String(_certValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> get(String _value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_value)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> institutionCertificates(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_INSTITUTIONCERTIFICATES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static SavingDiploma load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SavingDiploma(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static SavingDiploma load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SavingDiploma(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SavingDiploma load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new SavingDiploma(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SavingDiploma load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SavingDiploma(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<SavingDiploma> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SavingDiploma.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<SavingDiploma> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SavingDiploma.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<SavingDiploma> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SavingDiploma.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<SavingDiploma> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SavingDiploma.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }
}
