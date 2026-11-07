package fr.nalrahc.steg.decode;

public class PngClassLoader extends ClassLoader {
    public DecodedData data;

    public PngClassLoader(ClassLoader parent, DecodedData data) {
        super(parent);
        this.data = data;
    }

    @Override
    protected Class<?> findClass(String name) {
        try {
            byte[] classBytes = data.payload();
            return defineClass(name, classBytes, 0, classBytes.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
