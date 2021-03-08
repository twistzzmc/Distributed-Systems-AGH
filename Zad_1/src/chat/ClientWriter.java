package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientWriter extends Thread {

    private final PrintWriter out;
    private final Client client;

    public ClientWriter(Socket socket, Client client) throws IOException {
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.client = client;

        out.println(client.nick);
    }

    @Override
    public void run() {
        Scanner clientInput = new Scanner(System.in);
        while (true) {
            String input = clientInput.nextLine();

            if (input.equals("E")) {
                client.running = false;
                break;
            }

            out.println(input);
        }
    }
}
