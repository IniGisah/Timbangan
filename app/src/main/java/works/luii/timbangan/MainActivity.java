package works.luii.timbangan;

/**
 * Bluetooth Conector
 *
 * @rem: Connects To a bluetooth device and exchanges data.@@
 */

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements works.luii.timbangan.Notify {

    // UI
    private Handler h;
    private ListView btBondedDevicesListView;
    private ArrayAdapter btBondedAdapter;
    private EditText dataToSendEditText;
    private String dataToSend;
    private TextView console;
    private Button send, disconect;

    // Bluetooth
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    Set<BluetoothDevice> btBondedDevices;

    // Data
    String[] listOfBondedDevices;

    MainActivity main;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //The user have conceded permission
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                setupUI();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_ADMIN, android.Manifest.permission.BLUETOOTH_CONNECT)
                .check();


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    }

    private void setupUI() {
        // UI
        btBondedDevicesListView = (ListView) findViewById(R.id.bt_bonded_dev_list);
        console = (TextView) findViewById(R.id.console);
        dataToSendEditText = (EditText) findViewById(R.id.data_to_send);
        send = (Button) findViewById(R.id.send);
        disconect = (Button) findViewById(R.id.disconect);

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
                    console.append("Selected device:" + bluetoothDevice.getName().toString() + " Address:" + bluetoothDevice.getAddress() + "\n\n");
                    clientThread = new works.luii.timbangan.ClientThread(main, bluetoothDevice, console, dataToSendEditText.getText().toString(), h);
                    clientThread.start();
                }
            }
        });

        // Send
        /*
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataToSend = dataToSendEditText.getText().toString();
                connectedThreadReadWriteData.send(dataToSend);
                dataToSendEditText.setText("");

            }
        });
        */

        // Disconnect
        disconect.setOnClickListener(view -> clientThread.cancel());


        // Fab...
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            fillBtBondedDeviceList();
            Snackbar.make(view, "Updated", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            console.setText("");
        });
    }

    /**
     * Get instance of thread which is connected to the bluetooth device.
     * This instance is needed to send data via the {@link works.luii.timbangan.ConnectedThreadReadWriteData}
     */
    @Override
    public void connectionSuccessful() {
        Log.v("CON:", "Succeeded");
        connectedThreadReadWriteData = clientThread.getConnectedThread();
    }

    @Override
    public void messageIncomming(String message) {
        final String m = message;
        h.post(() -> {
            // todo: not working when writing to console from outside thread.....
            //console.append(m);
        });

    }

    @Override
    public void dataReceiveDone(float datakg) {

    }

    @Override
    public void needReconnect(boolean hasil) {

    }


    /**
     * Fill bluetooth device list
     */
    private String[] fillBtBondedDeviceList() {
        String[] dl;
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

    /**
     * Options Menu
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Callback for Options Menu
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
