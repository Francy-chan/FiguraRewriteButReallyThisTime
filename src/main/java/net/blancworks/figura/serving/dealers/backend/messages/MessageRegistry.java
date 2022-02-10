package net.blancworks.figura.serving.dealers.backend.messages;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.utils.ByteBufferExtensions;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageRegistry {
    // -- Variables -- //
    private static final List<String> idToName = new ArrayList<>();
    private static final HashMap<String, Integer> nameToID = new HashMap<>();


    // -- Functions -- //
    public static void clear(){
        idToName.clear();
        nameToID.clear();

        idToName.add(MessageNames.MESSAGE_REGISTRY_INIT);
        nameToID.put(MessageNames.MESSAGE_REGISTRY_INIT, 0);
    }

    public static void readRegistry(ByteBuffer buffer) {
        int count = buffer.getInt();

        //Get up to 1024 unique message types
        for(int i = 1; i < 1024 && i < count; i++){
            String s = ByteBufferExtensions.readString(buffer);

            FiguraMod.LOGGER.info("Message name " + s + " was given numerical ID " + idToName.size());

            nameToID.put(s, idToName.size());
            idToName.add(s);
        }
    }


    public static Integer tryGetID(String name) {
        return nameToID.get(name);
    }

    public static String tryGetName(int id){
        return idToName.get(id);
    }

}
