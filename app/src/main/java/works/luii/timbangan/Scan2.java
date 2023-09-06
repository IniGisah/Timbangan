package works.luii.timbangan;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Scan2 extends AppCompatActivity implements works.luii.timbangan.Notify{
    private Handler h;

    // Threads
    works.luii.timbangan.ClientThread clientThread;
    works.luii.timbangan.ConnectedThreadReadWriteData connectedThreadReadWriteData;



    public void onCreate(Bundle savedInstanceState) {
        h = new Handler();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_2);

        TextView textView = (TextView) findViewById(R.id.kambingkg);
        SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
        String jsonbt = sharedPreferences.getString("btdevice", "");

        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothDevice bluetoothDevice = bluetoothManager.getAdapter().getRemoteDevice(jsonbt);

        startscan(bluetoothDevice, textView);
/*
        new CountDownTimer(10000,1000){
            @Override
            public void onTick(long l) {
                Log.v("DATA:", "Receiving data...");
            }
            @Override
            public void onFinish() {
                clientThread.cancel();
            }
        }.start();
 */
    }

    @Override
    public void connectionSuccessful() {
        Log.v("CON:", "Succeeded");
        connectedThreadReadWriteData = clientThread.getConnectedThread();
    }

    @Override
    public void messageIncomming(String message) {    }

    @Override
    public void dataReceiveDone(float datakg) {
        clientThread.cancel();
        clientThread.interrupt();

        SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
        SharedPreferences.Editor IdKambing = sharedPreferences.edit();
        IdKambing.putFloat("berat", datakg);
        IdKambing.commit();

        Intent intent = new Intent(Scan2.this, Upload.class);
        startActivity(intent);
    }

    @Override
    public void needReconnect(boolean hasil) {
        Button buttonretry = (Button) findViewById(R.id.retrybutton_scan2);


        TextView textView = (TextView) findViewById(R.id.kambingkg);
        SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
        String jsonbt = sharedPreferences.getString("btdevice", "");

        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothDevice bluetoothDevice = bluetoothManager.getAdapter().getRemoteDevice(jsonbt);

        runOnUiThread(() -> {
            buttonretry.setVisibility(View.VISIBLE);
            buttonretry.setOnClickListener(v -> {
                startscan(bluetoothDevice, textView);
                buttonretry.setVisibility(View.GONE);
            });
        });

        Log.v("Thread Debug:", "inside needreconnect");
    }

    public void startscan(BluetoothDevice bluetoothDevice, TextView textView){

        clientThread = new works.luii.timbangan.ClientThread(Scan2.this, bluetoothDevice, textView, "", h);
        clientThread.start();

    }
}
