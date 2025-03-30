package net.Portality.createsprings.recipe.Welding;

public enum WelderRecipeSpeed {
    FAST(3),
    NORMAL(2),
    SLOW(1);

    private final int speedValue;

    WelderRecipeSpeed(int speedValue) {
        this.speedValue = speedValue;
    }

    public int getSpeedValue() {
        return speedValue;
    }
}
