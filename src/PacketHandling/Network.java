package PacketHandling;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by mathay on 10-4-17.
 */
public class Network {
    public static InetAddress group = null;
    public static MulticastSocket socket = null;
    public static int nodenr = -1;

    public static void main(String[] args) throws IOException {
        Network n = new Network();
        n.setConnection();
    }

    public void setConnection() throws IOException {
        //get/set nodenr
        if (Inet4Address.getLocalHost().getHostAddress().contains("192")) {
            nodenr = Integer.parseInt(Inet4Address.getLocalHost().getHostAddress().substring(Inet4Address.getLocalHost().getHostAddress().toString().length() - 1));
            System.out.println("IP Address: " + Inet4Address.getLocalHost().getHostAddress() + " Nodenr: " + nodenr);
        } else {
            System.out.println("No nodenr available");
        }

        //join socket and group
        group = InetAddress.getByName("228.0.0.0");
        socket = new MulticastSocket(8080);
        socket.joinGroup(group);

        //TODO Start a thread that listens on input and output
    }

}
