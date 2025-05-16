package net.Portality.createsprings.utill.mixins;

import net.minecraft.world.item.ItemStack;

public interface CshatAccessor {
    boolean getCshat();
    void setCshat(boolean value);

    ItemStack getCshatStack();
    void setCshatStack(ItemStack value);
}
