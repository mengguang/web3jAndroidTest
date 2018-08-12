package org.xmgu2008.mengguang.web3jandroidtest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.NetVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;


@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    @ViewById
    TextView logMessage;

    @Click
    void startButton() {
        startWeb3jTest();
    }

    @Background
    void startWeb3jTest() {
        String rpcUrl = "rpc url here.";
        writeLogMessage(rpcUrl);
        Web3j web3 = Web3jFactory.build(new HttpService(rpcUrl));
        NetVersion netVersion = null;
        try {
            netVersion = web3.netVersion().send();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String clientVersion = netVersion.getNetVersion();
        writeLogMessage(clientVersion);

        String fileName;
        String cwd = getApplicationContext().getDataDir().toString();
        writeLogMessage(cwd);
//        try {
//            fileName = WalletUtils.generateNewWalletFile(
//                    "123qwe",
//                    new File(cwd),false);
//            System.out.println(fileName);
//            writeLogMessage(fileName);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }

        fileName = "UTC--2018-08-12T17-19-36.059--356f6402cc10154593e36ddc0107989b5a6d9608.json";
        Credentials credentials = null;
        try {
            credentials = WalletUtils.loadCredentials(
                    "123qwe",
                    cwd + "/" +fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String fromAddress = credentials.getAddress();
        writeLogMessage(fromAddress);

        EthGetBalance balance = null;
        try {
            balance = web3.ethGetBalance(fromAddress, DefaultBlockParameterName.LATEST).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BigInteger b = balance.getBalance();
        writeLogMessage(b.toString());

        // get the next available nonce
        EthGetTransactionCount ethGetTransactionCount = null;
        try {
            ethGetTransactionCount = web3.ethGetTransactionCount(
                    fromAddress, DefaultBlockParameterName.LATEST).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        writeLogMessage(nonce.toString());
        String toAddress = "0x46d00A386E63B686a8d26407E6d6E0CB7c6256fA";

        EthGasPrice ethGasPrice = null;
        try {
            ethGasPrice = web3.ethGasPrice().send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BigInteger gasPrice = ethGasPrice.getGasPrice();
        writeLogMessage(gasPrice.toString());

        Transaction tx = Transaction.createEtherTransaction(fromAddress, BigInteger.ONE, BigInteger.ONE, BigInteger.ONE, toAddress, BigInteger.ONE);
        EthEstimateGas ethEstimateGas = null;
        try {
            ethEstimateGas = web3.ethEstimateGas(tx).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BigInteger gasLimit = ethEstimateGas.getAmountUsed();
        System.out.println(gasLimit);
        writeLogMessage(gasLimit.toString());

        // create our transaction
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, gasPrice, gasLimit, toAddress, Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.ETHER).toBigInteger());

        // sign & send our transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Integer.parseInt(clientVersion), credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = null;
        try {
            ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String hash = ethSendTransaction.getTransactionHash();
        System.out.println(hash);
        writeLogMessage(hash);

    }

    @UiThread
    void writeLogMessage(String message) {
        logMessage.append(message+"\n");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

