package com.github.sinfullysoul.mixins;

import com.badlogic.gdx.utils.Array;
import com.github.sinfullysoul.api.ZoneBlockEntityRenderInterface;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.util.ArrayUtils;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zone.class)
public abstract class ZoneMixin implements  ZoneBlockEntityRenderInterface {

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
