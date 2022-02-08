package net.blancworks.figura.serving.dealers.backend;

import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.serving.dealers.backend.network.FiguraNetworkManager;
import net.blancworks.figura.serving.entity.AvatarGroup;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class FiguraBackendDealer extends FiguraDealer {
    // -- Variables -- //
    private static final Identifier ID = new Identifier("figura", "backend");

    private final FiguraNetworkManager networkManager = new FiguraNetworkManager();


    // -- Functions -- //
    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public void tick() {
        super.tick();
        networkManager.tick();
    }

    @Override
    protected <T extends Entity> void requestForEntity(AvatarGroup group, T entity) {

    }
}
