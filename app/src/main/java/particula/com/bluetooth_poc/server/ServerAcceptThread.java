package particula.com.bluetooth_poc.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import particula.com.bluetooth_poc.MainActivity;

public class ServerAcceptThread extends Thread {
    private static final String NAME = "SDP_NAME";
    private static final UUID MY_UUID = UUID.randomUUID();

    private BluetoothServerSocket mmServerSocket;
    private final BluetoothAdapter mBluetoothAdapter;
//    private final BluetoothDevice mBluetoothDevice;

    private MainActivity activity;

    // THE UUID SHOULD BE IDENTICAL TO THE ONE REQUESTED BY THE CLIENT APP/IOT DEVICE
    public static UUID DEFAULT_UUID = UUID.fromString("00001101-9999-1000-8000-00805F9B34FB");

    public ServerAcceptThread() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        try {

            // MY_UUID is the app's UUID string, also used by the client code
            mmServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, DEFAULT_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                MainActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.activity, "Getting blocked on Socket", Toast.LENGTH_SHORT).show();
                    }
                });

                socket = mmServerSocket.accept();

                MainActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.activity, "Socket is open", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                manageConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        new ServerConnectedThread(socket).start();
    }

    /**
     * Will cancel the listening socket, and cause the thread to finish
     */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
        }
    }
}