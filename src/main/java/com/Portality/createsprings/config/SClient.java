package com.Portality.createsprings.config;

import net.createmod.catnip.config.ConfigBase;

@SuppressWarnings({"all"})
public class SClient extends ConfigBase {
    public final ConfigGroup client = group(0, "client",
            Comments.client);

    @Override
    public String getName() {return "client";}

    private static class Comments {
        static String client =
                "Client-only settings - If you're looking for general settings, look inside your worlds serverconfig folder!";

    }
}
