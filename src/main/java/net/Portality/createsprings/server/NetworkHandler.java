package net.Portality.createsprings.server;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("createsprings", "main2"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++,
                PunchcardUpdatePacket.class,
                PunchcardUpdatePacket::encode,
                PunchcardUpdatePacket::new,
                PunchcardUpdatePacket::handle);

        CHANNEL.registerMessage(id++,
                PortativeSteamEngineUpdatePacket.class,
                PortativeSteamEngineUpdatePacket::encode,
                PortativeSteamEngineUpdatePacket::new,
                PortativeSteamEngineUpdatePacket::handle);
    }
}
