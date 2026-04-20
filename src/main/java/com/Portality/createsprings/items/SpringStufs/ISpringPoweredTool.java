package com.Portality.createsprings.items.SpringStufs;

public interface ISpringPoweredTool {
    SpringPoweredCore getCore();

    default boolean hasSpeedSystem(){
        return false;
    }
}
