package particula.com.bluetooth_poc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import particula.com.bluetooth_poc.client.ClientConnectThread;
import particula.com.bluetooth_poc.server.ServerAcceptThread;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 2;

    public static MainActivity activity;

    private BluetoothDevice mDevice;

    private ArrayList<BluetoothDevice> mAllRemoteDevices;

    // Create a BroadcastReceiver for ACTION_FOUND (new bluetooth device found)
    // All this implementation is for client to detect the server device.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mAllRemoteDevices.add(device);

                Toast.makeText(MainActivity.this, "Asking " + device.getName() + "  for its UUID", Toast.LENGTH_SHORT).show();

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // start requesting for devices UUID(clients ask server)
                for (int i = 0; i < mAllRemoteDevices.size(); i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        // asks the remote device for its UUID and Name. Answer comes in Broadcast receiving
                        mAllRemoteDevices.get(i).fetchUuidsWithSdp();
                    }
                }
            } else if (BluetoothDevice.ACTION_UUID.equals(action)) {
                BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuids = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                if (uuids != null && uuids.length > 0) {

                    int size = uuids.length;
                    for (int i = 0; i < size; i++) {
                        if (uuids[i].toString().equals(ServerAcceptThread.DEFAULT_UUID.toString())) {
                            mDevice = d;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                mDevice.connectGatt(MainActivity.this, true, new PocBluetoothGattCallback());
                            }

                            Toast.makeText(MainActivity.this, "Founded Server device!!", Toast.LENGTH_SHORT).show();

                            if (mDevice != null) {
                                new ClientConnectThread(mDevice).start();
                            }
                            return;
                        }
                    }
                    Toast.makeText(MainActivity.this, "server device wasn't found yet!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void makeYourselfBTVisible() {
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        findViewById(R.id.server_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupServer();
            }
        });
        findViewById(R.id.client_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupClient();
            }
        });

        setupCommon();
    }

    private void setupCommon() {
        makeYourselfBTVisible();

        // starts listening to founded devices
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        // ask BT on if is off
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "Device do not support Bluetooth!!", Toast.LENGTH_LONG).show();
            finish();
        }

        // ask user to turn bluetooth on
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }


    private void setupServer() {
        new ServerAcceptThread().start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "User didn't turned on Bluetooth!!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Bluetooth is on!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupClient() {
        if (mAllRemoteDevices == null)
            mAllRemoteDevices = new ArrayList<>(1);

        Toast.makeText(MainActivity.this, "Searching for Server device...", Toast.LENGTH_SHORT).show();

        // makes the client find the server app. Then store its remote object in mDevice.
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReceiver.abortBroadcast();
    }
}
