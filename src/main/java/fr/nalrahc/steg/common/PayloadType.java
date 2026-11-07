package fr.nalrahc.steg.common;

public enum PayloadType {
    CLASS(1),
    JAR(2),
    BINARY(3);

    private final int value;

    PayloadType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static PayloadType fromValue(int value) {
        for (PayloadType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown payload type: " + value);
    }
}