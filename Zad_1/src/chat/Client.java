package chat;

import java.net.Socket;

public class Client extends Thread {
    private static final String hostName = "localhost";
    private static final int portNumber = 12345;

    public final String nick;
    public boolean running = false;

    public Client(String nick) {
        this.nick = nick;
    }

    public static void main(String[] args) {
        System.out.println("JAVA TCP CLIENT");
        new Client(args[0]).start();
    }

    @Override
    public void run() {
        this.running = true;

        try (Socket socket = new Socket(Client.hostName, portNumber)) {
            ClientWriter clientWriter = new ClientWriter(socket, this);
            ClientReader clientReader = new ClientReader(socket, this);

            clientWriter.start();
            clientReader.start();

            clientWriter.join();
            clientReader.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
