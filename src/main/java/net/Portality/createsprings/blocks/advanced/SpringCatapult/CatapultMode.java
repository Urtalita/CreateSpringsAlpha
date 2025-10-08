package net.Portality.createsprings.blocks.advanced.SpringCatapult;

import net.minecraft.nbt.CompoundTag;

public enum CatapultMode {
    SHOOTING("outputting"),
    CANSHOOT("canShoot"),
    INPUTTING("inputting"),
    RAVE("rave"),
    NO_TARGET("no_target"),
    WAITING("waiting");

    public String name;

    CatapultMode(String name){
        this.name = name;
    }

    public static CatapultMode deSerialize(CompoundTag tag, String name){
        String index = tag.getString(name);
        CatapultMode[] values = CatapultMode.values();
        for (CatapultMode value : values){
            if(value.name.equals(index)){
                return value;
            }
        }
        return null;
    }
}
