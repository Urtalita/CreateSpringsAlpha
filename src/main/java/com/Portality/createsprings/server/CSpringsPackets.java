package com.Portality.createsprings.server;
import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.server.packets.CatapultPlacementPacket;
import com.Portality.createsprings.server.packets.PunchcardUpdatePacket;
import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.CreateBuildInfo;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public enum CSpringsPackets implements BasePacketPayload.PacketTypeProvider {

    PUNCHCARD_UPDATE(PunchcardUpdatePacket.class, PunchcardUpdatePacket.STREAM_CODEC),
    CATAPULT_TARGET(CatapultPlacementPacket.class, CatapultPlacementPacket.STREAM_CODEC),
    CATAPULT_TARGET_CLIENT(CatapultPlacementPacket.ClientBoundRequest.class, CatapultPlacementPacket.ClientBoundRequest.STREAM_CODEC)
    ;

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> CSpringsPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(Create.asResource(name)),
                clazz, codec
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(Create.ID, CreateBuildInfo.VERSION);
        for (CSpringsPackets packet : CSpringsPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}
