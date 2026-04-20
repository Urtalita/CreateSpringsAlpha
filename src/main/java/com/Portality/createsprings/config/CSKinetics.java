package com.Portality.createsprings.config;

import net.createmod.catnip.config.ConfigBase;

public class CSKinetics extends ConfigBase {

    public final CSStress stressValues = nested(1, CSStress::new, Comments.stress);

    @Override
    public String getName() {
        return "kinetics";
    }

    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";
    }
}
