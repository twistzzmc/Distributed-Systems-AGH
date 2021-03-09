package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ClientWriter extends Thread {

    private final PrintWriter out;
    private final Client client;
    private final ClientUdp clientUdp;

    public ClientWriter(Socket socket, Client client, ClientUdp clientUdp) throws IOException {
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.client = client;
        this.clientUdp = clientUdp;

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
            } else if (input.equals("U")) {
//                clientUdp.sendMessage("CHECK CHECK CHECK");

                try {
                    Path filePath = Paths.get("C:\\Michal\\studia\\Semestr_6\\Systemy_Rozproszone\\Zad_1\\ASCII_FROG.txt");
                    String asciiFrog = Files.readString(filePath, StandardCharsets.US_ASCII);
                    clientUdp.sendMessage(asciiFrog);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } else {
                out.println(input);
            }

        }
    }
}
