package net.Portality.createsprings.Items.advanced.hat;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.CspringsArmorMaterials;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringBase.SpringBaseRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class HatItem extends Item {
    public HatItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new HatRenderer()));
    }
}
