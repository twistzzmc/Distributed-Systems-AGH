import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Z1JavaUdpClient {

    public static void main(String args[]) throws Exception
    {
        System.out.println("JAVA UDP CLIENT");
        DatagramSocket socket = null;
        int portNumber = 9008;

        try {
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost");
            byte[] sendBuffer = "Ping Java Udp".getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
            socket.send(sendPacket);

            System.out.println(socket.getLocalAddress());
            System.out.println(socket.getLocalPort());
            
            byte[] receiveBuffer = new byte[1024];

            DatagramPacket serverResponse = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(serverResponse);

            String msg = new String(serverResponse.getData());
            System.out.println("received msg: " + msg);
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
