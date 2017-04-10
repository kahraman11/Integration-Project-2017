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
        if (p.getSource() != Network.nodenr) {
            switch (p.getType()) {
                case 0:
                    retransmit(p);
                    break;
                case 1: //TODO
                    break;
                case 2:
                    text(p);
                    break;
            }
        }
    }

    static public void retransmit(EZPacket p) {
        if(!received(p)) {
            if(p.getType() == 0) {
                //TODO handle pings by users use method underneatho
                p.getText();
                nodenames.put(p.getSource(), p.getText());
            }
            Network.outBuffer.addPacket(p);
        }
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
            Network.gui.message("Hacker", p.getText());
        }
        retransmit(p);
    }
}
