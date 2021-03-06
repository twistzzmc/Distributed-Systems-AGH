package chat;

import java.net.Socket;

public class Client extends Thread {
    private static final String hostName = "localhost";
    private static final int portNumber = 12345;

    public static final String multicastAddress = "230.0.0.0";
    public static final int multicastPort = 12346;
    public final ClientMulticast clientMulticast;

    public final String nick;
    public boolean running = false;

    private final ClientUdp clientUdp;

    public Client(String nick) {
        this.nick = nick;
        this.clientUdp = new ClientUdp(hostName, portNumber, this);
        this.clientUdp.start();

        this.clientMulticast = new ClientMulticast(this);
        this.clientMulticast.start();
    }

    public static void main(String[] args) {
        new Client(args[0]).start();
        System.out.println("JAVA TCP CLIENT");
    }

    @Override
    public void run() {
        this.running = true;

        try (Socket socket = new Socket(Client.hostName, portNumber)) {
            ClientWriter clientWriter = new ClientWriter(socket, this, clientUdp);
            ClientReader clientReader = new ClientReader(socket, this);

            clientWriter.start();
            clientReader.start();

            clientWriter.join();
            clientReader.join();

            clientUdp.closeSocket();
            clientUdp.join();

            clientMulticast.closeSocket();
            clientMulticast.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
