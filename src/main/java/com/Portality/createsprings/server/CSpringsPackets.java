package com.Portality.createsprings.server;
import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.server.packets.*;
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
    PSE_BOOST(BoostPSEPacket.class, BoostPSEPacket.STREAM_CODEC),
    PSE_DASH(DashPSEPacket.class, DashPSEPacket.STREAM_CODEC),
    PSE_RELEASE(ReleasePSEPacket.class, ReleasePSEPacket.STREAM_CODEC),
    PSE_OPEN(OpenPSEPacket.class, OpenPSEPacket.STREAM_CODEC),

    ACTIVATE_PUNCHCARD(ActivatePunchcard.class, ActivatePunchcard.STREAM_CODEC),
    CATAPULT_TARGET(CatapultPlacementPacket.class, CatapultPlacementPacket.STREAM_CODEC),
    CATAPULT_TARGET_CLIENT(CatapultPlacementPacket.ClientBoundRequest.class, CatapultPlacementPacket.ClientBoundRequest.STREAM_CODEC),
    PSE_SERVER_UPDATE(PortativeSteamEngineUpdatePacket.class, PortativeSteamEngineUpdatePacket.STREAM_CODEC),

    PSE_CLIENT_UPDATE(PSEClientUpdate.class, PSEClientUpdate.STREAM_CODEC),
    AIR_DASH(AirDashPlayerPacket.class, AirDashPlayerPacket.STREAM_CODEC),
    PUSH_OFF_PACKET(PushOffPacket.class, PushOffPacket.STREAM_CODEC),
    GRAB_PUNCHCARD(GrabPunchcard.class, GrabPunchcard.STREAM_CODEC),
    ROTATE_PLAYER(RotatePlayerPacket.class, RotatePlayerPacket.STREAM_CODEC),
    PSKI_SPRING_UPDATE(PSKISpringUpdate.class, PSKISpringUpdate.STREAM_CODEC)
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
