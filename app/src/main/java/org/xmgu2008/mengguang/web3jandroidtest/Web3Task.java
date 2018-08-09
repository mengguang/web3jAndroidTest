package org.xmgu2008.mengguang.web3jandroidtest;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.NetVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class Web3Task extends AsyncTask<Void, Integer, String> {
    private final String rpcUrl = "rpc url here.";

    WeakReference<Activity> mWeakActivity;

    public Web3Task(Activity activity) {
        mWeakActivity = new WeakReference<Activity>(activity);
    }

    protected String doInBackground(Void... voids) {

        Web3j web3 = Web3j.build(new HttpService(rpcUrl));
        NetVersion netVersion = null;
        try {
            netVersion = web3.netVersion().send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String clientVersion = netVersion.getNetVersion();
        System.out.println(clientVersion);
        return clientVersion;

    }

    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(String result) {
        //showDialog("Downloaded " + result + " bytes");
        Activity activity = mWeakActivity.get();
        if (activity != null) {
            TextView message = activity.findViewById(R.id.textMessage);
            message.setText(result);
        }
    }
}
