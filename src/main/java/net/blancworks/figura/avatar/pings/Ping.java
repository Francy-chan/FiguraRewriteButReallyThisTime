package net.blancworks.figura.avatar.pings;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import net.blancworks.figura.avatar.script.lua.types.LuaTable;
import net.blancworks.figura.serving.dealers.backend.messages.MessageSenderContext;
import net.blancworks.figura.utils.ByteBufferExtensions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

//Represents an instance of a single ping, that's either being sent by the client, or has been gotten from the other client.
public class Ping {

    @FunctionalInterface
    public interface ObjectWriter {
        void write(Object obj, LittleEndianDataOutputStream stream) throws Exception;
    }

    @FunctionalInterface
    public interface ObjectReader {
        Object read(LittleEndianDataInputStream stream) throws Exception;
    }

    public static final byte NULL_ID = Byte.MIN_VALUE;
    public static final byte BOOL_ID = NULL_ID + 1;
    public static final byte INT_ID = NULL_ID + 2;
    public static final byte FLOAT_ID = NULL_ID + 3;
    public static final byte STRING_ID = NULL_ID + 4;
    public static final byte TABLE_ID = NULL_ID + 5;


    public static final ImmutableMap<Class<?>, ObjectWriter> writers;
    public static final ImmutableMap<Byte, ObjectReader> readers;

    static {
        ImmutableMap.Builder<Class<?>, ObjectWriter> writerBuilder = new ImmutableMap.Builder<>();
        writerBuilder.put(Boolean.class, (obj, stream) -> writeBoolean((boolean) obj, stream));
        writerBuilder.put(Integer.class, (obj, stream) -> writeInt((int) obj, stream));
        writerBuilder.put(Float.class, (obj, stream) -> writeFloat((float) obj, stream));
        writerBuilder.put(String.class, (obj, stream) -> writeString((String) obj, stream));
        writerBuilder.put(LuaTable.class, (obj, stream) -> writeTable((LuaTable) obj, stream));
        writers = writerBuilder.build();

        ImmutableMap.Builder<Byte, ObjectReader> readerBuilder = new ImmutableMap.Builder<>();
        readerBuilder.put(BOOL_ID, LittleEndianDataInputStream::readByte);
        readerBuilder.put(INT_ID, LittleEndianDataInputStream::readInt);
        readerBuilder.put(FLOAT_ID, LittleEndianDataInputStream::readFloat);
        readerBuilder.put(STRING_ID, ByteBufferExtensions::readString);
        readerBuilder.put(TABLE_ID, Ping::readTable);
        readers = readerBuilder.build();

    }

    private LittleEndianDataOutputStream pingPacket;
    public int slotID;
    public short pingID;
    public Object[] args;

    public void set(int slot, short id, Object[] args) {
        this.slotID = slot;
        this.pingID = id;
        this.args = args;
    }

    public void write(MessageSenderContext ctx) throws Exception {
        //Write packet header
        ctx.writer.writeInt(slotID);
        ctx.writer.writeShort(pingID);

        // -- Construct packet -- //
        pingPacket = new LittleEndianDataOutputStream(new ByteArrayOutputStream());

        //Support up to 128 args.
        byte realArgCount = (byte) args.length;
        pingPacket.writeByte(realArgCount);

        for (int i = 0; i < realArgCount; i++)
            writeObject(args[i], pingPacket);
    }

    public void read(ByteBuffer buffer) throws Exception {
        this.slotID = buffer.getInt();
        this.pingID = buffer.getShort();

        byte argCount = buffer.get();
        args = new Object[argCount];

        byte[] remainingBytes = new byte[buffer.remaining()];
        buffer.get(remainingBytes);

        LittleEndianDataInputStream is = new LittleEndianDataInputStream(new ByteArrayInputStream(remainingBytes));

        for (int i = 0; i < argCount; i++)
            args[i] = readObject(is);
    }

    public static void writeObject(Object obj, LittleEndianDataOutputStream os) throws Exception {
        Class<?> c = obj.getClass();

        ObjectWriter writer = writers.get(c);

        //If no writer, write a null value.
        if (writer == null)
            writeNull(os);
        else
            writer.write(obj, os);
    }

    public static void writeNull(LittleEndianDataOutputStream os) throws IOException {
        os.writeByte(NULL_ID);
    }

    public static void writeBoolean(boolean value, LittleEndianDataOutputStream os) throws IOException {
        os.writeByte(BOOL_ID);
        os.writeBoolean(value);
    }

    public static void writeInt(int amount, LittleEndianDataOutputStream os) throws IOException {
        os.writeByte(INT_ID);
        os.writeInt(amount);
    }

    public static void writeFloat(float amount, LittleEndianDataOutputStream os) throws IOException {
        os.writeByte(FLOAT_ID);
        os.writeFloat(amount);
    }

    public static void writeString(String str, LittleEndianDataOutputStream os) throws Exception {
        os.writeByte(STRING_ID);

        ByteBufferExtensions.writeString(os, str);
    }

    public static void writeTable(LuaTable table, LittleEndianDataOutputStream os) throws Exception {
        os.writeByte(TABLE_ID);

        os.writeInt(table.size());
        for (Map.Entry<Object, Object> entry : table.entrySet()) {
            if (entry.getKey() instanceof String s) {
                writeString(s, os);
                writeObject(entry.getValue(), os);
            } else {
                writeNull(os);
            }
        }
    }

    public static Object readObject(LittleEndianDataInputStream is) throws Exception {
        byte typeID = is.readByte();

        ObjectReader reader = readers.get(typeID);

        if (reader == null)
            return null;
        else
            return reader.read(is);
    }

    public static Map<String, Object> readTable(LittleEndianDataInputStream is) throws Exception {
        HashMap<String, Object> readValues = new HashMap<>();

        int entryCount = is.readInt();
        for (int i = 0; i < entryCount; i++) {
            Object nextKey = readObject(is);

            if (nextKey instanceof String stringKey)
                readValues.put(stringKey, readObject(is));
        }

        return readValues;
    }
}
