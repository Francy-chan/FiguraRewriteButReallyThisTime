package net.blancworks.figura.serving.entity;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.customizations.FiguraCustomizationManager;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.trust.TrustManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class stores the Figura state for a related entity.
 */
public class FiguraMetadata extends FiguraEntityReceiverArray<AvatarHolder> {
    // -- Variables -- //
    public UUID targetID;
    private final Map<Identifier, AvatarHolder> groupsByDealerID = new HashMap<>();

    public final FiguraCustomizationManager entityFinalCustomizations = new FiguraCustomizationManager();

    // -- Constructors -- //

    public FiguraMetadata() {

    }

    public FiguraMetadata(UUID id) {
        this.targetID = id;


        entries = new AvatarHolder[FiguraHouse.registeredDealers.size()];
        for (int i = 0; i < FiguraHouse.registeredDealers.size(); i++) {
            FiguraDealer dealer = FiguraHouse.registeredDealers.get(i);
            var holder = dealer.getHolder(id);

            entries[i] = holder;
            groupsByDealerID.put(dealer.getID(), holder);
        }

        setTrustContainer(TrustManager.get(new Identifier("group", "untrusted")));
    }

    public FiguraMetadata(HashMap<Identifier, AvatarHolder> overrideMap) {
        entries = new AvatarHolder[FiguraHouse.registeredDealers.size() + overrideMap.size()];

        int i = 0;

        for (Map.Entry<Identifier, AvatarHolder> entry : overrideMap.entrySet()) {
            entries[i++] = entry.getValue();
            groupsByDealerID.put(entry.getKey(), entry.getValue());
        }

        for (int j = 0; j < FiguraHouse.registeredDealers.size(); j++) {
            FiguraDealer dealer = FiguraHouse.registeredDealers.get(j);

            entries[i + j] = null;
            groupsByDealerID.put(dealer.getID(), null);
        }
    }

    // -- Functions -- //

    //Gets the avatar in a given slot, from the highest-priority dealer.
    public FiguraAvatar getAvatarFromSlot(int index) {
        for (AvatarHolder holder : entries) {
            if (holder == null) continue;

            var ava = holder.entries[index];

            if (ava != null)
                return ava;
        }

        return null;
    }

    public AvatarHolder getGroupByID(Identifier id) {
        return groupsByDealerID.get(id);
    }
}
