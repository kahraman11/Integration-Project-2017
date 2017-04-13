package PacketHandling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Steven on 10-4-2017.
 */
public class Handlemsg {

    private static ConcurrentHashMap<Integer, ArrayList<Integer>> rec = new ConcurrentHashMap<>();
    public static HashMap<Integer, String> nodenames = new HashMap<>();

    static public void handlemsg(EZPacket p) {
        if (p.getSource() != Network.nodenr && !received(p)) {
            switch (p.getType()) {
                case 0:
                    retransmit(p);
                    break;
                case 1: OutBuffer.ack(p);
                    break;
                case 2:
                    text(p);
                    break;
            }
        }
    }

    static public void retransmit(EZPacket p) {
        if(p.getType() == 0) {
            p.getText();
            System.out.println(p.getSource() + " " + p.getText() + " " + p.getSeq());
            nodenames.put(p.getSource(), p.getText());
        }
        if(p.getType() != 2 && p.getType() != 0) {
            EZPacket pkt = new EZPacket(Network.nodenr, 0, 1, p.getSource(), p.getSeq(), new byte[0]);
            OutBuffer.addPacket(pkt);
        }
        System.out.println("retransmitted packet type: " + p.getType());
        OutBuffer.addPacket(p);
    }

    static public boolean received(EZPacket p) {
        if(rec.containsKey(p.getSource())) {
            ArrayList<Integer> list = rec.get(p.getSource());
            if(list.contains(p.getSeq())){
                return true;
            } else {
                list.add(p.getSeq());
                rec.put(p.getSource(), list);
                return false;
            }
        } else {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(p.getSeq());
            rec.put(p.getSource(), list);
            return false;
        }
    }

    static public void text(EZPacket p) {
        //TODO handle incoming text
        //send to gui
        if(nodenames.containsKey(p.getSource())) {
            Network.gui.message(nodenames.get(p.getSource()), p.getText());
        } else {
            System.out.println("test");
            Network.gui.message("Hacker", p.getText() + " " + p.getSeq() + " " + p.getSource());
        }
        retransmit(p);
    }
}
