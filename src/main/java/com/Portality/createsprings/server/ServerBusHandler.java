package com.Portality.createsprings.server;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.utill.test.CSpringsGameTests;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

@EventBusSubscriber(modid = CreateSprings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ServerBusHandler {
    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        event.register(CSpringsGameTests.class);
    }
}
