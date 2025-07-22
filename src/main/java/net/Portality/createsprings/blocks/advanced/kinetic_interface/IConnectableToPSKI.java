package net.Portality.createsprings.blocks.advanced.kinetic_interface;

public interface IConnectableToPSKI {
    float getStored();
    float getCapacity();
    void setStored(float newStored);
    float getHardness();
    float getImpactCof();

    default void handelUnchanging(float Speed){
        setStored(Math.max(0, getStored() - 1000 * Speed));
    }
}
