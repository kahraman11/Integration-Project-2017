package PacketHandling;

import Encryption.Encryption;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Created by Steven on 10-4-2017.
 */
public class RecThread extends Thread {

    public void run() {
        while(true) {
            DatagramPacket d = new DatagramPacket(new byte[24000], 24000);
            try {
                Network.socket.receive(d);
                EZPacket ez = new EZPacket(d);
                System.out.println("rec pkt type:" + ez.getType() + " seq:" + ez.getSeq() + " source:" + ez.getSource() + " size:" + ez.getSize());
                Handlemsg.handlemsg(ez);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
