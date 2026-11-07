package fr.nalrahc.steg.decode;

import fr.nalrahc.steg.common.PayloadType;

public record DecodedData(int version, PayloadType payloadType, String filename, int payloadSize, byte[] payload) {
    public String rawName() {
        int extensionSize = 0;
        switch (payloadType) {
            case JAR -> extensionSize = 4;
            case CLASS -> extensionSize = 6;
        }
        return filename.substring(0, filename.length() - extensionSize);
    }
}
