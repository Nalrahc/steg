package fr.nalrahc.steg.encode;

public final class BitReader {

    private final byte[] data;
    private int bitIndex;

    public BitReader(byte[] data) {
        this.data = data;
    }

    public boolean hasNext() {
        return bitIndex < data.length * 8;
    }

    public int nextBit() {
        int byteIndex = bitIndex / 8;
        int offset = 7 - (bitIndex % 8);
        bitIndex++;
        return (data[byteIndex] >> offset) & 1;
    }
}