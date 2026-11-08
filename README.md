# Steg

Steg(anography) is a free Java CLI tool for hiding data inside PNG files using the Least Significant Bit (LSB) method.

## Features

- Data encoding into PNG files
- Data decoding from encoded PNG files
- Direct execution of embedded PNG payloads (absolutely unsafe)
- Multiple payload types:
    - CLASS
    - JAR
    - BINARY
- Versioned header format
- Magic signature `STEG`
- Original filename restoration during decoding
- Header validation before payload extraction

---

## Data Format

```text
+---------------------+
| MAGIC   (4 bytes)   |
+---------------------+
| VERSION (1 byte)    |
+---------------------+
| TYPE    (1 byte)    |
+---------------------+
| NAME_LEN (2 bytes)  |
+---------------------+
| NAME    (n bytes)   |
+---------------------+
| P_SIZE  (4 bytes)   |
+---------------------+
| PAYLOAD (n bytes)   |
+---------------------+
```

---

## Principle

Each pixel in a PNG image is composed of four channels:

```text
R G B A
```

The project uses the least significant bit of each RGB component:

```text
RRRRRRRx
GGGGGGGx
BBBBBBBx
```

This provides 3 bits of storage per pixel while producing changes that are usually imperceptible to the human eye.

---

## Maximum Payload Capacity

In theory, the maximum payload size an image can store, in bytes, is:

```text
(width × height × 3) / 8
```

For example, a 1920×1080 PNG image can hide up to 777 600 bytes.

---

## Compilation

From the project root:

```bash
mkdir -p target/classes

javac -d target/classes $(find src/main/java -name "*.java")
```

Then build the executable JAR:

```bash
jar -cfe steg.jar fr.nalrahc.steg.Main -C target/classes .
```

---

## Usage

### Encoding

Hide a text file inside `Boston.png` and produce `Boston_with_LICENSE.png`:

```bash
java -jar steg.jar encode LICENSE Boston.png Boston_with_LICENSE.png
```

Hide a Java class inside `Boston.png` and produce `Encoded_Boston.png`:

```bash
java -jar steg.jar encode MyPayload.class Boston.png Encoded_Boston.png
```

---

### Decoding

Extract the embedded payload from an encoded PNG:

```bash
java -jar steg.jar decode Boston_with_LICENSE.png
```

The payload will be restored using its original filename stored in the header.

---

### Running

> Warning: executing unknown code hidden inside a PNG file is generally a very bad idea.

This is roughly equivalent to:

```bash
curl www.random.org | sh
```

If you still want to do it:

```bash
java -jar steg.jar run Boston_with_LICENSE.png
```

Depending on the payload type:

- `BINARY`: its contents are printed to standard output.
- `CLASS`: its `main()` method is executed.
- `JAR`: it is extracted into a temporary directory and executed.

---

## Packaging

If you want to distribute an application composed of an encoded PNG file and `steg.jar`, you can use:

```bash
jpackage \
  --type app-image \
  --name MyApp \
  --input . \
  --main-jar steg.jar \
  --arguments "run app/Encoded.png"
```