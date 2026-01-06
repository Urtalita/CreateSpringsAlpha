package net.Portality.createsprings.Items.SpringStufs;

public interface ISpringPoweredTool {
    SpringPoweredCore getCore();

    default boolean hasSpeedSystem(){
        return false;
    }
}
