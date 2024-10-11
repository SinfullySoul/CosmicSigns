package com.github.sinfullysoul.block_entities;

import com.badlogic.gdx.utils.Array;
import finalforeach.cosmicreach.blockentities.BlockEntity;


//The interface cant be in mixin package
public interface ZoneBlockEntityRenderInterface {
    //public Array<TextModelInstance> allTextModels = new Array<>();

    public default void addRenderableBlockEntity(BlockEntity text) {

    }
    public default void removeRenderableBlockEntity(BlockEntity text) {

    }
    public default Array<BlockEntity> getRenderableBlockEntities(){

        return null;
    }
}
