package particula.com.bluetooth_poc.client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

import particula.com.bluetooth_poc.server.ServerAcceptThread;

public class ClientConnectThread extends Thread {

    private BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    private final BluetoothAdapter mBluetoothAdapter;

    public ClientConnectThread(BluetoothDevice device) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            mmSocket = device.createRfcommSocketToServiceRecord(ServerAcceptThread.DEFAULT_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) {
            }
            return;
        }
    }

    private void manageConnectedSocket(BluetoothSocket mmSocket) {
        new ClientConnectedThread(mmSocket).start();
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}