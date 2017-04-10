package PacketHandling;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Steven on 10-4-2017.
 */
public class Handlemsg {

    private static ConcurrentHashMap<Integer, ArrayList<Integer>> rec = new ConcurrentHashMap<>();

    static public void handlemsg(EZPacket p) {
        switch(p.getType()) {
            case 0: retransmit(p);
                    break;
            case 1: //TODO
                    break;
            case 2: text(p);
                    break;
        }

    }

    static public void retransmit(EZPacket p) {
        if(!received(p)) {
            if(p.getType() == 0) {
                //TODO handle pings by users use method underneatho
                p.getText();
            }
            Network.sendBuffer.addPacket(p);
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
        retransmit(p);
    }
}
