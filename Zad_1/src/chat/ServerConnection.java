package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class Connection extends Thread {
    private final Socket clientSocket;
    private String nick;

    public Connection(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            this.nick = in.readLine();
            System.out.println("Client connected! (" + nick + ")");

            for (String message : Server.messages) {
                out.println(message);
            }

            while(true) {
                String msg;
                try {
                    msg = in.readLine();
                    if (msg != null) {
                        Server.messages.add("[" + nick + "]: " + msg);
                    }
                } catch (SocketException se) {
                    closeSocket();
                    break;
                }

                if (msg != null) {
                    System.out.println("[" + nick + "]: " + msg);

                    for (Thread connection : Server.connections) {
                        Connection con = (Connection) connection;
                        PrintWriter otherOut = new PrintWriter(con.getClientSocket().getOutputStream(), true);
                        if (connection != this) {
                            otherOut.println("[" + nick + "]: " + msg);
                        }
                    }
                } else {
                    closeSocket();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSocket() throws IOException {
        clientSocket.close();
        Server.connections.remove(this);
        System.out.println("Client disconnected! (" + nick + ")");
    }

    public Socket getClientSocket() { return clientSocket; }
}
