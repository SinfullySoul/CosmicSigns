package com.github.sinfullysoul.mixins;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.github.sinfullysoul.api.IRenderable;
import com.github.sinfullysoul.block_entities.ZoneBlockEntityRenderInterface;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.world.Region;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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
