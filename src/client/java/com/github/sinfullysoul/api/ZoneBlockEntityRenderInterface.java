package com.github.sinfullysoul.api;

import com.badlogic.gdx.utils.Array;
import finalforeach.cosmicreach.blockentities.BlockEntity;


//The interface cant be in mixin package
public interface ZoneBlockEntityRenderInterface {

    public default void addRenderableBlockEntity(BlockEntity text) {

    }
    public default void removeRenderableBlockEntity(BlockEntity text) {

    }
    public default Array<BlockEntity> getRenderableBlockEntities(){

        return null;
    }
}
