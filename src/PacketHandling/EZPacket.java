package PacketHandling;

import Encryption.Encryption;

import java.net.DatagramPacket;

public class EZPacket {

    private static final int headerlength = 14;
    private int seq = 0; //0-3
    private int target = 0; //4
    private int source; //5
    private int type; //8
    private int acktarget = 0;
    private int ack = 0; //10-13 (first is source rest is seq of received packet)
    private byte[] data = new byte[0];

    public static void main(String[] args) {
        EZPacket p = new EZPacket(0,0,2,"test dit is awesome".getBytes());
        p.print();
        System.out.println();
        EZPacket pkt = new EZPacket(0);
        pkt.setTarget(0);
        pkt.data = "test dit is awesome".getBytes();
        pkt.print();
        System.out.println();
        for(byte b: pkt.data) {
            System.out.print(b + " ");
        }
    }


    public void setPacket(byte[] b) {
        data = new byte[b.length-headerlength];
        System.out.println(b.length + " " + headerlength);
        System.arraycopy(b,headerlength,data,0,b.length-headerlength);
        if(data.length > 0) {
            System.out.println("data just before decrypt " + data.length);
            dataPrint();
            System.out.println("data before decrypting is length: " + data.length);
            data = Encryption.decrypt(data);
            System.out.println("data decrypted: " + new String(data));
        }
        seq = b[0] * 2097152 + b[1] * 16384 + b[2]*128 + b[3];
        target = b[4];
        source = b[5];
        type = b[8];
        acktarget = b[9];
        ack = b[10] * 2097152 + b[11] * 16384 + b[12]*128 + b[13];
    }

    //Don't set packet on unencrypted data
    public void setPacket(byte[] b, int length) {
        byte[] reduced = new byte[length];
        System.out.println("length: "  +length);
        System.arraycopy(b, 0, reduced, 0, length);
        setPacket(reduced);
    }

    public byte[] getBytes() {
        int size = headerlength + data.length;
        byte[] bytes = new byte[size];
        bytes[0] = (byte)(seq / 2097152);
        bytes[1] = (byte)(seq / 16384);
        bytes[2] = (byte)(seq / 128);
        bytes[3] = (byte)(seq % 128);
        bytes[4] = (byte) target;
        bytes[5] = (byte) source;
        bytes[6] = (byte)(size / 256);
        bytes[7] = (byte)(size % 256);
        bytes[8] = (byte) type;
        bytes[9] = (byte) acktarget;
        bytes[10] = (byte)(seq / 2097152);
        bytes[11] = (byte)(seq / 16384);
        bytes[12] = (byte)(seq / 128);
        bytes[13] = (byte)(seq % 128);
        System.arraycopy(data, 0, bytes, headerlength, data.length);
        return bytes;
    }

    public EZPacket(int src) {
        source = src;
    }

    public EZPacket(int src, int target, int type, byte[] data) {
        source = src;
        this.target = target;
        this.data = data;
        this.type = type;
    }


    public EZPacket(int src, int target, int type, int acktarget, int ack,  byte[] data) {
        source = src;
        this.target = target;
        this.data = data;
        this.type = type;
        this.acktarget = acktarget;
        this.ack = ack;
    }

    public EZPacket(byte[] b) {
        setPacket(b);
    }


    //DO NOT USE ANYWHERE except for RecThread
    public EZPacket(DatagramPacket d) {
        int length = d.getData()[6] * 256 + d.getData()[7];
        System.out.println("constructor: " + length);
        setPacket(d.getData(), length);
    }

    public DatagramPacket getDGP() {
        data = Encryption.encrypt(data);
        byte[] bytes = getBytes();
        System.out.println("sending name set: " + getSeq());
        dataPrint();
        System.out.println();
        System.out.println("encryption decrypted: " + new String(Encryption.decrypt(data)));
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
        return  target;
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

    public void setAck(int i) {
        ack = i;
    }

    public int getAck() {
        return ack;
    }

    public void setAcktarget(int i) {
        acktarget = i;
    }

    public int getAcktarget() {
        return acktarget;
    }

    public int getSize() {
        return data.length+headerlength;
    }

    public void dataPrint() {
        for(byte b: data) {
            System.out.print(b + " ");
        }
    }

    public void print() {
        byte[] bytes = getBytes();
        for(byte b: bytes) {
            System.out.print(b + " ");
        }
    }
}
