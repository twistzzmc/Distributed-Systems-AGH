package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {
    public static final ArrayList<Thread> connections = new ArrayList<>();
    public static final ArrayList<String> messages = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        int portNumber = 12345;
        ServerSocket serverSocket;

        serverSocket = new ServerSocket(portNumber);

        System.out.println("Server UP!");

        while(true) {
            Thread newConnection = new Connection(serverSocket.accept());
            connections.add(newConnection);
            newConnection.start();
        }
    }

}
