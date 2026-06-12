package com.Portality.createsprings.compat;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockEntity;
import com.Portality.createsprings.blocks.advanced.spring.SpringBlock;
import com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity;
import com.Portality.createsprings.blocks.displaySource.CSpringsDisplaySources;
import com.Portality.createsprings.items.advanced.Spring.SpringItem;
import com.Portality.sableCompat.SableCompatLargeSpring;
import com.Portality.sableCompat.SableCompatSpring;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

import static com.Portality.createsprings.CreateSprings.CSPRINGS_REGISTRATE;
import static com.simibubi.create.api.behaviour.display.DisplaySource.displaySource;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class SableCompatAbstractionLayer {
    public static boolean isLoaded(){
        return ModList.get().isLoaded("sable");
    }

    public static void pushSubLevels(SpringBlockEntity be){
        if(isLoaded()){
            SableCompatSpring.pushSubLevels(be);
        }
    }

    public static void pushCreatedSubLevels(SpringBlockEntity be){
        if(isLoaded()){
            SableCompatSpring.pushCreatedSubLevels(be);
        }
    }

    public static boolean launchEntitiesInFront(SpringBlockEntity be, Vec3 vector){
        if(isLoaded()){
            return SableCompatSpring.launchEntitiesInFront(be, vector);
        }
        return false;
    }

    public static void registerSpringBlock(){
        if(isLoaded()){
            SableCompatSpring.registerSpring();
            return;
        }

        CSpringsBlocks.SPRING = CSPRINGS_REGISTRATE.block("spring", SpringBlock::new)
                .initialProperties(SharedProperties::copperMetal)
                .transform(pickaxeOnly())
                .properties(p -> p.noOcclusion().isRedstoneConductor((s, l, pos) -> false))
                .transform(displaySource(CSpringsDisplaySources.CHARGE))
                .item(SpringItem::new)
                .build()
                .blockstate(BlockStateGen.directionalBlockProvider(false))
                //.onRegister(movementBehaviour(new SpringMovement()))
                .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                .register();
    }

    public static void pushSubLevels(LargeSpringBlockEntity largeSpringBlockEntity) {
        if(isLoaded()){
            SableCompatLargeSpring.pushSubLevels(largeSpringBlockEntity);
        }
    }
}
