package ro.pub.cs.systems.eim.practicaltest02;

import android.util.DebugUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class ServerThread extends Thread {
    private boolean isRunning;
    private ServerSocket serverSocket;
    private int port;

    private String eurValue, usdValue;

    public String getEurValue() {
        return eurValue;
    }

    public String getUsdValue() {
        return usdValue;
    }

    private Thread updater = new Thread() {
        @Override
        public void run() {
            try {
                while (!updater.isInterrupted()) {
                    Log.v(Constants.TAG, "Updater now updating");
                    String webPageAddress = Constants.currencyAPIURL;
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet(webPageAddress);
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        String content = httpClient.execute(httpGet, responseHandler);
                        if (content != null) {
                            JSONObject jason = new JSONObject(content);
                            JSONObject bpi = jason.getJSONObject(Constants.currencyMain);
                            eurValue = bpi.getJSONObject(Constants.EUR).get(Constants.currencyStr).toString();
                            usdValue = bpi.getJSONObject(Constants.USD).get(Constants.currencyStr).toString();
                        } else {
                            Log.v(Constants.TAG, "Updater error: content is null");
                        }
                    } catch (Exception exception) {
                        Log.v(Constants.TAG, "Updater exception: " + exception.getMessage());
                    }
                    Thread.sleep(Constants.SLEEP_TIME);
                }
            } catch (InterruptedException e) {
                Log.v(Constants.TAG, "Updater caught an exception");
            }
        }
    };

    public ServerThread(int port) {
        Log.v(Constants.TAG, "ServerThread: created with port " + port);
        this.port = port;
    }

    public void startServer() {
        updater.start();
        isRunning = true;
        start();
        Log.v(Constants.TAG, "startServer() method was invoked");
    }

    public void stopServer() {
        isRunning = false;
        try {
            serverSocket.close();
            updater.interrupt();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
        Log.v(Constants.TAG, "stopServer() method was invoked");
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"));
            while (isRunning) {
                Socket socket = serverSocket.accept();
                Log.v(Constants.TAG, "accept()-ed: " + socket.getInetAddress());
                if (socket != null) {
                    String infoRequest = "";
                    try {
                        infoRequest = Utilities.getReader(socket).readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String response = "";
                    if (infoRequest.compareTo(Constants.EUR) == 0) {
                        response = eurValue;
                    } else if (infoRequest.compareTo(Constants.USD) == 0){
                        response = usdValue;
                    }
                    Log.v(Constants.TAG, "ServerThread now sending back: " + response);
                    try {
                        Utilities.getWriter(socket).println(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    socket.close();
                }
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }
}