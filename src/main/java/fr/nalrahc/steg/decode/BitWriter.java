package fr.nalrahc.steg.decode;

import java.io.ByteArrayOutputStream;

public final class BitWriter {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private int currentByte;
    private int bitCount;

    public void writeBit(int bit) {
        currentByte = (currentByte << 1) | bit;
        bitCount++;
        if (bitCount == 8) {
            out.write(currentByte);
            currentByte = 0;
            bitCount = 0;
        }
    }

    public byte[] toByteArray() {
        return out.toByteArray();
    }
}