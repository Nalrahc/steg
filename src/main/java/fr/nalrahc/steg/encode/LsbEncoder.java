package fr.nalrahc.steg.encode;

import fr.nalrahc.steg.common.PayloadType;
import fr.nalrahc.steg.common.StegHeader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class LsbEncoder {

    public static void encode(String payloadPath, String coverImagePath, String outputImagePath) throws Exception {
        File payloadFile = new File(payloadPath);
        String filename = payloadFile.getName();
        byte[] filenameBytes = filename.getBytes(StandardCharsets.UTF_8);

        PayloadType type = detectType(filename);

        byte[] payload = Files.readAllBytes(payloadFile.toPath());

        byte[] header = ByteBuffer.allocate(4 + 1 + 1 + 2 + filenameBytes.length + 4)
                .put(StegHeader.MAGIC)
                .put(StegHeader.VERSION)
                .put((byte) type.value())
                .putShort((short) filenameBytes.length)
                .put(filenameBytes)
                .putInt(payload.length)
                .array();

        byte[] content = new byte[header.length + payload.length];

        System.arraycopy(header, 0, content, 0, header.length);
        System.arraycopy(payload, 0, content, header.length, payload.length);

        BufferedImage source = ImageIO.read(new File(coverImagePath));
        BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(source, 0, 0, null);
        graphics.dispose();

        long capacity = ((long) image.getWidth() * image.getHeight() * 3) / 8;

        if (content.length > capacity) {
            throw new IllegalArgumentException("Image too small");
        }

        BitReader bits = new BitReader(content);

        boolean finished = false;
        for (int y = 0; y < image.getHeight() && !finished; y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int a = (rgb >>> 24) & 0xFF;
                int r = (rgb >>> 16) & 0xFF;
                int g = (rgb >>> 8) & 0xFF;
                int b = rgb & 0xFF;

                if (bits.hasNext()) {
                    r = (r & 0xFE) | bits.nextBit();
                }

                if (bits.hasNext()) {
                    g = (g & 0xFE) | bits.nextBit();
                }

                if (bits.hasNext()) {
                    b = (b & 0xFE) | bits.nextBit();
                }

                int packed = (a << 24) | (r << 16) | (g << 8) | b;

                image.setRGB(x, y, packed);

                if (!bits.hasNext()) {
                    finished = true;
                    break;
                }
            }
        }

        ImageIO.write(image, "png", new File(outputImagePath));
        System.out.println("Encoded data on : " + filename);
    }

    private static PayloadType detectType(String filename) {
        String lower = filename.toLowerCase();

        if (lower.endsWith(".class")) {
            return PayloadType.CLASS;
        }

        if (lower.endsWith(".jar")) {
            return PayloadType.JAR;
        }

        return PayloadType.BINARY;
    }
}