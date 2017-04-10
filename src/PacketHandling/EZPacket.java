package PacketHandling;

import java.net.DatagramPacket;

public class EZPacket {

    private static final int headerlength = 4;
    private int seq; //0-1
    private int target = 0; //2
    private int source; //3
    private byte[] data = new byte[0];

    public static void main(String[] args) {
        EZPacket pkt = new EZPacket(0);
        pkt.setSeq(257);
        pkt.setTarget(0);
        pkt.print();
    }

    public void setPacket(byte[] b) {
        data = new byte[b.length-headerlength];
        System.arraycopy(b,headerlength,data,0,b.length-headerlength);
        seq = b[0] * 256 + b[1];
        target = b[2];
        source = b[3];
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[data.length+headerlength];
        bytes[0] = (byte)(seq / 256);
        bytes[1] = (byte)(seq % 256);
        bytes[2] = (byte) target;
        bytes[3] = (byte) source;
        System.arraycopy(data, 0, bytes, headerlength, data.length);
        return bytes;
    }

    public EZPacket(int src) {
        source = src;
    }

    public EZPacket(int seq, int src, int target, byte[] data) {
        source = src;
        this.seq = seq;
        this.target = target;
        this.data = data;
    }

    public EZPacket(byte[] b) {
        setPacket(b);
    }



    public EZPacket(DatagramPacket d) {
       setPacket(d.getData());
    }

    public DatagramPacket getDGP() {
        byte[] bytes = getBytes();
        return new DatagramPacket(bytes, bytes.length);
    }

    public int getSeq() {
        return  seq;
    }

    public void setSeq(int i) {
        seq = i;
    }

    public void setTarget(int i) {
        target = i;
    }

    public int getTarget() {
        return  seq;
    }

    public void setSource(int i) {
        source = i;
    }

    public int getSource() {
        return source;
    }

    public void print() {
        byte[] bytes = getBytes();
        for(byte b: bytes) {
            System.out.print(b + " ");
        }
    }
}
