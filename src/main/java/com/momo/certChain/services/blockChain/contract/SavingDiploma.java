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
    public static final String BINARY = "60806040523480156100115760006000fd5b505b60006100236100c860201b60201c565b905080600060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508073ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a3505b6100d5565b60003390506100d2565b90565b610dc1806100e46000396000f3fe60806040523480156100115760006000fd5b50600436106100675760003560e01c80635c44f0e51461006d578063715018a61461009d5780638da5cb5b146100a75780639507d39a146100c5578063ccb1173b146100f5578063f2fde38b1461011157610067565b60006000fd5b61008760048036038101906100829190610875565b61012d565b6040516100949190610a79565b60405180910390f35b6100a56101d0565b005b6100af61031d565b6040516100bc9190610a5d565b60405180910390f35b6100df60048036038101906100da9190610875565b61034c565b6040516100ec9190610a79565b60405180910390f35b61010f600480360381019061010a91906108a0565b6103fc565b005b61012b6004803603810190610126919061084a565b610533565b005b6001600050602052806000526040600020600091509050805461014f90610bfb565b80601f016020809104026020016040519081016040528092919081815260200182805461017b90610bfb565b80156101c85780601f1061019d576101008083540402835291602001916101c8565b820191906000526020600020905b8154815290600101906020018083116101ab57829003601f168201915b505050505081565b6101de6106f163ffffffff16565b73ffffffffffffffffffffffffffffffffffffffff1661020261031d63ffffffff16565b73ffffffffffffffffffffffffffffffffffffffff1614151561025a576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161025190610abd565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff16600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a36000600060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b5b565b6000600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050610349565b90565b606060016000506000838152602001908152602001600020600050805461037290610bfb565b80601f016020809104026020016040519081016040528092919081815260200182805461039e90610bfb565b80156103eb5780601f106103c0576101008083540402835291602001916103eb565b820191906000526020600020905b8154815290600101906020018083116103ce57829003601f168201915b505050505090506103f7565b919050565b61040a6106f163ffffffff16565b73ffffffffffffffffffffffffffffffffffffffff1661042e61031d63ffffffff16565b73ffffffffffffffffffffffffffffffffffffffff16141515610486576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161047d90610abd565b60405180910390fd5b60405160200161049590610a47565b6040516020818303038152906040528051906020012060001916600160005060008481526020019081526020016000206000506040516020016104d89190610a2f565b60405160208183030381529060405280519060200120600019161415156104ff5760006000fd5b8060016000506000848152602001908152602001600020600050908051906020019061052c9291906106fe565b505b5b5050565b6105416106f163ffffffff16565b73ffffffffffffffffffffffffffffffffffffffff1661056561031d63ffffffff16565b73ffffffffffffffffffffffffffffffffffffffff161415156105bd576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016105b490610abd565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff161415151561062f576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161062690610a9c565b60405180910390fd5b8073ffffffffffffffffffffffffffffffffffffffff16600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a380600060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b5b50565b60003390506106fb565b90565b82805461070a90610bfb565b90600052602060002090601f01602090048101928261072c5760008555610778565b82601f1061074557805160ff1916838001178555610778565b82800160010185558215610778579182015b828111156107775782518260005090905591602001919060010190610757565b5b5090506107859190610789565b5090565b61078e565b808211156107a8576000818150600090555060010161078e565b509056610d8a565b60006107c36107be84610b05565b610ade565b9050828152602081018484840111156107dc5760006000fd5b6107e7848285610bb6565b505b9392505050565b6000813590506107ff81610d54565b5b92915050565b600082601f830112151561081a5760006000fd5b813561082a8482602086016107b0565b9150505b92915050565b60008135905061084381610d6f565b5b92915050565b60006020828403121561085d5760006000fd5b600061086b848285016107f0565b9150505b92915050565b6000602082840312156108885760006000fd5b600061089684828501610834565b9150505b92915050565b60006000604083850312156108b55760006000fd5b60006108c385828601610834565b925050602083013567ffffffffffffffff8111156108e15760006000fd5b6108ed85828601610806565b9150505b9250929050565b61090181610b77565b825250505b565b600061091382610b4d565b61091d8185610b59565b935061092d818560208601610bc6565b61093681610cc4565b84019150505b92915050565b6000815461094f81610bfb565b6109598186610b6b565b945060018216600081146109745760018114610985576109b9565b60ff198316865281860193506109b9565b61098e85610b37565b60005b838110156109b1578154818901526001820191505b602081019050610991565b838801955050505b5050505b92915050565b60006109d0602683610b59565b91506109db82610cd6565b6040820190505b919050565b60006109f4602083610b59565b91506109ff82610d26565b6020820190505b919050565b6000610a18600083610b6b565b9150610a2382610d50565b6000820190505b919050565b6000610a3b8284610942565b91508190505b92915050565b6000610a5282610a0b565b91508190505b919050565b6000602082019050610a7260008301846108f8565b5b92915050565b60006020820190508181036000830152610a938184610908565b90505b92915050565b60006020820190508181036000830152610ab5816109c3565b90505b919050565b60006020820190508181036000830152610ad6816109e7565b90505b919050565b6000610ae8610afa565b9050610af48282610c30565b5b919050565b600060405190505b90565b600067ffffffffffffffff821115610b2057610b1f610c93565b5b610b2982610cc4565b90506020810190505b919050565b600081905081600052602060002090505b919050565b6000815190505b919050565b60008282526020820190505b92915050565b60008190505b92915050565b6000610b8282610b8a565b90505b919050565b600073ffffffffffffffffffffffffffffffffffffffff821690505b919050565b60008190505b919050565b828183376000838301525050505b565b60005b83811015610be55780820151818401525b602081019050610bc9565b83811115610bf4576000848401525b505050505b565b600060028204905060018216801515610c1557607f821691505b60208210811415610c2957610c28610c62565b5b505b919050565b610c3982610cc4565b810181811067ffffffffffffffff82111715610c5857610c57610c93565b5b806040525050505b565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b565b6000601f19601f83011690505b919050565b7f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160008201527f64647265737300000000000000000000000000000000000000000000000000006020820152505b565b7f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e65726000820152505b565b505b565b610d5d81610b77565b81141515610d6b5760006000fd5b505b565b610d7881610bab565b81141515610d865760006000fd5b505b565bfea26469706673582212205ba3eff74ef80c81a6175e4c6d0bb317fa3108d05eab2ea3cc7e0e78044eda8a64736f6c63430008040033";

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

    public RemoteFunctionCall<TransactionReceipt> addCertificate(BigInteger _certId, String _certValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDCERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_certId), 
                new org.web3j.abi.datatypes.Utf8String(_certValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> get(BigInteger _value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> institutionCertificates(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_INSTITUTIONCERTIFICATES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
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
