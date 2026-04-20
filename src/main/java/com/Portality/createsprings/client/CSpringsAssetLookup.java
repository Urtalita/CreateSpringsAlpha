package com.Portality.createsprings.client;

import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class CSpringsAssetLookup {
    public static ModelFile customBlockModel(String string, RegistrateBlockstateProvider prov) {
        return prov.models()
                .getExistingFile(prov.modLoc("block/" + string));
    }
}
