package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReader extends Thread {

    private final BufferedReader in;
    private final Client client;

    public ClientReader(Socket socket, Client client) throws IOException {
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.client = client;
    }

    @Override
    public void run() {
        try {
            while (client.running) {
                if (in.ready()) {
                    String serverMessage = in.readLine();
                    System.out.println(serverMessage);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
