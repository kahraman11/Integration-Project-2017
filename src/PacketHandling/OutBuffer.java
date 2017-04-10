package PacketHandling;

import GUI.GUI;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mathay on 10-4-17.
 */
public class OutBuffer {
    public static List<DatagramPacket> outputBuffer;
    private GUI gui = null;

    public static int SEQ = 1;

    public static int nextSeq() {
        SEQ++;
        return SEQ;
    }

    public OutBuffer() {
        outputBuffer = new ArrayList<>();
        startUp();
    }

    public void addPacket(EZPacket packet) {
        outputBuffer.add(packet.getDGP());
    }

    public void startUp() {
        Thread thread = new Thread(){
            public void run() {
                while (true) {
                    if (outputBuffer.size() > 0) {
                        sendPacket(outputBuffer.get(0));
                        outputBuffer.remove(0);
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    public void sendPacket(DatagramPacket dgp) {
        try {
            Network.socket.send(dgp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
