package PacketHandling;

import Converter.ImageConverter;
import GUI.GUI;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mathay on 10-4-17.
 */
public class OutBuffer {
    public static List<DatagramPacket> outputBuffer;
    private GUI gui = null;
    public static ConcurrentHashMap<Integer, Long> outstandingAck = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, EZPacket> outstandingPkt = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, List<Integer>> outstandingRec = new ConcurrentHashMap<>();


    public static void checkOutStanding() {
        Thread thread = new Thread() {
            public void run() {
                for (Integer i : outstandingAck.keySet()) {
                    long time = System.nanoTime();
                    if(receivedAll(i)) {
                        outstandingPkt.remove(i);
                        outstandingAck.remove(i);
                    }else if (time < outstandingAck.get(i) + 1000) {
                        addPacket(outstandingPkt.get(i));
                        outstandingPkt.remove(i);
                        outstandingAck.remove(i);
                    }
                }
                try {
                    Thread.sleep(50);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public static void ack(EZPacket p) {
        if(p.getAcktarget() == Network.nodenr){
            if(outstandingRec.containsKey(p.getAck())){
                List<Integer> list = outstandingRec.get(p.getAck());
                list.add(p.getSource());
                outstandingRec.put(p.getAck(), list);
            } else {
                List<Integer> list = new ArrayList<>();
                list.add(p.getSource());
                outstandingRec.put(p.getAck(), list);
            }
        }
    }

    public static boolean receivedAll(int i) {
        return outstandingRec.get(i).containsAll(Handlemsg.nodenames.keySet());
    }

    public static int SEQ = 1;

    public static int nextSeq(EZPacket pkt) {
        SEQ++;
        if(pkt.getType() != 0 && pkt.getType() != 2) {
            outstandingAck.put(SEQ, System.nanoTime());
            outstandingPkt.put(SEQ, pkt);
        }
        return SEQ;
    }

    public OutBuffer() {
        outputBuffer = new ArrayList<>();
        startUp();
    }

    public static void addPacket(EZPacket packet) {
        if(packet.getSource() == Network.nodenr) {
            packet.setSeq(nextSeq(packet));
        }
        outputBuffer.add(packet.getDGP());
    }

    public void startUp() {
        Thread thread = new Thread(){
            public void run() {
                int nr = 0;
                while (true) {
                    //System.out.println("buffer: " + outputBuffer.size());
                    if (outputBuffer.size() > 0) {
                        sendPacket(outputBuffer.get(0));
                        outputBuffer.remove(0);
                    } else {
                        if(nr==10) {
                            if (Handlemsg.nodenames.containsKey(Network.nodenr)) {
                                EZPacket p = new EZPacket(Network.nodenr, 0, 0, Handlemsg.nodenames.get(Network.nodenr).getBytes());
                                p.setSeq(nextSeq(p));
                                sendPacket(p.getDGP());
                            } else {
                                EZPacket p = new EZPacket(Network.nodenr, 0, 0, "koos naamloos".getBytes());
                                p.setSeq(nextSeq(p));
                                sendPacket(p.getDGP());
                            }
                            nr=0;
                        } else {
                            nr++;
                        }
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

    public static void sendPacket(File file) {
        EZPacket packet = new EZPacket(Network.nodenr, 0, 3, ImageConverter.send(file));
        OutBuffer.addPacket(packet);
    }

    public void sendPacket(DatagramPacket dgp) {
        try {
            Network.socket.send(dgp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
