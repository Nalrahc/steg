package fr.nalrahc.steg.common;

import java.nio.charset.StandardCharsets;

public final class StegHeader {

    public static final byte[] MAGIC = "STEG".getBytes(StandardCharsets.US_ASCII);
    public static final byte VERSION = 1;
}