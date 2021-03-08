import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class Z4JavaUdpServer {

    public static void main(String args[])
    {
        System.out.println("JAVA UDP SERVER");
        DatagramSocket socket = null;
        int portNumber = 9008;

        try{
            socket = new DatagramSocket(portNumber);
            byte[] receiveBuffer = new byte[1024];

            while(true) {
                Arrays.fill(receiveBuffer, (byte)0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData());
                System.out.println("received msg: " + msg);

                InetAddress address = receivePacket.getAddress();
                int port = receivePacket.getPort();
                byte[] responseBuffer;

                if (msg.length() > 0 && msg.charAt(0) == 80) {  // Python
                    responseBuffer = "Ping Python".getBytes();
                } else if (msg.length() > 0 && msg.charAt(0) == 74) {   // Java
                    responseBuffer = "Ping Java".getBytes();
                } else {    // Unknown
                    responseBuffer = "Ping Unknown".getBytes();
                }

                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, address, port);
                socket.send(responsePacket);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
