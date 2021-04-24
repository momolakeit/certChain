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
    public static final String BINARY = "[\r\n"
            + "\t{\r\n"
            + "\t\t\"anonymous\": false,\r\n"
            + "\t\t\"inputs\": [\r\n"
            + "\t\t\t{\r\n"
            + "\t\t\t\t\"indexed\": true,\r\n"
            + "\t\t\t\t\"internalType\": \"address\",\r\n"
            + "\t\t\t\t\"name\": \"previousOwner\",\r\n"
            + "\t\t\t\t\"type\": \"address\"\r\n"
            + "\t\t\t},\r\n"
            + "\t\t\t{\r\n"
            + "\t\t\t\t\"indexed\": true,\r\n"
            + "\t\t\t\t\"internalType\": \"address\",\r\n"
            + "\t\t\t\t\"name\": \"newOwner\",\r\n"
            + "\t\t\t\t\"type\": \"address\"\r\n"
            + "\t\t\t}\r\n"
            + "\t\t],\r\n"
            + "\t\t\"name\": \"OwnershipTransferred\",\r\n"
            + "\t\t\"type\": \"event\"\r\n"
            + "\t},\r\n"
            + "\t{\r\n"
            + "\t\t\"inputs\": [],\r\n"
            + "\t\t\"name\": \"owner\",\r\n"
            + "\t\t\"outputs\": [\r\n"
            + "\t\t\t{\r\n"
            + "\t\t\t\t\"internalType\": \"address\",\r\n"
            + "\t\t\t\t\"name\": \"\",\r\n"
            + "\t\t\t\t\"type\": \"address\"\r\n"
            + "\t\t\t}\r\n"
            + "\t\t],\r\n"
            + "\t\t\"stateMutability\": \"view\",\r\n"
            + "\t\t\"type\": \"function\"\r\n"
            + "\t},\r\n"
            + "\t{\r\n"
            + "\t\t\"inputs\": [],\r\n"
            + "\t\t\"name\": \"renounceOwnership\",\r\n"
            + "\t\t\"outputs\": [],\r\n"
            + "\t\t\"stateMutability\": \"nonpayable\",\r\n"
            + "\t\t\"type\": \"function\"\r\n"
            + "\t},\r\n"
            + "\t{\r\n"
            + "\t\t\"inputs\": [\r\n"
            + "\t\t\t{\r\n"
            + "\t\t\t\t\"internalType\": \"address\",\r\n"
            + "\t\t\t\t\"name\": \"newOwner\",\r\n"
            + "\t\t\t\t\"type\": \"address\"\r\n"
            + "\t\t\t}\r\n"
            + "\t\t],\r\n"
            + "\t\t\"name\": \"transferOwnership\",\r\n"
            + "\t\t\"outputs\": [],\r\n"
            + "\t\t\"stateMutability\": \"nonpayable\",\r\n"
            + "\t\t\"type\": \"function\"\r\n"
            + "\t}\r\n"
            + "]";

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
