package net.Portality.createsprings.recipe.Welding;

public enum WelderRecipeSpeed {
    FAST(4),
    NORMAL(3),
    SLOW(2);

    private final int speedValue;

    WelderRecipeSpeed(int speedValue) {
        this.speedValue = speedValue;
    }

    public int getSpeedValue() {
        return speedValue;
    }
}
