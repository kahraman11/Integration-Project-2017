package PacketHandling;

import java.net.DatagramPacket;

public class EZPacket {

    private static final int headerlength = 7;
    private int seq; //0-1
    private int target = 0; //2
    private int source; //3
    private int type; //6
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

    public void setPacket(byte[] b, int length) {
        byte[] reduced = new byte[length];
        System.arraycopy(b, 0, reduced, 0, length);
        setPacket(reduced);
    }

    public byte[] getBytes() {
        int size = getSize();
        byte[] bytes = new byte[size];
        bytes[0] = (byte)(seq / 256);
        bytes[1] = (byte)(seq % 256);
        bytes[2] = (byte) target;
        bytes[3] = (byte) source;
        bytes[4] = (byte)(size / 256);
        bytes[5] = (byte)(size % 256);
        bytes[6] = (byte) type;
        System.arraycopy(data, 0, bytes, headerlength, data.length);
        return bytes;
    }

    public EZPacket(int src) {
        source = src;
    }

    public EZPacket(int seq, int src, int target, int type, byte[] data) {
        source = src;
        this.seq = seq;
        this.target = target;
        this.data = data;
        this.type = type;
    }

    public EZPacket(byte[] b) {
        setPacket(b);
    }

    public EZPacket(DatagramPacket d) {
        int length = d.getData()[4] * 256 + d.getData()[5];
       setPacket(d.getData(), length);
    }

    public DatagramPacket getDGP() {
        byte[] bytes = getBytes();
        return new DatagramPacket(bytes, bytes.length, Network.group, Network.port);
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] d) {
        data = d;
    }

    public String getText() {
        return new String(getData());
    }

    public int getTarget() {
        return  seq;
    }

    public void setType(int i) {
        type = i;
    }

    public int getType() {
        return type;
    }

    public void setSource(int i) {
        source = i;
    }

    public int getSource() {
        return source;
    }

    public int getSize() {
        return data.length+headerlength;
    }

    public void print() {
        byte[] bytes = getBytes();
        for(byte b: bytes) {
            System.out.print(b + " ");
        }
    }
}
