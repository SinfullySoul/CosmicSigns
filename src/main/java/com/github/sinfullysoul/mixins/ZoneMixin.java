package com.github.sinfullysoul.mixins;

import com.badlogic.gdx.utils.Array;
import com.github.sinfullysoul.block_entities.ZoneBlockEntityRenderInterface;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.util.ArrayUtils;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zone.class)
public abstract class ZoneMixin implements  ZoneBlockEntityRenderInterface {


    //already have a renderable array so might as well use it to update the signs
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lfinalforeach/cosmicreach/world/Zone;runScheduledTriggers()V",shift = At.Shift.AFTER))
    private void updateRenderAbleBlockEntitiesInject(CallbackInfo ci) {
        ArrayUtils.forEach(this.allRenderableBlockEntities, BlockEntity::onTick);
    }
    public Array<BlockEntity> allRenderableBlockEntities = new Array<>();

    @Override
    public  void addRenderableBlockEntity(BlockEntity be) {
        this.allRenderableBlockEntities.add(be);
    }
    @Override
    public  void removeRenderableBlockEntity(BlockEntity be) {
        this.allRenderableBlockEntities.removeValue(be, true);
    }
    @Override
    public  Array<BlockEntity> getRenderableBlockEntities(){
        return this.allRenderableBlockEntities;
    }
}
