# BluetoothServerClientPoc
A poc for server/client data transfer application on top of RFCOMM communication protocol.


This is a POC for Bluetoouth Server/Client application over RFCOMM bluetooth protocol.

To use this POC you'll need atleast 2 device(1 for server 1 for client).

What it do is - server is going visible to other devices, client connect to it by identifing the server UUID(being declaired internally).
After the handshake occurs - the server start sending messages to the client: TIMESTAMP + "hello Client!".

All the states/information are being represented via Toasts.
