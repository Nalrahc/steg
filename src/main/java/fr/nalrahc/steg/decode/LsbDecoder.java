package fr.nalrahc.steg.decode;

import fr.nalrahc.steg.common.PayloadType;
import fr.nalrahc.steg.common.StegHeader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class LsbDecoder {

    public static void decodeAndWrite(String imagePath) throws Exception {
        DecodedData data = decodeData(imagePath);
        Files.write(new File(data.filename()).toPath(), data.payload());
        System.out.println("Data : " + data);
    }

    public static DecodedData decodeData(String imagePath) throws Exception {
        BufferedImage image = ImageIO.read(new File(imagePath));

        BitWriter bits = new BitWriter();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);

                int r = (rgb >>> 16) & 0xFF;
                int g = (rgb >>> 8) & 0xFF;
                int b = rgb & 0xFF;

                bits.writeBit(r & 1);
                bits.writeBit(g & 1);
                bits.writeBit(b & 1);
            }
        }

        byte[] raw = bits.toByteArray();

        verifyMagic(raw);

        int offset = 4;

        int version = raw[offset++] & 0xFF;

        if (version != StegHeader.VERSION) {
            throw new IllegalStateException("Unsupported version: " + version);
        }

        PayloadType type = PayloadType.fromValue(raw[offset++] & 0xFF);

        int filenameLength = ByteBuffer.wrap(raw, offset, 2).getShort() & 0xFFFF;

        offset += 2;

        if (offset + filenameLength > raw.length) {
            throw new IllegalStateException("Invalid filename length");
        }

        String filename = new String(raw, offset, filenameLength, StandardCharsets.UTF_8);

        offset += filenameLength;

        if (offset + 4 > raw.length) {
            throw new IllegalStateException("Missing payload size");
        }

        int payloadSize = ByteBuffer.wrap(raw, offset, 4).getInt();

        offset += 4;

        if (payloadSize < 0) {
            throw new IllegalStateException("Negative payload size");
        }

        if (offset + payloadSize > raw.length) {
            throw new IllegalStateException("Corrupted payload");
        }

        byte[] payload = new byte[payloadSize];

        System.arraycopy(raw, offset, payload, 0, payloadSize);

        return new DecodedData(version, type, filename, payloadSize, payload);
    }

    private static void verifyMagic(byte[] data) {
        for (int i = 0; i < StegHeader.MAGIC.length; i++) {
            if (data[i] != StegHeader.MAGIC[i]) {
                throw new IllegalStateException("Missing STEG signature");
            }
        }
    }
}