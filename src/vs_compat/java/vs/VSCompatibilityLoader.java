package vs;

import net.minecraftforge.common.MinecraftForge;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

public class VSCompatibilityLoader {
    public static void load() {
        ValkyrienSkiesMod.getApi().registerAttachment(SpringForceAttachment.class);
        MinecraftForge.EVENT_BUS.addListener(CSpringsVsCompatibility::onSpringSplash);
    }
}
