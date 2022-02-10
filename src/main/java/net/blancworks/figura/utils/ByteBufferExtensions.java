package net.blancworks.figura.utils;

import com.google.common.io.LittleEndianDataOutputStream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteBufferExtensions {

    //Reads a string from input
    public static String readString(ByteBuffer bytes) {
        //We use a roundabout method because Java encodes/decodes strings funny sometimes.
        int byteCount = bytes.getInt();
        byte[] idBytes = new byte[byteCount];
        bytes.get(idBytes);
        return new String(idBytes, StandardCharsets.UTF_8);
    }

    //Writes a string to output
    public static void writeString(LittleEndianDataOutputStream dos, String s) throws IOException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(bytes.length);
        dos.write(bytes);
    }
}
