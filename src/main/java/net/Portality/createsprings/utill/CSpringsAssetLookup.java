package net.Portality.createsprings.utill;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraftforge.client.model.generators.ModelFile;

public class CSpringsAssetLookup {
    public static ModelFile customBlockModel(String string, RegistrateBlockstateProvider prov) {
        return prov.models()
                .getExistingFile(prov.modLoc("block/" + string));
    }
}
