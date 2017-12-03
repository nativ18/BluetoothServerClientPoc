# BluetoothServerClientPoc
A poc for server/client data transfer application on top of RFCOMM communication protocol.

In order to run this project you'll need at least 2 devices - 1 for server 1 for client.

What it does:
The server is becoming visible to other devices as it loads. 
The client connect to it by identifing the server UUID(being declaired internally).

After the handshake occurs - the server start sending messages to the client: TIMESTAMP + "hello Client!".

All the states/information are being represented via Toasts for simplicity.
