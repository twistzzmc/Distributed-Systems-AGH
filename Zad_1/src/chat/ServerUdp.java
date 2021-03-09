package chat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class ServerUdp extends Thread {
    private final int port;
    DatagramSocket socket = null;

    private final ArrayList<SocketAddress> clientAddressList = new ArrayList<>();
    private final Hashtable<SocketAddress, String> clientNicks = new Hashtable<>();

    public ServerUdp(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            System.out.println("UDP Server UP!");

            socket = new DatagramSocket(port);
            byte[] receiveBuffer = new byte[1024];

            while (true) {
                Arrays.fill(receiveBuffer, (byte)0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData());

                if (msg.trim().split("-")[0].equals("NEW_CLIENT")) {
                    clientAddressList.add(receivePacket.getSocketAddress());
                    clientNicks.put(receivePacket.getSocketAddress(), msg.trim().split("-")[1]);
                } else {
                    msg = "[" + clientNicks.get(receivePacket.getSocketAddress()) + "]:\n" + msg;
                    System.out.println(msg);

                    for (SocketAddress csa : clientAddressList) {
                        if (!csa.equals(receivePacket.getSocketAddress())) {
                            byte[] responseBuffer = msg.getBytes();
                            DatagramPacket responsePacket = new DatagramPacket(
                                    responseBuffer,
                                    responseBuffer.length,
                                    csa
                            );
                            socket.send(responsePacket);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
