package particula.com.bluetooth_poc.server;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class ServerConnectedThread extends Thread {

    private static final int MESSAGE_READ = 2;

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ServerConnectedThread(BluetoothSocket socket) {
        Log.d("ServerThread", "ServerConnectedThread");

        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
//                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity
//                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
//                        .sendToTarget();

                //write a message to client
                String hello = System.currentTimeMillis() + ": Hello Client!";
                mmOutStream.write(hello.getBytes("UTF-8"));
                mmOutStream.flush();

                Log.d("ServerThread", "sending hello message");

                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}