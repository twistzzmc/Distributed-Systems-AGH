package chat;

import java.io.IOException;
import java.net.*;

public class ClientMulticast extends Thread {
    public MulticastSocket socket = null;
    private final byte[] buff = new byte[1024];
    private SocketAddress socketAddress;
    private NetworkInterface networkInterface;

    private boolean socketShouldBeClosed = true;
    private final Client client;

    public ClientMulticast(Client client) {
        this.client = client;
    }

    public void closeSocket() {
        socketShouldBeClosed = true;
        socket.close();
    }

    public void sendMessage(String message) {
        try {
            byte[] msgBuff = message.getBytes();
            DatagramPacket packet = new DatagramPacket(msgBuff, msgBuff.length, socketAddress);
            socket.leaveGroup(socketAddress, networkInterface);
            socket.send(packet);
            socket.joinGroup(socketAddress, networkInterface);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("JAVA MULTICAST CLIENT");
            socketShouldBeClosed = false;

            socket = new MulticastSocket(Client.multicastPort);
            InetAddress inetAddress = InetAddress.getByName(Client.multicastAddress);
            socketAddress = new InetSocketAddress(inetAddress, Client.multicastPort);
            networkInterface = NetworkInterface.getByInetAddress(inetAddress);

            socket.joinGroup(socketAddress, networkInterface);
            while (client.running) {
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                socket.receive(packet);
                String msg = new String(packet.getData(), 0, packet.getLength());
                System.out.println(msg);
            }
        } catch (IOException ioe) {
            if (!socketShouldBeClosed) {
                ioe.printStackTrace();
            }
        }
    }
}
