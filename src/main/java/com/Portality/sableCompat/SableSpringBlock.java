package com.Portality.sableCompat;

import com.Portality.createsprings.blocks.advanced.spring.SpringBlock;
import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;

public class SableSpringBlock extends SpringBlock implements BlockWithSubLevelCollisionCallback {
    public SableSpringBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockSubLevelCollisionCallback sable$getCallback() {
        return SplashCallback.INSTANCE;
    }
}
