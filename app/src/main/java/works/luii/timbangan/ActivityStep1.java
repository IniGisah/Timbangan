package works.luii.timbangan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ActivityStep1 extends AppCompatActivity implements works.luii.timbangan.Notify {
    
    //UI
    private Handler h;
    private ListView btBondedDevicesListView;
    private ArrayAdapter btBondedAdapter;

    // Bluetooth
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    Set<BluetoothDevice> btBondedDevices;

    // Data
    String[] listOfBondedDevices;

    ActivityStep1 main;
    Button nextButton = (Button) findViewById(R.id.next_pg1);
    
    // Threads
    works.luii.timbangan.ClientThread clientThread;
    works.luii.timbangan.ConnectedThreadReadWriteData connectedThreadReadWriteData;

    /**
     * On Create
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        nextButton.setVisibility(View.GONE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //The user have conceded permission
                Toast.makeText(ActivityStep1.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                setupUI();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(ActivityStep1.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_ADMIN, android.Manifest.permission.BLUETOOTH_CONNECT)
                .check();
    }

    private void setupUI() {
        // UI
        btBondedDevicesListView = (ListView) findViewById(R.id.bt_bonded_dev_list_pg1);

        h = new Handler();

        // Bluetooth
        // Get bluetooth manager and adapter.
        // If adapter=null => Device does not support bluetooth!
        BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        Log.v("Bluetooth", bm.getAdapter().toString());

        bluetoothAdapter = bm.getAdapter();
        listOfBondedDevices = null;
        if (bluetoothAdapter != null) listOfBondedDevices = fillBtBondedDeviceList();
        else listOfBondedDevices[0] = "Keine";

        btBondedAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listOfBondedDevices);
        btBondedDevicesListView.setAdapter(btBondedAdapter);

        // Check device selected from list of bonded devices and establish a connection.
        main = this;
        btBondedDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Iterator bi = btBondedDevices.iterator();
                bluetoothDevice = null;
                for (int i = 0; i <= position; i++) bluetoothDevice = (BluetoothDevice) bi.next();
                if (bluetoothDevice != null) {
                    nextButton.setVisibility(View.VISIBLE);

                    //console.append("Selected device:" + bluetoothDevice.getName().toString() + " Address:" + bluetoothDevice.getAddress() + "\n\n");
                    //clientThread = new works.luii.timbangan.ClientThread(main, bluetoothDevice, console, dataToSendEditText.getText().toString(), h);
                    //clientThread.start();
                }
            }
        });

        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ActivityStep2.class);
            startActivity(intent);
        });

        // Fab...
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_pg1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillBtBondedDeviceList();
                Snackbar.make(view, "Updated", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //console.setText("");
            }
        });
    }

    /**
     * Get instance of thread which is connected to the bluetooth device.
     * This instance is needed to send data via the {@link works.luii.timbangan.ConnectedThreadReadWriteData}
     */
    @Override
    public void connectionSuceeded() {
        Log.v("CON:", "Succeeded");
        connectedThreadReadWriteData = clientThread.getConnectedThread();
    }

    @Override
    public void messageIncomming(String message) {
        final String m = message;
        h.post(new Runnable() {
            @Override
            public void run() {
                // todo: not working when writing to console from outside thread.....
                //console.append(m);
            }
        });
    }

    /**
     * Fill bluetooth device list
     */
    private String[] fillBtBondedDeviceList() {
        String[] dl = new String[0];
        StringBuilder deviceList = new StringBuilder();
        btBondedDevices = bluetoothAdapter.getBondedDevices();
        if (btBondedDevices.size() > 0) {
            for (BluetoothDevice dev : btBondedDevices) {
                deviceList.append(dev.getName() + "\n");
                deviceList.append(dev.getAddress() + ",");
            }
        } else deviceList.append("No Devices!\n,");

        dl = deviceList.toString().split(",");
        return dl;
    }
}
