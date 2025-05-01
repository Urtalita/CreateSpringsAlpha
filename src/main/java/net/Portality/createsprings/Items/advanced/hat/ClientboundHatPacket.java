package net.Portality.createsprings.Items.advanced.hat;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.UUID;

public class ClientboundHatPacket extends SimplePacketBase {
    private final Collection<UUID> uuids;

    public ClientboundHatPacket(Collection<UUID> uuids) {
        this.uuids = uuids;
    }

    public ClientboundHatPacket(FriendlyByteBuf buffer) {
        this.uuids = buffer.readList(FriendlyByteBuf::readUUID);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeCollection(uuids, FriendlyByteBuf::writeUUID);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            PlayerHatRenderer.updatePlayerList(this.uuids);
        });
        return true;
    }
}
