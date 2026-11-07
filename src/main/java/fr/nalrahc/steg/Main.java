package fr.nalrahc.steg;

import fr.nalrahc.steg.decode.DecodedData;
import fr.nalrahc.steg.decode.LsbDecoder;
import fr.nalrahc.steg.decode.PngClassLoader;
import fr.nalrahc.steg.encode.LsbEncoder;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class Main {

    static void main(String[] args) throws Exception {
        if (args.length == 0) {
            usage();
            return;
        }

        switch (args[0]) {
            case "encode" -> {
                if (args.length != 4) {
                    usage();
                    return;
                }

                LsbEncoder.encode(args[1], args[2], args[3]);
            }

            case "decode" -> {
                if (args.length != 2) {
                    usage();
                    return;
                }

                LsbDecoder.decodeAndWrite(args[1]);
            }

            case "run" -> {
                if (args.length != 2) {
                    usage();
                    return;
                }

                DecodedData data = LsbDecoder.decodeData(args[1]);
                switch (data.payloadType()) {
                    case JAR -> {
                        Path tempJar = Files.createTempFile("steg-", ".jar");
                        tempJar.toFile().deleteOnExit();
                        Files.write(tempJar, data.payload());
                        Process process = new ProcessBuilder("java", "-jar", tempJar.toString()).inheritIO().start();
                        process.waitFor();
                    }
                    case CLASS -> {
                        PngClassLoader classLoader = new PngClassLoader(Main.class.getClassLoader(), data);
                        Class<?> decodedClass = classLoader.loadClass(classLoader.data.rawName());
                        Method mainMethod = Arrays.stream(decodedClass.getDeclaredMethods()).filter(
                                method -> "main".equals(method.getName())).findAny().orElseThrow();
                        mainMethod.invoke(null, (Object) new String[]{});
                    }
                    case BINARY -> System.out.write(data.payload());
                }
            }

            default -> usage();
        }
    }

    private static void usage() {
        System.out.println("""
                Usage:
                
                  encode:
                  steg encode payload.class cover.png hidden.png
                
                  decode:
                  steg decode hidden.png
                
                  run:
                  steg run hidden.png
                """);
    }
}