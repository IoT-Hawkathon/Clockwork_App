package com.amazonaws.demo.s3tranferutility;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

/**
 * Created by spal on 3/26/16.
 */
public class easyAWS extends ListActivity{

    // Used just for better logging?
    private static final String TAG = "DownloadActivity";

    // This is the main class for interacting with the Transfer Manager
    private TransferUtility transferUtility;

    final private String redditKey = "TIL_OUTPUT.txt";
    final private String pizzaKey  = "pizzaFacts.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) { // <-- this is what you have to call to setup
        super.onCreate(savedInstanceState);

        transferUtility = Util.getTransferUtility(this);
    }

    /*
     * Begins to download the file specified by the key in the bucket.
     */
    private void beginDownload(String key) { // <-- Call this when ever you are ready to download the files
        // Location to download files from S3 to. You can choose any accessible
        // file.
        File redditFile = new File(redditKey);
        File pizzaFile  = new File(pizzaKey);

        // Download Reddit TIL File
        TransferObserver redditObserver = transferUtility.download(Constants.BUCKET_NAME, redditKey, redditFile);

        // Download pizza facts File
        TransferObserver pizzaObserver = transferUtility.download(Constants.BUCKET_NAME, pizzaKey, pizzaFile);
    }

    /*
      * A TransferListener class that can listen to a download task and be
      * notified when the status changes.
      */
    private class DownloadListener implements TransferListener {
        // Simply updates the list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "onError: " + id, e);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            Log.d(TAG, "onStateChanged: " + id + ", " + state);
        }
    }
}
