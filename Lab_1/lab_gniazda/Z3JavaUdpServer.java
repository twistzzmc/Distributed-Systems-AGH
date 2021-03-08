import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import java.nio.ByteBuffer;
import java.net.InetAddress;

public class Z3JavaUdpServer {

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

                
                int nb = ByteBuffer.wrap(receivePacket.getData()).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
                System.out.println(nb);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                byte[] b = ByteBuffer.allocate(4).putInt(nb + 1).array();

                DatagramPacket responsePacket = new DatagramPacket(b, b.length, clientAddress, clientPort);
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
