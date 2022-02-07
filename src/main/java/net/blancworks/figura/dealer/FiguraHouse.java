package net.blancworks.figura.dealer;

import net.blancworks.figura.dealer.entity.FiguraEntityMetadata;
import net.minecraft.entity.Entity;

/**
 * Main class that holds all the dealers, which give avatars out based on UUID and such.
 */
public class FiguraHouse {

    //List of all the dealers currently set up for Figura.
    public static final FiguraDealer[] dealers = new FiguraDealer[]{
            new FiguraLocalDealer()
    };

    public static void init(){
        for (int i = 0; i < dealers.length; i++)
            dealers[i].id = i;
    }

    /**
     * Creates an entity metadata for a given entity, and puts in requests for the entity's avatar.
     */
    public static FiguraEntityMetadata createEntityMetadata(Entity e) {
        FiguraEntityMetadata metadata = new FiguraEntityMetadata();

        for (int i = 0; i < dealers.length; i++) {
            FiguraDealer dealer = dealers[i];
            metadata.avatarsByID[i] = dealer.getAvatar(e);
        }

        return metadata;
    }
}
