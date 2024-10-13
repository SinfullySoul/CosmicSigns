package com.github.sinfullysoul.mixins;

import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockEntity.class)
public interface BlockEntityInterface {
    @Accessor("zone") public Zone getZone();
}
