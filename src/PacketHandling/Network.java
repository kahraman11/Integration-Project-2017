package PacketHandling;

import GUI.GUI;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by mathay on 10-4-17.
 */
public class Network {
    public static InetAddress group = null;
    public static MulticastSocket socket = null;
    public static int port = 8080;
    public static int nodenr = -1;
    public static OutBuffer outBuffer = null;
    public static GUI gui = null;

    public static void main(String[] args) throws IOException {
        //Network n = new Network(GUI);
        //n.setConnection();
    }

    public Network(GUI gui) {
        this.gui = gui;
    }

    public void setConnection() throws IOException {
        //get/set nodenr
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
                InetAddress iaddress = (InetAddress) ee.nextElement();
                if (iaddress.toString().contains("192")) {
                    nodenr = Integer.parseInt(iaddress.toString().substring(iaddress.toString().length() - 1));
                    System.out.println("IP Address: " + iaddress.getHostAddress() + " Nodenr: " + nodenr);
                } else {
                    System.out.println("Not a correct IP Address");
                }
            }
        }

        //join socket and group
        group = InetAddress.getByName("228.0.0.0");
        socket = new MulticastSocket(port);
        socket.joinGroup(group);

        //start outputbuffer to send stuff
        outBuffer = new OutBuffer();

        //test
        byte[] data = "e04fd020ea3a6910a2d808002b30309d".getBytes();
        for(int i =0; i<10; i++) {
            EZPacket packet = new EZPacket(1,2,2,data);
            //outBuffer.addPacket(packet);
        }
    }

}
