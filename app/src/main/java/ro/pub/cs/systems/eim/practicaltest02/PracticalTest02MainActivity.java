package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    EditText serverPort, clientPort, ip;
    EditText currencyTextView;

    Button clientConnect, serverConnect;

    Spinner currency;

    ServerThread serverThread;

    ArrayAdapter<CharSequence> currencyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPort = findViewById(R.id.serverPortTextView);
        clientPort = findViewById(R.id.clientPortTextView);
        ip = findViewById(R.id.clientIPTextView);
        currencyTextView = findViewById(R.id.valueTextView);

        clientConnect = findViewById(R.id.clientConnectButton);
        serverConnect = findViewById(R.id.serverConnectButton);

        currency = findViewById(R.id.currencySpinner);

        final String currencyString = "EUR";

        serverConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverThread = new ServerThread(Integer.valueOf(serverPort.getText().toString()));
                serverThread.startServer();
            }
        });

        currencyAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item);
        currencyAdapter.add(Constants.EUR);
        currencyAdapter.add(Constants.USD);
        currencyAdapter.notifyDataSetChanged();
        currency.setAdapter(currencyAdapter);

        currency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext(), currencyAdapter.getItem(position).toString() + " was selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) {
            serverThread.stopServer();
        }
        super.onDestroy();
    }
}
