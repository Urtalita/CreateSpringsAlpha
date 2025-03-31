package net.Portality.createsprings.datagen;

import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CreateSprings.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.PUNCHCARD);
        simpleItem(ModItems.SPRING_ALLOY);
        simpleItem(ModItems.SPRING_ALLOY_BUCKET);
        simpleItem(ModItems.SPRING_ALLOY_SHEET);
        simpleItem(ModItems.SPRING_ALLOY_NUGGET);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(CreateSprings.MODID,"item/" + item.getId().getPath()));
    }
}
