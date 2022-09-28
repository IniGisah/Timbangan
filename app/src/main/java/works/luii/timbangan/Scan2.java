package works.luii.timbangan;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Scan2 extends AppCompatActivity implements works.luii.timbangan.Notify{
    private Handler h;

    // Threads
    works.luii.timbangan.ClientThread clientThread;
    works.luii.timbangan.ConnectedThreadReadWriteData connectedThreadReadWriteData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_2);

        TextView textView = (TextView) findViewById(R.id.kambingkg);
        SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
        String jsonbt = sharedPreferences.getString("btdevice", "");

        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothDevice bluetoothDevice = bluetoothManager.getAdapter().getRemoteDevice(jsonbt);

        h = new Handler();

        clientThread = new works.luii.timbangan.ClientThread(Scan2.this, bluetoothDevice, textView, "", h);
        clientThread.start();

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

        SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
        SharedPreferences.Editor IdKambing = sharedPreferences.edit();
        IdKambing.putFloat("berat", datakg);
        IdKambing.commit();

        Intent intent = new Intent(Scan2.this, Upload.class);
        startActivity(intent);
    }
}
