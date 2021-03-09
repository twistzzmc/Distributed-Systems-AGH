package chat;

import java.net.*;
import java.util.Arrays;

public class ClientUdp extends Thread {
    private final String hostName;
    private final int port;
    private final Client client;
    private boolean socketShouldBeClosed = true;

    private DatagramSocket socket = null;

    public ClientUdp(String hostName, int port, Client client) {
        this.hostName = hostName;
        this.port = port;
        this.client = client;
    }

    public void closeSocket() {
        socketShouldBeClosed = true;
        socket.close();
    }

    public void sendMessage(String msg) {
        try {
            InetAddress address = InetAddress.getByName(hostName);
            byte[] sendBuffer = msg.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
            socket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("JAVA UDP CLIENT");
            socketShouldBeClosed = false;

            socket = new DatagramSocket();
            byte[] receiveBuffer = new byte[1024];

            this.sendMessage("NEW_CLIENT-" + client.nick);

            while (client.running) {
                Arrays.fill(receiveBuffer, (byte)0);
                DatagramPacket serverMessage = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(serverMessage);

                String msg = new String(serverMessage.getData());
                System.out.println(msg);
            }
        } catch (Exception e) {
            if (!socketShouldBeClosed) {
                e.printStackTrace();
            }
        }
    }
}
