package PacketHandling;

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
            } catch (IOException e) {
                e.printStackTrace();
            }
            new EZPacket(d);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
