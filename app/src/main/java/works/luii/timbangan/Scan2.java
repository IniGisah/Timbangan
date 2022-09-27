package works.luii.timbangan;

import static android.content.res.Configuration.HARDKEYBOARDHIDDEN_NO;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class Scan2 extends AppCompatActivity implements works.luii.timbangan.Notify{
    private Handler h;

    TextView id_text, berat_text;

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
        this.runOnUiThread(() -> {
            Dialog dialog = new Dialog(Scan2.this);
            dialog.setContentView(R.layout.popup_done);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

            id_text = dialog.findViewById(R.id.textviewid);
            berat_text = dialog.findViewById(R.id.textviewberat);

            SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
            String idkambing = sharedPreferences.getString("id", "");

            id_text.setText("Id : " + idkambing);
            berat_text.setText("Berat : " + datakg + " kg");
        });
    }
}
