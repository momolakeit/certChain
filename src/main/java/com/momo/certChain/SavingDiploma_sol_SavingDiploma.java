package com.momo.certChain;

import java.math.BigInteger;
import java.util.Arrays;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
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
public class SavingDiploma_sol_SavingDiploma extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b506040516103af3803806103af83398101604081905261002f916100e2565b8051610042906000906020840190610049565b50506101fc565b828054610055906101ab565b90600052602060002090601f01602090048101928261007757600085556100bd565b82601f1061009057805160ff19168380011785556100bd565b828001600101855582156100bd579182015b828111156100bd5782518255916020019190600101906100a2565b506100c99291506100cd565b5090565b5b808211156100c957600081556001016100ce565b600060208083850312156100f4578182fd5b82516001600160401b038082111561010a578384fd5b818501915085601f83011261011d578384fd5b81518181111561012f5761012f6101e6565b604051601f8201601f19908116603f01168101908382118183101715610157576101576101e6565b81604052828152888684870101111561016e578687fd5b8693505b8284101561018f5784840186015181850187015292850192610172565b8284111561019f57868684830101525b98975050505050505050565b600181811c908216806101bf57607f821691505b602082108114156101e057634e487b7160e01b600052602260045260246000fd5b50919050565b634e487b7160e01b600052604160045260246000fd5b6101a48061020b6000396000f3fe608060405234801561001057600080fd5b506004361061002b5760003560e01c80636d4ce63c14610030575b600080fd5b61003861004e565b60405161004591906100e0565b60405180910390f35b60606000805461005d90610133565b80601f016020809104026020016040519081016040528092919081815260200182805461008990610133565b80156100d65780601f106100ab576101008083540402835291602001916100d6565b820191906000526020600020905b8154815290600101906020018083116100b957829003601f168201915b5050505050905090565b6000602080835283518082850152825b8181101561010c578581018301518582016040015282016100f0565b8181111561011d5783604083870101525b50601f01601f1916929092016040019392505050565b600181811c9082168061014757607f821691505b6020821081141561016857634e487b7160e01b600052602260045260246000fd5b5091905056fea2646970667358221220b40fcab0502bd2a401480df16358d7385cb50c30856a3ce4244162f9ea7ac95264736f6c63430008030033";

    public static final String FUNC_GET = "get";

    @Deprecated
    protected SavingDiploma_sol_SavingDiploma(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SavingDiploma_sol_SavingDiploma(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected SavingDiploma_sol_SavingDiploma(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected SavingDiploma_sol_SavingDiploma(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<String> get() {
        final Function function = new Function(FUNC_GET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    @Deprecated
    public static SavingDiploma_sol_SavingDiploma load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SavingDiploma_sol_SavingDiploma(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static SavingDiploma_sol_SavingDiploma load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SavingDiploma_sol_SavingDiploma(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SavingDiploma_sol_SavingDiploma load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new SavingDiploma_sol_SavingDiploma(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SavingDiploma_sol_SavingDiploma load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SavingDiploma_sol_SavingDiploma(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<SavingDiploma_sol_SavingDiploma> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String _value) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_value)));
        return deployRemoteCall(SavingDiploma_sol_SavingDiploma.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<SavingDiploma_sol_SavingDiploma> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String _value) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_value)));
        return deployRemoteCall(SavingDiploma_sol_SavingDiploma.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<SavingDiploma_sol_SavingDiploma> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _value) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_value)));
        return deployRemoteCall(SavingDiploma_sol_SavingDiploma.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<SavingDiploma_sol_SavingDiploma> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _value) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_value)));
        return deployRemoteCall(SavingDiploma_sol_SavingDiploma.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }
}
