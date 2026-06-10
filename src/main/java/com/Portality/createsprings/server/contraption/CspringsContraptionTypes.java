package com.Portality.createsprings.server.contraption;

import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

import static com.simibubi.create.AllContraptionTypes.BY_LEGACY_NAME;

public class CspringsContraptionTypes {
    public static final Holder.Reference<ContraptionType> SPRING = register("spring", SpringContraption::new);

    private static Holder.Reference<ContraptionType> register(String name, Supplier<? extends Contraption> factory) {
        ContraptionType type = new ContraptionType(factory);
        BY_LEGACY_NAME.put(name, type);

        return Registry.registerForHolder(CreateBuiltInRegistries.CONTRAPTION_TYPE, Create.asResource(name), type);
    }

    public static void init() {}
}
