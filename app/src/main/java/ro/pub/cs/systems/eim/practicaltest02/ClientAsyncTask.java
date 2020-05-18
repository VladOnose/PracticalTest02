package ro.pub.cs.systems.eim.practicaltest02;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class ClientAsyncTask extends AsyncTask<String, String, Void> {

    private String currencyStr;
    private EditText currencyTextView;

    public ClientAsyncTask(String currencyStr, EditText currencyTextView) {
        this.currencyStr = currencyStr;
        this.currencyTextView = currencyTextView;
    }

    @Override
    protected Void doInBackground(String... params) {
        Socket socket = null;
        try {
            String serverAddress = params[0];
            int serverPort = Integer.parseInt(params[1]);
            socket = new Socket(serverAddress, serverPort);
            Log.v(Constants.TAG, "Async task now running " + serverAddress + ":" + serverPort);
            if (socket == null) {
                return null;
            }
            Log.v(Constants.TAG, "Connection opened with " + socket.getInetAddress() + ":" + socket.getLocalPort());

            Utilities.getWriter(socket).println(currencyStr);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                publishProgress(currentLine);
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
        try {
            if (socket != null) {
                socket.close();
            }
            Log.v(Constants.TAG, "Connection closed");
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        currencyTextView.setText("");
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        currencyTextView.append(progress[0]);
    }

    @Override
    protected void onPostExecute(Void result) {
    }
}
