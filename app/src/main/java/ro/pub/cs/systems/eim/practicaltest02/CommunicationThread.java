package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class CommunicationThread extends Thread {

    private Socket socket;

    public CommunicationThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String infoRequest = "";
        try {
            infoRequest = Utilities.getReader(socket).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String webPageAddress = Constants.currencyAPIURL;
        Log.v(Constants.TAG, "CommunicationThread: request for " + infoRequest);
        String error = null;
        String response = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webPageAddress);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String content = httpClient.execute(httpGet, responseHandler);
            if (content != null) {
                JSONObject jason = new JSONObject(content);
                JSONObject bpi = jason.getJSONObject(Constants.currencyMain);

                Log.v(Constants.TAG, "CommunicationThread: response is " + response);
            } else {
                error = "-1";
                response = null;
                Log.v(Constants.TAG, "Server error: content is null");
            }
        } catch (Exception exception) {
            error = "-1";
            response = null;
            Log.v(Constants.TAG, "Server error: " + exception.getMessage());
        }
        if (response == null) {
            try {
                Utilities.getWriter(socket).println(error);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Utilities.getWriter(socket).println(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}