package net.Portality.createsprings.server.packets;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.Portality.createsprings.Items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class PSEClientUpdate extends SimplePacketBase {
    private final CompoundTag updatedTag;

    public PSEClientUpdate(CompoundTag updatedTag) {
        this.updatedTag = updatedTag;
    }

    public PSEClientUpdate(FriendlyByteBuf buf) {
        this.updatedTag = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(updatedTag);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                Entity entity = Minecraft.getInstance().player;
                if(entity instanceof Player player){
                    if(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PortativeSteamEngineItem){
                        player.getItemBySlot(EquipmentSlot.CHEST).setTag(updatedTag);
                    }
                }
            }
        }));
        return true;
    }
}
